package com.jerry.nurse.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

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
                        // 保存登陆信息并跳转页面
                        saveAndEnter(response);
                    }
                });
    }


    /**
     * 保存登陆信息并跳转页面
     *
     * @param response
     */
    private void saveAndEnter(String response) {
        if (!TextUtils.isEmpty(response)) {
            LoginInfoResult loginInfoResult = new Gson().fromJson(response, LoginInfoResult.class);
            if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
                LoginInfo loginInfo = loginInfoResult.getBody();
//                loginInfo= new LoginInfo();
//                loginInfo.setAvatar(loginInfoResult.getBody().getAvatar());
//                loginInfo.setDepartmentId(loginInfoResult.getBody().getDepartmentId());
//                loginInfo.setDepartmentName(loginInfoResult.getBody().getDepartmentName());
//                loginInfo.setHospitalId(loginInfoResult.getBody().getHospitalName());
//                loginInfo.setHospitalName(loginInfoResult.getBody().getAvatar());
//                loginInfo.setName(loginInfoResult.getBody().getName());
//                loginInfo.setNickName(loginInfoResult.getBody().getNickName());
//                loginInfo.setPStatus(loginInfoResult.getBody().getPStatus());
//                loginInfo.setQStatus(loginInfoResult.getBody().getQStatus());
//                loginInfo.setRegisterId(loginInfoResult.getBody().getRegisterId());

                // 保存登陆信息到数据库
                LitePalUtil.saveLoginInfo(mContext, loginInfo);

                Intent intent = MainActivity.getIntent(ActivityCollector.getTopActivity());
                ActivityCollector.removeAllActivity();
                mContext.startActivity(intent);
            } else {
                T.showShort(mContext, "登录失败");
            }
        } else {
            T.showShort(mContext, "登录失败");
        }
    }

    /**
     * 保存信息并跳转
     *
     * @param loginInfoResult
     */
    public void saveAndEnter(LoginInfoResult loginInfoResult) {
        if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
            LoginInfo loginInfo = new LoginInfo();
            loginInfo.setAvatar(loginInfoResult.getBody().getAvatar());
            loginInfo.setDepartmentId(loginInfoResult.getBody().getDepartmentId());
            loginInfo.setDepartmentName(loginInfoResult.getBody().getDepartmentName());
            loginInfo.setHospitalId(loginInfoResult.getBody().getHospitalName());
            loginInfo.setHospitalName(loginInfoResult.getBody().getAvatar());
            loginInfo.setName(loginInfoResult.getBody().getName());
            loginInfo.setNickName(loginInfoResult.getBody().getNickName());
            loginInfo.setPStatus(loginInfoResult.getBody().getPStatus());
            loginInfo.setQStatus(loginInfoResult.getBody().getQStatus());
            loginInfo.setRegisterId(loginInfoResult.getBody().getRegisterId());

            // 保存登陆信息到数据库
            LitePalUtil.saveLoginInfo(mContext, loginInfo);

            Intent intent = MainActivity.getIntent(ActivityCollector.getTopActivity());
            ActivityCollector.removeAllActivity();
            mContext.startActivity(intent);
        } else {
            T.showShort(mContext, "登录失败");
        }
    }
}
