package com.jerry.nurse.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by Jerry on 2017/8/6.
 */

public class EaseMobManager {

    private ProgressDialogManager mProgressDialogManager;

    private static final String PASSWORD = "WAJB357";

    private static final int MESSAGE_EASE_MOB_LOGIN_FAILED = 2;
    private static final int MESSAGE_EASE_MOB_LOGIN_SUCCESS = 3;

    private Context mContext;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 环信登陆成功
                case MESSAGE_EASE_MOB_LOGIN_SUCCESS:
                    mProgressDialogManager.dismiss();
                    onLoginSuccess();
                    break;
                // 环信登陆失败
                case MESSAGE_EASE_MOB_LOGIN_FAILED:
                    mProgressDialogManager.dismiss();
                    onLoginFailed();
                    break;
                default:
                    break;
            }
        }
    };

    public EaseMobManager(Context context) {
        mContext = context;
        mProgressDialogManager = new ProgressDialogManager(context);
    }


    /**
     * 环信的登陆方法，是一个异步方法
     *
     * @param registerId
     */
    public void login(String registerId) {
//        mProgressDialogManager.setMessage("初始化中");
//        mProgressDialogManager.show();
        EMClient.getInstance().login(registerId, PASSWORD, new EMCallBack() {
            @Override
            public void onSuccess() {
                L.i("环信登陆成功!!!");
                mHandler.sendEmptyMessage(MESSAGE_EASE_MOB_LOGIN_SUCCESS);
            }

            @Override
            public void onError(int code, String error) {
                L.e("环信登陆失败，错误码：" + code + "，错误信息：" + error);
                mHandler.sendEmptyMessage(MESSAGE_EASE_MOB_LOGIN_FAILED);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * 注销方法
     */
    public void logout() {
        EMClient.getInstance().logout(true, new EMCallBack() {

            @Override
            public void onSuccess() {
                onLogoutSuccess();
            }


            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {

            }
        });
    }


    /**
     * 登录成功
     */
    protected void onLoginSuccess() {
    }

    /**
     * 登录失败
     */
    protected void onLoginFailed() {
        T.showShort(ActivityCollector.getTopActivity(), "通讯模块初始化失败");
    }

    /**
     * 注销成功
     */
    protected void onLogoutSuccess() {
        L.i("环信已注销");
    }
}
