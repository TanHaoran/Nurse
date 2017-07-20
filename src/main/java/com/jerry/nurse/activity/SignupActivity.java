package com.jerry.nurse.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;

import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.jerry.nurse.R;
import com.jerry.nurse.common.ServiceMethod;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by Jerry on 2017/7/16.
 */

public class SignupActivity extends BaseActivity {

    @Bind(R.id.et_cellphone)
    EditText mCellphoneEditText;

    @Bind(R.id.et_verification_code)
    EditText mVerificationCodeEditText;

    @Bind(R.id.et_password)
    EditText mPasswordEditText;

    @Bind(R.id.btn_get_verification_code)
    AppCompatButton mGetVerificationCodeButton;

    @Bind(R.id.btn_signup)
    AppCompatButton mSignupButton;

    @BindString(R.string.cellphone_invalid)
    String mStringCellphoneInvalid;

    @BindString(R.string.verification_code_invalid)
    String mStringVerificationCodeInvalid;

    @BindString(R.string.password_length_invalid)
    String mStringPasswordInvalid;

    private ProgressDialog mProgressDialog;

    /**
     * 验证码获取间隔
     */
    private int mValidateCountDown = 60;

    private Runnable mValidateRunnable = new Runnable() {
        @Override
        public void run() {
            // 验证码倒计时
            mValidateCountDown--;
            if (mValidateCountDown == 0) {
                mHandler.removeCallbacks(mValidateRunnable);
                mGetVerificationCodeButton.setText("获取验证码");
                mValidateCountDown = 60;
                mGetVerificationCodeButton.setEnabled(true);
            }
            mGetVerificationCodeButton.setText("(" + mValidateCountDown + "秒)");
            mHandler.postDelayed(mValidateRunnable, 1000);
        }
    };

    private Handler mHandler = new Handler();

    @Override
    public int getContentViewResId() {
        return R.layout.activity_signup;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
    }

    /**
     * 获取验证码按钮
     *
     * @param v
     */
    @OnClick(R.id.btn_get_verification_code)
    void onGetVerificationCode(View v) {
        OkHttpUtils.get().url(ServiceMethod.GET_VERIFICATION_CODE)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("发送验证码出错");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // 验证码是否发送成功
                        if (response.equals("")) {
                            L.i("发送验证码成功");
                            mGetVerificationCodeButton.setEnabled(true);
                            mGetVerificationCodeButton.setText("(" + mValidateCountDown + "秒)");
                            mHandler.postDelayed(mValidateRunnable, 1000);
                        } else {
                            L.i("发送验证码失败");
                        }
                    }
                });
    }

    @OnClick(R.id.btn_signup)
    void onSignup(View view) {
        // 本地验证用户名和密码格式是否符合
        if (!localValidate()) {
            return;
        }

        mSignupButton.setEnabled(false);

        mProgressDialog.setMessage("注册中...");
        mProgressDialog.show();

        String cellphone = mCellphoneEditText.getText().toString();
        String verificationCode = mVerificationCodeEditText.getText().toString();

        // 远端验证码验证
        verificationCodeValidate(cellphone, verificationCode);
    }


    /**
     * 验证码验证
     *
     * @param cellphone 手机号
     * @param code      验证码
     */
    private void verificationCodeValidate(String cellphone, String code) {
        OkHttpUtils.get().url(ServiceMethod.VALIDATE_VERIFICATION_CODE)
                .addParams("cellphone", cellphone)
                .addParams("code", code)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("验证验证码出错");
                        onSignupFailed();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // 验证验证码是否正确
                        if (response.equals("")) {
                            L.i("验证验证码成功");
                            String cellphone = mCellphoneEditText.getText().toString();
                            String password = mPasswordEditText.getText().toString();
                            signup(cellphone, password);
                        } else {
                            L.i("验证验证码失败");
                            T.showLong(SignupActivity.this, R.string.verification_code_invalid);
                            onSignupFailed();
                        }
                    }
                });
    }

    /**
     * 注册
     *
     * @param cellphone
     * @param password
     */
    private void signup(String cellphone, String password) {
        OkHttpUtils.post().url(ServiceMethod.SIGNUP)
                .addParams("cellphone", cellphone)
                .addParams("password", password)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("注册出错");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // 验证是否注册成功
                        if (response.equals("")) {
                            L.i("注册成功");
                            onSignupSuccess();
                        } else {
                            L.i("注册失败");
                            onSignupFailed();
                        }
                    }
                });
    }

    /**
     * 本地验证注册
     */
    private boolean localValidate() {
        boolean valid = true;

        String cellphone = mCellphoneEditText.getText().toString();
        String verificationCode = mVerificationCodeEditText.getText()
                .toString();
        String password = mPasswordEditText.getText().toString();

        // 本地验证手机号
        if (cellphone.isEmpty()) {
            mCellphoneEditText.setError(mStringCellphoneInvalid);
            valid = false;
        } else {
            mCellphoneEditText.setError(null);
        }

        // 本地验证验证码
        if (verificationCode.length() != 4) {
            mVerificationCodeEditText.setError(mStringVerificationCodeInvalid);
            valid = false;
        } else {
            mVerificationCodeEditText.setError(null);
        }

        // 本地验证密码
        if (password.isEmpty() || password.length() < 4 || password
                .length() > 10) {
            mPasswordEditText.setError(mStringPasswordInvalid);
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
        mProgressDialog.dismiss();
        mSignupButton.setEnabled(false);
        setResult(Activity.RESULT_OK, null);
        mHandler.removeCallbacks(mValidateRunnable);
        finish();
    }

    /**
     * 注册失败
     */
    private void onSignupFailed() {
        mProgressDialog.dismiss();
        mSignupButton.setEnabled(false);
        T.showShort(this, R.string.signup_failed);
    }

    /**
     * 环信的注册方法，是一个同步方法
     */
    private void easeMobSignup() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String cellphone = mCellphoneEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                try {
                    EMClient.getInstance().createAccount(cellphone, password);
                    L.i("注册成功！");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    L.i("注册失败，错误码：" + e.getErrorCode() + "，错误信息：" + e.getMessage());
                }
            }
        }
        ).start();
    }

    @OnClick(R.id.tv_login)
    void onLogin(View view) {
        mHandler.removeCallbacks(mValidateRunnable);
        finish();
    }
}
