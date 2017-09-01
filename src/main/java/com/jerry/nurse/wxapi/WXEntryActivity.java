package com.jerry.nurse.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.app.MyApplication;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.model.ThirdPartInfo;
import com.jerry.nurse.model.WeChat;
import com.jerry.nurse.model.WeChatTokenResult;
import com.jerry.nurse.model.WeChatUserInfoResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LoginManager;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.zhy.http.okhttp.OkHttpUtils;

import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    public static final int WE_CHAT_RESULT = 0x110;
    public static final String EXTRA_WE_CHAT = "extra_we_chat";

    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    private static final int RETURN_MSG_TYPE_SHARE = 2;

    private ProgressDialogManager mProgressDialogManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mProgressDialogManager = new ProgressDialogManager(this);
        //如果没回调onResp，八成是这句没有写
        MyApplication.sWxApi.handleIntent(getIntent(), this);
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
    }

    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    //app发送消息给微信，处理返回消息的回调
    @Override
    public void onResp(BaseResp resp) {
        L.i("错误码 : " + resp.errCode);
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == resp.getType()) {
                    T.showShort(WXEntryActivity.this, "分享失败");
                } else {
                    T.showShort(WXEntryActivity.this, "登录失败");
                }
                finish();
                break;
            // 请求成功
            case BaseResp.ErrCode.ERR_OK:
                switch (resp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        // 拿到了微信返回的code,立马再去请求access_token
                        String code = ((SendAuth.Resp) resp).code;
                        L.i("code = " + code);

                        mProgressDialogManager.show();
                        OkHttpUtils.get().url(ServiceConstant.WECHAT_GET_TOKEN)
                                .addParams("appid", ServiceConstant.WX_APP_ID)
                                .addParams("secret", ServiceConstant.WX_APP_SECRET)
                                .addParams("code", code)
                                .addParams("grant_type", "authorization_code")
                                .build()
                                .execute(new FilterStringCallback(mProgressDialogManager) {

                                    @Override
                                    public void onFilterResponse(String response, int id) {
                                        WeChatTokenResult result = new Gson().fromJson(response, WeChatTokenResult.class);
                                        if (result.getExpires_in() == 7200) {
                                            // 读取成功后获取用户的信息
                                            getUserInfo(result.getAccess_token(), result.getOpenid());
                                        } else {
                                            T.showShort(WXEntryActivity.this, R.string.login_failed);
                                            finish();
                                        }
                                    }
                                });
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        T.showShort(WXEntryActivity.this, "微信分享成功");
                        finish();
                        break;
                }
                break;
        }
    }

    /**
     * 获取微信用户基本信息
     *
     * @param accessToken
     * @param openId
     */
    private void getUserInfo(String accessToken, String openId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.WECHAT_GET_USER_INFO)
                .addParams("access_token", accessToken)
                .addParams("openid", openId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        // 获取用户信息后组装成我们平台的类，进行登录(注册)
                        WeChatUserInfoResult result = new Gson().fromJson(response, WeChatUserInfoResult.class);
                        WeChat weChat = new WeChat();
                        weChat.setOpenId(result.getOpenid());
                        weChat.setNickName(result.getNickname());
                        weChat.setSex(result.getSex());
                        weChat.setLanguage(result.getLanguage());
                        weChat.setCity(result.getCity());
                        weChat.setProvince(result.getProvince());
                        weChat.setCountry(result.getCountry());
                        weChat.setHeadImgurl(result.getHeadimgurl());
                        String activityName = ActivityCollector.getTopActivity().getLocalClassName();
                        // 如果是登录界面就进行登录(注册)
                        if (activityName.equals("activity.LoginActivity")) {
                            postWeChatLogin(weChat);
                        }
                        // 如果是设置界面就进行绑定
                        else if (activityName.equals("activity.SettingActivity")) {
                            bindWeChat(weChat);
                        }
                    }
                });
    }


    /**
     * 微信登录(注册)
     *
     * @param weChat
     */
    private void postWeChatLogin(WeChat weChat) {
        // 设置极光推送Id
        weChat.setDeviceRegId(JPushInterface.getRegistrationID(this));
        OkHttpUtils.postString()
                .url(ServiceConstant.LOGIN_BY_WX)
                .content(StringUtil.addModelWithJson(weChat))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        LoginInfoResult loginInfoResult = new Gson().fromJson(response, LoginInfoResult.class);
                        if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
                            // 使用登录管理器登录成功后保存登录信息
                            LoginManager loginManager = new LoginManager(WXEntryActivity.this, null);
                            loginManager.saveAndEnter(loginInfoResult.getBody());
                        } else {
                            T.showShort(WXEntryActivity.this, loginInfoResult.getMsg());
                        }
                        finish();
                    }
                });
    }

    /**
     * 绑定微信
     *
     * @param weChat
     */
    private void bindWeChat(WeChat weChat) {
        String registerId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
        thirdPartInfo.setRegisterId(registerId);
        thirdPartInfo.setWXData(weChat);
        OkHttpUtils.postString()
                .url(ServiceConstant.BIND)
                .content(StringUtil.addModelWithJson(thirdPartInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            T.showShort(WXEntryActivity.this, "微信绑定成功");
                            L.i("微信绑定成功");
                            // 获取用户所有绑定信息
                        } else {
                            T.showShort(WXEntryActivity.this, commonResult.getMsg());
                        }
                        finish();
                    }
                });
    }


}