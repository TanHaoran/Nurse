package com.jerry.nurse.util;

import android.os.Handler;
import android.os.Message;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by Jerry on 2017/8/6.
 */

public abstract class EaseMobManager {

    private static final String PASSWORD = "WAJB357";

    private static final int MESSAGE_EASE_MOB_LOGIN_FAILED = 2;
    private static final int MESSAGE_EASE_MOB_LOGIN_SUCCESS = 3;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                // 环信登陆成功
                case MESSAGE_EASE_MOB_LOGIN_SUCCESS:
                    onLoginSuccess();
                    break;
                // 环信登陆失败
                case MESSAGE_EASE_MOB_LOGIN_FAILED:
                    onLoginFailed();
                    break;
                default:
                    break;
            }
        }
    };

    public EaseMobManager() {
    }


    /**
     * 环信的登陆方法，是一个异步方法
     *
     * @param registerId
     */
    public void login(String registerId) {
        EMClient.getInstance().login(registerId, PASSWORD, new EMCallBack() {
            @Override
            public void onSuccess() {
                L.i("环信登陆成功");
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
     * 环信登录成功
     */
    protected void onLoginFailed() {
        T.showShort(ActivityCollector.getTopActivity(), "通讯模块初始化失败");
    }


    /**
     * 环信登录成功
     */
    protected abstract void onLoginSuccess();
}
