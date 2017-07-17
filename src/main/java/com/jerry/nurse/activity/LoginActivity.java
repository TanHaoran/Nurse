package com.jerry.nurse.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;

import com.jerry.nurse.R;
import com.jerry.nurse.bean.User;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.T;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    // 注册请求码
    private static final int REQUEST_SIGNUP_CODE = 0;

    @Bind(R.id.et_cellphone)
    EditText mCellphoneEditText;

    @Bind(R.id.et_password)
    EditText mPasswordEditText;

    @Bind(R.id.btn_login)
    AppCompatButton mLoginButton;


    @BindString(R.string.cellphoneInvalid)
    String mStringCellphoneInvalid;

    @BindString(R.string.passwordLengthInvalid)
    String mStringPasswordInvalid;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_login;
    }

    @Override
    public void init(Bundle savedInstanceState) {
    }

    @OnClick(R.id.btn_login)
    void onLoginButton(View view) {
        L.i("登陆");

        //验证用户名和密码格式是否符合
//        if (!validate()) {
//            return;
//        }

        mLoginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("登录中...");
        progressDialog.show();

        String cellphone = mCellphoneEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // TODO 登陆逻辑

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                progressDialog.dismiss();
                mLoginButton.setEnabled(true);
                // 登陆成功
                onLoginSuccess();
                // 登陆失败
                // onLoginFailed()
            }
        }, 1000);

    }

    /**
     * 本地验证登陆
     */
    private boolean validate() {
        boolean valid = true;

        String cellphone = mCellphoneEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        if (cellphone.isEmpty()) {
            mCellphoneEditText.setError(mStringCellphoneInvalid);
            valid = false;
        } else {
            mCellphoneEditText.setError(null);
        }

        // 密码的长度要介于4和10之间
        if (password.isEmpty() || password.length() < 4 || password
                .length() > 10) {
            mPasswordEditText.setError(mStringPasswordInvalid);
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }

    private void onLoginSuccess() {

        // TODO
        // saveUserInfo();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * 保存用户信息
     */
    private void saveUserInfo() {

        // 用户存在，密码正确，登录成功, 先保存登录信息，然后跳转至主界面
        SPUtil.put(this, "cellphone", "具体手机号");

        //先清除数据库
        DataSupport.deleteAll(User.class);
        User user = new User();
        user.save();
    }

    private void onLoginFailed() {
        T.showShort(this, R.string.loginFailed);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                L.i("注册成功");
            }
        }
    }


    @OnClick(R.id.tv_signup)
    void onSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP_CODE);
    }
}
