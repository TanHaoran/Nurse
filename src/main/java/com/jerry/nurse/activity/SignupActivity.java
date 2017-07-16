package com.jerry.nurse.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jerry.nurse.R;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.T;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

import static com.jerry.nurse.R.id.btn_signup;
import static com.jerry.nurse.R.id.et_cellphone;
import static com.jerry.nurse.R.id.et_password;
import static com.jerry.nurse.R.id.et_verification_code;
import static com.jerry.nurse.R.id.tv_login;

/**
 * Created by Jerry on 2017/7/16.
 */

public class SignupActivity extends BaseActivity {

    @Bind(et_cellphone)
    EditText mCellphoneEditText;

    @Bind(et_verification_code)
    EditText mVerificationCodeEditText;

    @Bind(et_password)
    EditText mPasswordEditText;

    @Bind(btn_signup)
    Button mSignupButton;

    @BindString(R.string.cellphone_invalid)
    String mCellphoneInvalid;

    @BindString(R.string.verification_code_invalid)
    String mVerificationCodeInvalid;

    @BindString(R.string.password_length_invalid)
    String mPasswordInvalid;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_signup;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @OnClick(btn_signup)
    void onSignup(View view) {
        L.i("注册");

        // 本地验证用户名和密码格式是否符合
        if (!validate()) {
            return;
        }

        mSignupButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("注册中...");
        progressDialog.show();

        String cellphone = mCellphoneEditText.getText().toString();
        String verificationCode = mVerificationCodeEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // TODO 注册逻辑

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSignupButton.setEnabled(true);
                progressDialog.dismiss();
                // 注册成功
                onSignupSuccess();
                // 注册失败
                // onSignupFailed()
            }
        }, 1000);
    }

    /**
     * 本地验证注册
     */
    private boolean validate() {
        boolean valid = true;

        String cellphone = mCellphoneEditText.getText().toString();
        String verificationCode = mVerificationCodeEditText.getText()
                .toString();
        String password = mPasswordEditText.getText().toString();

        // 本地验证手机号
        if (cellphone.isEmpty()) {
            mCellphoneEditText.setError(mCellphoneInvalid);
            valid = false;
        } else {
            mCellphoneEditText.setError(null);
        }

        // 本地验证验证码
        if (verificationCode.length() != 4) {
            mVerificationCodeEditText.setError(mVerificationCodeInvalid);
            valid = false;
        } else {
            mVerificationCodeEditText.setError(null);
        }

        // 本地验证密码
        if (password.isEmpty() || password.length() < 4 || password
                .length() > 10) {
            mPasswordEditText.setError(mPasswordInvalid);
            valid = false;
        } else {
            mPasswordEditText.setError(null);
        }

        return valid;
    }

    /**
     * 注册成功
     */
    private void onSignupSuccess() {
        setResult(Activity.RESULT_OK, null);
        finish();
    }

    /**
     * 注册失败
     */
    private void onSignupFailed() {
        T.showShort(this, R.string.signup_failed);
    }

    @OnClick(tv_login)
    void onLogin(View view) {
        finish();
    }
}
