package com.jerry.nurse.util;

import android.content.Context;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.model.WeChat;
import com.jerry.nurse.model.WeChatTokenResult;
import com.jerry.nurse.model.WeChatUserInfoResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

/**
 * Created by Jerry on 2017/8/29.
 */

public class WeChatLoginManager {

    private Context mContext;

    public WeChatLoginManager(Context context) {
        mContext = context;
    }

    public void login(String code) {
        OkHttpUtils.get().url(ServiceConstant.WECHAT_GET_TOKEN)
                .addParams("appid", ServiceConstant.WX_APP_ID)
                .addParams("secret", ServiceConstant.WX_APP_SECRET)
                .addParams("code", code)
                .addParams("grant_type", "authorization_code")
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        WeChatTokenResult result = new Gson().fromJson(response, WeChatTokenResult.class);
                        if (result.getExpires_in() == 7200) {
                            // 读取成功后获取用户的信息
                            getUserInfo(result.getAccess_token(), result.getOpenid());
                        } else {
                            T.showShort(mContext, R.string.login_failed);
                        }
                    }
                });
    }

    /**
     * 获取微信用户基本信息
     *
     * @param accessToken
     * @param openId
     */
    private void getUserInfo(String accessToken, String openId) {
        OkHttpUtils.get().url(ServiceConstant.WECHAT_GET_USER_INFO)
                .addParams("access_token", accessToken)
                .addParams("openid", openId)
                .build()
                .execute(new FilterStringCallback() {

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
                        postWeChatLogin(weChat);
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
        weChat.setDeviceRegId(JPushInterface.getRegistrationID(mContext));
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
                            LoginManager loginManager = new LoginManager(mContext, null);
                            loginManager.saveAndEnter(loginInfoResult.getBody());
                        } else {
                            T.showShort(mContext, loginInfoResult.getMsg());
                        }
                    }
                });
    }

}
