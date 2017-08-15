package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.model.Register;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.LoginManager;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class HospitalLoginActivity extends BaseActivity {

    @Bind(R.id.tb_login)
    TitleBar mTitleBar;

    @Bind(R.id.et_account)
    EditText mAccountEditText;

    @Bind(R.id.et_password)
    EditText mPasswordEditText;

    private ProgressDialogManager mProgressDialogManager;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, HospitalLoginActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_hospital_login;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        mProgressDialogManager = new ProgressDialogManager(this);

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });
    }

    @OnClick(R.id.acb_login)
    void onLogin(View view) {
        String account = mAccountEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        //验证用户名和密码格式是否符合
        String errorMessage = localValidate(account, password);
        if (errorMessage != null) {
            T.showShort(this, errorMessage);
            return;
        }

        // 远端登录
        login(account, password);
    }

    /**
     * 本地验证注册
     */
    public static String localValidate(String account, String password) {
        // 本地验证账号
        if (account.isEmpty()) {
            return "账号号不能为空";
        }

        // 本地验证验证码
        if (password.isEmpty()) {
            return "密码不能为空";
        }

        return null;
    }

    /**
     * 登录
     *
     * @param account
     * @param password
     */
    private void login(final String account, final String password) {
        mProgressDialogManager.show();
        Register register = new Register(account, password);
        OkHttpUtils.postString()
                .url(ServiceConstant.HOSPITAL_LOGIN)
                .content(StringUtil.addModelWithJson(register))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        final LoginInfoResult loginInfoResult = new Gson().fromJson(response, LoginInfoResult.class);
                        if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
                            LoginManager loginManager = new LoginManager(HospitalLoginActivity.this, null);
                            loginManager.saveAndEnter(loginInfoResult.getBody());
                        } else {
                            T.showShort(HospitalLoginActivity.this, loginInfoResult.getMsg());
                        }
                    }
                });
    }


    @OnClick(R.id.tv_account_login)
    void onAccountLogin(View view) {
        finish();
    }
}
