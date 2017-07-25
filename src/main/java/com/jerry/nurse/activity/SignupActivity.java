package com.jerry.nurse.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.ShortMessage;
import com.jerry.nurse.model.User;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.AccountValidatorUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.BindString;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ExtraValue.EXTRA_SIGNUP_TYPE;
import static com.jerry.nurse.constant.ServiceConstant.EASE_MOB_PASSWORD;


/**
 * Created by Jerry on 2017/7/16.
 */

public class SignupActivity extends BaseActivity {

    public static final int EXTRA_TYPE_REGISTER = 0;
    public static final int EXTRA_TYPE_VERTIFICATION_CODE = 1;
    public static final int EXTRA_TYPE_FORGET_PASSWORD = 2;

    private static final int REQUEST_COUNTRY = 0x00000101;

    private static final int MESSAGE_SIGNUP_SUCCESS = 0;
    private static final int MESSAGE_SIGNUP_FAILED = 1;
    private static final int MESSAGE_LOGIN_FAILED = 2;
    private static final int MESSAGE_LOGIN_SUCCESS = 3;


    @Bind(R.id.cet_cellphone)
    EditText mCellphoneEditText;

    @Bind(R.id.et_verification_code)
    EditText mVerificationCodeEditText;

    @Bind(R.id.tv_send_verification_code)
    TextView mGetVerificationCodeTextView;

    @Bind(R.id.btn_signup)
    AppCompatButton mSignupButton;

    @Bind(R.id.iv_agree)
    ImageView mAgreeImageView;

    @Bind(R.id.tv_country)
    TextView mCountryTextView;

    @BindString(R.string.cellphone_invalid)
    String mStringCellphoneInvalid;

    @BindString(R.string.verification_code_length_invalid)
    String mStringVerificationCodeInvalid;

    @BindColor(R.color.primary)
    int mPrimaryColor;

    @BindColor(R.color.gray_textColor)
    int mGrayColor;

    private ProgressDialog mProgressDialog;

    private boolean mIsAgree;

    private String mErrorMessage;

    private User mUser;

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
                mGetVerificationCodeTextView.setText(R.string.send_verification_code);
                mValidateCountDown = 60;
                mGetVerificationCodeTextView.setEnabled(true);
                mGetVerificationCodeTextView.setTextColor(mPrimaryColor);
                mHandler.removeCallbacks(mValidateRunnable);
            } else {
                mGetVerificationCodeTextView.setText("(" + mValidateCountDown + "秒)");
                mHandler.postDelayed(mValidateRunnable, 1000);
            }
        }
    };

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_SIGNUP_SUCCESS:
                    onSignupSuccess();
                    break;
                case MESSAGE_SIGNUP_FAILED:
                    onSignupFailed();
                    break;
                case MESSAGE_LOGIN_SUCCESS:
                    onLoginSuccess();
                    break;
                case MESSAGE_LOGIN_FAILED:
                    onSignupFailed();
                    break;
                default:
                    break;
            }
        }
    };

    public static Intent getIntent(Context context, int type) {
        Intent intent = new Intent(context, SignupActivity.class);
        intent.putExtra(EXTRA_SIGNUP_TYPE, type);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_signup;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        int type = getIntent().getIntExtra(EXTRA_SIGNUP_TYPE, -1);

        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
    }

    /**
     * 获取验证码按钮
     *
     * @param v
     */
    @OnClick(R.id.tv_send_verification_code)
    void onGetVerificationCode(View v) {
        // 首先验证手机号是否为空和是否合法
        String cellphone = mCellphoneEditText.getText().toString();
        if (cellphone.isEmpty()) {
            T.showLong(this, R.string.cellphone_empty);
            return;
        }
        if (!AccountValidatorUtil.isMobile(cellphone)) {
            T.showLong(this, R.string.cellphone_invalid);
            return;
        }

        mProgressDialog.setMessage("发送验证码...");
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_VERIFICATION_CODE)
                .addParams("Phone", cellphone)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                        T.showLong(SignupActivity.this, R.string.get_verification_code_failed);
                        L.e("发送验证码失败");
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        if (response.equals(ServiceConstant.REQUEST_SUCCESS)) {
                            L.i("发送验证码成功");
                            // 控制发送验证码的状态
                            mGetVerificationCodeTextView.setEnabled(false);
                            mGetVerificationCodeTextView.setTextColor(mGrayColor);
                            mGetVerificationCodeTextView.setText("(" + mValidateCountDown + "秒)");
                            mHandler.postDelayed(mValidateRunnable, 1000);
                            T.showLong(SignupActivity.this, R.string.get_verification_finished);
                        } else {
                            L.i("发送验证码失败" + response);
                            T.showLong(SignupActivity.this, R.string.get_verification_code_failed);
                        }
                    }
                });
    }

    @OnClick(R.id.btn_signup)
    void onSignup(View view) {
        // 本地验证用户名和密码格式是否符合
        if (!localValidate()) {
            if (mErrorMessage != null) {
                T.showLong(this, mErrorMessage);
            }
            return;
        }

        // 勾选同意
        if (!mIsAgree) {
            T.showLong(this, R.string.please_agree);
            return;
        }

        mSignupButton.setEnabled(false);

        mProgressDialog.setMessage("注册中...");
        mProgressDialog.show();

        String cellphone = mCellphoneEditText.getText().toString();
        String code = mVerificationCodeEditText.getText().toString();

        // 远端服务验证
        validateVerificationCode(cellphone, code);
    }

    /**
     * 验证验证码服务
     *
     * @param cellphone
     * @param code
     */
    private void validateVerificationCode(final String cellphone, String code) {
        ShortMessage shortMessage = new ShortMessage(cellphone, code);
        OkHttpUtils.postString()
                .url(ServiceConstant.VALIDATE_VERIFICATION_CODE)
                .content(StringUtil.addModelWithJson(shortMessage))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("验证验证码失败");
                        T.showLong(SignupActivity.this, R.string.verification_code_invalid);
                        onSignupFailed();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        if (response.contains(ServiceConstant.USER_EXIST)) {
                            L.i("用户已存在，准备护士通登录");
                            if (response.split("|").length == 2) {
                                String registerId = response.split("|")[1];
                                // 获取用户信息，然后在环信登录
                                getUserInfo(cellphone, registerId);
                            } else {
                                onSignupFailed();
                            }
                        } else if (response.startsWith(ServiceConstant.USER_NOT_EXIST)) {
                            L.i("用户不存在，准备环信注册");
                            // 用户不存在，服务端自动进行护士通注册
                            if (response.split(":").length == 2) {
                                String registerId = response.split(":")[1];
                                // 然后在环信注册
                                easeMobSignup(registerId, EASE_MOB_PASSWORD);
                            } else {
                                onSignupFailed();
                            }
                        } else {
                            L.i(response);
                            T.showLong(SignupActivity.this, response);
                            onSignupFailed();
                        }
                    }
                });
    }

    /**
     * 获取用户信息并在环信登陆
     */
    private void getUserInfo(final String cellphone, final String registerId) {
        OkHttpUtils.get().url(ServiceConstant.GET_USER_INFO)
                .addParams("Phone", cellphone)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("获取用户信息失败");
                        onSignupFailed();
                        T.showLong(SignupActivity.this, R.string.signup_failed);
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        L.e("获取用户信息成功，护士通登陆");
                        mUser = new Gson().fromJson(response, User.class);
                        easeMobLogin(registerId, EASE_MOB_PASSWORD);
                    }
                });
    }

    /**
     * 环信的登陆方法，是一个异步方法
     *
     * @param cellphone
     * @param password
     */
    private void easeMobLogin(String cellphone, String password) {
        EMClient.getInstance().login(cellphone, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                L.i("环信登陆成功");
                mHandler.sendEmptyMessage(MESSAGE_LOGIN_SUCCESS);
            }

            @Override
            public void onError(int code, String error) {
                L.e("环信登陆失败，错误码：" + code + "，错误信息：" + error);
                mHandler.sendEmptyMessage(MESSAGE_LOGIN_FAILED);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * 环信的注册方法，是一个同步方法
     *
     * @param cellphone
     * @param password
     */
    private void easeMobSignup(final String cellphone, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().createAccount(cellphone, password);
                    L.i("环信注册成功！");
                    mHandler.sendEmptyMessage(MESSAGE_SIGNUP_SUCCESS);
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    L.e("环信注册失败，错误码：" + e.getErrorCode() + "，错误信息：" + e.getMessage());
                    mHandler.sendEmptyMessage(MESSAGE_SIGNUP_FAILED);
                }
            }
        }
        ).start();
    }

    /**
     * 本地验证注册
     */
    private boolean localValidate() {

        String cellphone = mCellphoneEditText.getText().toString();
        String verificationCode = mVerificationCodeEditText.getText()
                .toString();

        // 本地验证手机号
        if (cellphone.isEmpty()) {
            mErrorMessage = mStringCellphoneInvalid;
            return false;
        } else {
            mErrorMessage = null;
        }

        // 本地验证验证码
        if (verificationCode.length() != 4) {
            mErrorMessage = mStringVerificationCodeInvalid;
            return false;
        } else {
            mErrorMessage = null;
        }

        return true;
    }

    /**
     * 登陆成功后执行
     */
    private void onLoginSuccess() {
        // 首先处理界面
        resetUI();
        T.showLong(this, R.string.login_success);

        // 保存用户信息
        SPUtil.put(this, "cellphone", mUser.getPhone());
        SPUtil.put(this, "name", mUser.getName());
        SPUtil.put(this, "nickname", mUser.getNickName());

        //先清除数据库
        DataSupport.deleteAll(User.class);
        User user = new User();
        user.save();
        L.i("保存用户信息成功");

        String cellphone = mCellphoneEditText.getText().toString();
        Intent intent = PasswordActivity.getIntent(this, "设置密码", cellphone);
        startActivity(intent);
    }

    private void resetUI() {
        mProgressDialog.dismiss();
        mSignupButton.setEnabled(true);
        mHandler.removeCallbacks(mValidateRunnable);
    }


    /**
     * 注册成功
     */
    private void onSignupSuccess() {
        // 首先处理界面
        resetUI();
        T.showLong(this, R.string.signup_success);

        String cellphone = mCellphoneEditText.getText().toString();
        Intent intent = PasswordActivity.getIntent(this, "设置密码", cellphone);
        startActivity(intent);
    }

    /**
     * 注册失败
     */
    private void onSignupFailed() {
        mProgressDialog.dismiss();
        mSignupButton.setEnabled(true);
    }

    @OnClick(R.id.rl_country)
    void onCountry(View view) {
        Intent intent = CountryActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_COUNTRY);
    }

    @OnClick({R.id.ll_agree, R.id.tv_agree})
    void onAgree(View v) {
        if (mIsAgree) {
            mAgreeImageView.setVisibility(View.INVISIBLE);
        } else {
            mAgreeImageView.setVisibility(View.VISIBLE);
        }
        mIsAgree = !mIsAgree;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mValidateRunnable);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_COUNTRY) {
            // 获取国家地区编号并显示
            String country = data.getStringExtra("");
            mCountryTextView.setText(country);
        }
    }
}
