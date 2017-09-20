package com.jerry.nurse.util;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.jerry.nurse.activity.MainActivity;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

/**
 * Created by Jerry on 2017/8/6.
 * 获取用户登陆信息工具类
 */
public class LoginManager {

    private Context mContext;
    private ProgressDialogManager mProgressDialogManager;

    public LoginManager(Context context, ProgressDialogManager progressDialogManager) {
        mContext = context;
        mProgressDialogManager = progressDialogManager;
    }

    /**
     * 根据注册Id获取登陆信息
     *
     * @param registerId
     */
    public void getLoginInfoByRegisterId(final String registerId) {
        OkHttpUtils.get().url(ServiceConstant.GET_USER_LOGIN_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        T.showShort(mContext, "登录失败");
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        // 如果登陆成功保存登陆信息并跳转页面
                        LoginInfoResult loginInfoResult = new Gson().fromJson(response, LoginInfoResult.class);
                        if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
                            saveAndEnter(loginInfoResult.getBody());
                        } else {
                            T.showShort(mContext, "登录失败");
                        }
                    }
                });
    }

    /**
     * 更新登陆信息
     *
     * @param registerId
     */
    public void updateLoginInfoByRegisterId(final String registerId) {
        OkHttpUtils.get().url(ServiceConstant.GET_USER_LOGIN_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        T.showShort(mContext, "更新信息失败");
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        // 如果登陆成功保存登陆信息并跳转页面
                        LoginInfoResult loginInfoResult = new Gson().fromJson(response, LoginInfoResult.class);
                        if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
                            save(loginInfoResult.getBody());
                        } else {
                            T.showShort(mContext, loginInfoResult.getMsg());
                        }
                    }
                });
    }

    /**
     * 保存登陆信息
     *
     * @param loginInfo
     */
    public void save(LoginInfo loginInfo) {
        // 保存登陆信息到数据库
        LitePalUtil.saveLoginInfo(mContext, loginInfo);
    }


    /**
     * 保存登陆信息并跳转页面
     *
     * @param loginInfo
     */
    public void saveAndEnter(LoginInfo loginInfo) {
        // 保存登陆信息到数据库
        LitePalUtil.saveLoginInfo(mContext, loginInfo);
        goToMainActivity();
    }

    /**
     * 保存信息并跳转
     *
     * @param loginInfoResult
     */
    public void saveAndEnter(LoginInfoResult loginInfoResult) {
        if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
            LoginInfo loginInfo = loginInfoResult.getBody();

            // 保存登陆信息到数据库
            LitePalUtil.saveLoginInfo(mContext, loginInfo);
            goToMainActivity();
        } else {
            T.showShort(mContext, "登录失败");
        }
    }


    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        Intent intent = MainActivity.getIntent(ActivityCollector.getTopActivity());
        mContext.startActivity(intent);
    }
}
