package com.jerry.nurse.util;

import android.content.Context;

import com.google.gson.Gson;
import com.jerry.nurse.activity.BaseActivity;
import com.jerry.nurse.listener.PermissionListener;
import com.jerry.nurse.model.QQOriginUserInfo;
import com.jerry.nurse.model.Qq;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Jerry on 2017/8/4.
 */

public abstract class TencentLoginUtil {

    // 腾讯官方获取的APPID
    private static final String APP_ID = "1106292702";
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;

    private Context mContext;

    public TencentLoginUtil(Context context) {
        mContext = context;
        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID, context);
        mIUiListener = new BaseUiListener();
    }

    public BaseUiListener getIUiListener() {
        return mIUiListener;
    }

    public void login() {
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */

        //all表示获取所有权限
        mTencent.login(ActivityCollector.getTopActivity(), "all", mIUiListener);
    }


    /**
     * 自定义监听器实现IUiListener接口后，需要实现的3个方法
     * onComplete完成 onError错误 onCancel取消
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            L.e("response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                final String openID = obj.getString("openid");
                final String accessToken = obj.getString("access_token");
                final String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                final QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(mContext, qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(final Object response) {
                        BaseActivity.requestRuntimePermission(new String[]{android.Manifest.permission.READ_PHONE_STATE},
                                new PermissionListener() {

                                    @Override
                                    public void onGranted() {
                                        String deviceId = DeviceUtil.getDeviceId(mContext);
                                        makeUpUserInfo(response, deviceId, openID, accessToken, expires);
                                    }

                                    @Override
                                    public void onDenied(List<String> deniedPermission) {
                                        String deviceId = "";
                                        makeUpUserInfo(response, deviceId, openID, accessToken, expires);
                                    }
                                });

                    }

                    @Override
                    public void onError(UiError uiError) {
                        L.e("登录失败" + uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        L.e("登录取消");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            T.showShort(mContext, "授权失败");
        }

        @Override
        public void onCancel() {
            T.showShort(mContext, "授权取消");
        }
    }

    private void makeUpUserInfo(Object response, String deviceId, String openID, String accessToken, String expires) {
        QQOriginUserInfo originInfo = new Gson().fromJson(response.toString(), QQOriginUserInfo.class);
        Qq info = new Qq();
        info.setOpenId(openID);
        info.setFigureUrl(originInfo.getFigureurl_qq_2());
        String nickname = EmojiFilter.filterEmoji(originInfo.getNickname());
        info.setNickName(nickname);
        info.setProvince(originInfo.getProvince());
        info.setCity(originInfo.getCity());
        info.setGender(originInfo.getGender());
        info.setAccessToken(accessToken);
        info.setExpires(expires);
        info.setDeviceId(deviceId);
        L.e("登录成功" + new Gson().toJson(info));
        loginComplete(info);
    }

    public abstract void loginComplete(Qq info);
}
