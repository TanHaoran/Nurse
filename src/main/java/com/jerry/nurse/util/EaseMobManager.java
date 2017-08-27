package com.jerry.nurse.util;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

/**
 * Created by Jerry on 2017/8/6.
 */

public class EaseMobManager {

    /**
     * 环信账号默认密码
     */
    private static final String PASSWORD = "WAJB357";

    /**
     * 环信的登陆方法，是一个异步方法
     *
     * @param registerId
     */
    public void login(String registerId) {
        EMClient.getInstance().login(registerId, PASSWORD, new EMCallBack() {
            @Override
            public void onSuccess() {
                L.i("环信登陆成功!");
            }

            @Override
            public void onError(int code, String error) {
                L.e("环信登陆失败，错误码：" + code + "，错误信息：" + error);
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
                L.i("环信注销成功!");
            }


            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String message) {
                L.i("环信注销失败!");
            }
        });
    }
}
