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
import com.umeng.analytics.MobclickAgent;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

import static com.jerry.nurse.R.id.btn_login;
import static com.jerry.nurse.R.id.et_cellphone;
import static com.jerry.nurse.R.id.et_password;
import static com.jerry.nurse.R.id.tv_signup;

public class LoginActivity extends BaseActivity {

    // 注册请求码
    private static final int REQUEST_SIGNUP_CODE = 0;

    @Bind(et_cellphone)
    EditText mCellphoneEditText;

    @Bind(et_password)
    EditText mPasswordEditText;

    @Bind(btn_login)
    AppCompatButton mLoginButton;


    @BindString(R.string.cellphone_invalid)
    String mCellphoneInvalid;

    @BindString(R.string.password_length_invalid)
    String mPasswordInvalid;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_login;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // 禁止默认的页面统计方式
        MobclickAgent.openActivityDurationTrack(false);
    }

    @OnClick(btn_login)
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
            mCellphoneEditText.setError(mCellphoneInvalid);
            valid = false;
        } else {
            mCellphoneEditText.setError(null);
        }

        // 密码的长度要介于4和10之间
        if (password.isEmpty() || password.length() < 4 || password
                .length() > 10) {
            mPasswordEditText.setError(mPasswordInvalid);
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
        T.showShort(this, R.string.login_failed);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                L.i("注册成功");
            }
        }
    }


    @OnClick(tv_signup)
    void onSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP_CODE);
    }
}
