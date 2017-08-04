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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.ShortMessage;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.AccountValidatorUtil;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.UserUtil;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.activity.CountryActivity.EXTRA_COUNTRY_CODE;
import static com.jerry.nurse.activity.CountryActivity.EXTRA_COUNTRY_NAME;
import static com.jerry.nurse.constant.ServiceConstant.EASE_MOB_PASSWORD;
import static com.jerry.nurse.constant.ServiceConstant.USER_COLON;


/**
 * Created by Jerry on 2017/7/16.
 */

public class SignupActivity extends BaseActivity {

    public static final String EXTRA_SIGNUP_TYPE = "extra_signup_type";

    public static final String DEFAULT_COUNTRY_CODE = "+86";

    public static final int EXTRA_TYPE_REGISTER = 0;
    public static final int EXTRA_TYPE_FORGET_PASSWORD = 1;
    public static final int EXTRA_TYPE_VERIFICATION_CODE = 2;
    public static final int EXTRA_TYPE_CHANGE_CELLPHONE = 3;

    private static final int REQUEST_COUNTRY = 0x00000101;

    private static final int MESSAGE_SIGNUP_SUCCESS = 0;
    private static final int MESSAGE_SIGNUP_FAILED = 1;
    private static final int MESSAGE_EASE_MOB_LOGIN_FAILED = 2;
    private static final int MESSAGE_EASE_MOB_LOGIN_SUCCESS = 3;

    @Bind(R.id.tb_signup)
    TitleBar mTitleBar;

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

    @Bind(R.id.ll_protocol)
    LinearLayout mProtocolLayout;

    @BindColor(R.color.primary)
    int mPrimaryColor;

    @BindColor(R.color.gray_textColor)
    int mGrayColor;


    // 是否同意协议
    private boolean mIsAgree = true;

    private int mType = EXTRA_TYPE_REGISTER;

    private String mRegisterId;

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
                // 注册成功
                case MESSAGE_SIGNUP_SUCCESS:
                case MESSAGE_EASE_MOB_LOGIN_SUCCESS:
                    onSignupSuccess();
                    break;
                // 注册失败
                case MESSAGE_SIGNUP_FAILED:
                case MESSAGE_EASE_MOB_LOGIN_FAILED:
                    onSignupFailed();
                    break;
                default:
                    break;
            }
        }
    };
    private ProgressDialog mProgressDialog;

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

        // 初始化等待框
        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("请稍后...");


        mType = getIntent().getIntExtra(EXTRA_SIGNUP_TYPE, -1);

        if (mType == EXTRA_TYPE_REGISTER) {
            mTitleBar.setTitle("新用户注册");
        } else if (mType == EXTRA_TYPE_FORGET_PASSWORD) {
            mTitleBar.setTitle("忘记密码");
            mProtocolLayout.setVisibility(View.GONE);
            mSignupButton.setText(R.string.next_step);
        } else if (mType == EXTRA_TYPE_VERIFICATION_CODE) {
            mTitleBar.setTitle("验证码登陆");
            mProtocolLayout.setVisibility(View.GONE);
            mSignupButton.setText(R.string.login);
        }
        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });
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
        int type = 0;
        if (mType == EXTRA_TYPE_REGISTER) {
            type = 0;
        } else if (mType == EXTRA_TYPE_FORGET_PASSWORD) {
            type = 2;
        } else if (mType == EXTRA_TYPE_VERIFICATION_CODE) {
            type = 1;
        }
        // 读取国家代码
        String countryCode = DEFAULT_COUNTRY_CODE;
        if (mCountryTextView.getText().toString().split(" ").length == 2) {
            countryCode = mCountryTextView.getText().toString().split(" ")[1];
        }

        try {
            countryCode = URLEncoder.encode(countryCode, "UTF-8");
            OkHttpUtils.get().url(ServiceConstant.GET_VERIFICATION_CODE)
                    .addParams("Phone", cellphone)
                    .addParams("CountryCode", countryCode)
                    .addParams("Type", String.valueOf(type))
                    .build()
                    .execute(new FilterStringCallback() {

                        @Override
                        public void onFilterError(Call call, Exception e, int id) {
                            mProgressDialog.dismiss();
                        }

                        @Override
                        public void onFilterResponse(String response, int id) {
                            mProgressDialog.dismiss();
                            if (response.equals(ServiceConstant.REQUEST_SUCCESS)) {
                                // 控制发送验证码的状态
                                mGetVerificationCodeTextView.setEnabled(false);
                                mGetVerificationCodeTextView.setTextColor(mGrayColor);
                                mGetVerificationCodeTextView.setText("(" + mValidateCountDown + "秒)");
                                mHandler.postDelayed(mValidateRunnable, 1000);
                                T.showLong(SignupActivity.this, R.string.get_verification_finished);
                            } else {
                                T.showLong(SignupActivity.this, response);
                            }
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


    }

    @OnClick(R.id.btn_signup)
    void onSignup(View view) {
        // 本地验证用户名和密码格式是否符合
        String cellphone = mCellphoneEditText.getText().toString();
        String verificationCode = mVerificationCodeEditText.getText().toString();

        String errorMessage = localValidate(cellphone, verificationCode);

        if (errorMessage != null) {
            T.showShort(this, errorMessage);
            return;
        }

        // 勾选同意
        if (!mIsAgree) {
            T.showLong(this, R.string.please_agree);
            return;
        }

        mSignupButton.setEnabled(false);

        mProgressDialog.setMessage("验证中...");
        mProgressDialog.show();

        // 远端服务验证
        validateVerificationCode(cellphone, verificationCode);
    }

    /**
     * 验证验证码服务
     *
     * @param cellphone
     * @param code
     */
    private void validateVerificationCode(final String cellphone, String code) {
        int type = 0;
        if (mType == EXTRA_TYPE_REGISTER) {
            type = 0;
        } else if (mType == EXTRA_TYPE_FORGET_PASSWORD) {
            type = 2;
        } else if (mType == EXTRA_TYPE_VERIFICATION_CODE) {
            type = 1;
        }

        ShortMessage shortMessage = new ShortMessage(
                "", cellphone, code, type);
        OkHttpUtils.postString()
                .url(ServiceConstant.VALIDATE_VERIFICATION_CODE)
                .content(StringUtil.addModelWithJson(shortMessage))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                        onSignupFailed();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        // 第一种情况，用户已存在，要在环信登陆，下一步设置密码
                        if (response.contains(ServiceConstant.USER_PHONE)) {
                            L.i("用户已存在，客户端准备环信登录");
                            if (response.split(USER_COLON).length == 4) {
                                mRegisterId = response.split(USER_COLON)[3];
                                // 环信登录
                                easeMobLogin(mRegisterId, EASE_MOB_PASSWORD);
                            } else {
                                onSignupFailed();
                            }
                        }
                        // 第二种情况，用户不存在，需要先在护士通注册，然后在环信注册
                        else if (response.startsWith(ServiceConstant.USER_REGISTER_ID)) {
                            L.i("用户不存在，服务端准备环信注册");
                            // 用户不存在，服务端自动进行护士通注册
                            if (response.split(USER_COLON).length == 2) {
                                mRegisterId = response.split(USER_COLON)[1];
                                // 环信登陆
                                easeMobLogin(mRegisterId, EASE_MOB_PASSWORD);
                            } else {
                                onSignupFailed();
                            }
                        } else {
                            onSignupFailed();
                            T.showShort(SignupActivity.this, response);
                        }
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
     * 本地验证注册
     */
    public static String localValidate(String cellphone, String verificationCode) {

        String errorMessage = null;
        // 本地验证手机号
        if (cellphone.isEmpty()) {
            errorMessage = "手机号不能为空";
            return errorMessage;
        }

        if (verificationCode.isEmpty()) {
            errorMessage = "验证码不能为空";
            return errorMessage;
        }

        if (!AccountValidatorUtil.isMobile(cellphone)) {
            errorMessage = "手机号不合法";
            return errorMessage;
        }

        // 本地验证验证码
        if (verificationCode.length() != 6) {
            errorMessage = "验证码长度不正确";
        }

        return errorMessage;
    }

    private void resetUI() {
        mProgressDialog.dismiss();
        mSignupButton.setEnabled(true);
    }

    /**
     * 注册成功
     */
    private void onSignupSuccess() {
        if (mType != EXTRA_TYPE_VERIFICATION_CODE) {
            // 首先处理界面
            resetUI();
            T.showLong(this, R.string.signup_success);

            String cellphone = mCellphoneEditText.getText().toString();
            Intent intent = PasswordActivity.getIntent(this, "设置密码", cellphone, mRegisterId);
            startActivity(intent);
            mHandler.removeCallbacks(mValidateRunnable);
        } else {
            getUserRegisterInfo(mRegisterId);
        }
    }

    /**
     * 获取用户注册信息
     */
    private void getUserRegisterInfo(final String registerId) {
        OkHttpUtils.get().url(ServiceConstant.GET_USER_REGISTER_INFO_BY_ID)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        mSignupButton.setEnabled(true);
                        L.e("获取用户信息成功，护士通登陆");
                        UserRegisterInfo userRegisterInfo = new Gson()
                                .fromJson(response, UserRegisterInfo.class);

                        UserUtil.saveRegisterInfo(SignupActivity.this, userRegisterInfo);
                        ActivityCollector.removeAllActivity();
                        Intent intent = MainActivity.getIntent(SignupActivity.this);
                        startActivity(intent);
                    }
                });
    }

    /**
     * 注册失败
     */
    private void onSignupFailed() {
        resetUI();
        L.i("注册失败");
    }

    /**
     * 点击国家代码切换
     *
     * @param view
     */
    @OnClick(R.id.rl_country)
    void onCountry(View view) {
        if (mCountryTextView.getText().toString().split(" ").length == 2) {
            String countryCode = mCountryTextView.getText().toString().split(" ")[1];
            Intent intent = CountryActivity.getIntent(this, countryCode);
            startActivityForResult(intent, REQUEST_COUNTRY);
        }
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
            Bundle bundle = data.getExtras();
            String countryName = bundle.getString(EXTRA_COUNTRY_NAME);
            String countryCode = bundle.getString(EXTRA_COUNTRY_CODE);
            mCountryTextView.setText(countryName + " " + countryCode);
        }
    }
}
