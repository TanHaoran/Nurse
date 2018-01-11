package com.jerry.nurse.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.model.ShortMessage;
import com.jerry.nurse.model.SignupResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.AccountValidatorUtil;
import com.jerry.nurse.util.LoginManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;

import static com.jerry.nurse.activity.CountryActivity.EXTRA_COUNTRY_CODE;
import static com.jerry.nurse.activity.CountryActivity.EXTRA_COUNTRY_NAME;
import static com.jerry.nurse.activity.LoginActivity.REQUEST_COUNTRY;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;


/**
 * Created by Jerry on 2017/7/16.
 */

public class SignupActivity extends BaseActivity {

    // 进入类型
    public static final String EXTRA_ENTER_TYPE = "extra_signup_type";

    // 进入类型：注册
    public static final int TYPE_REGISTER = 0;
    // 进入类型：验证码登录
    public static final int TYPE_VERIFICATION_CODE = 1;
    // 进入类型：忘记密码
    public static final int TYPE_FORGET_PASSWORD = 2;
    // 进入类型：修改手机号
    public static final int TYPE_CHANGE_CELLPHONE = 3;
    // 进入类型：设置新手机
    public static final int TYPE_NEW_CELLPHONE = 4;
    // 进入类型：绑定手机
    public static final int TYPE_BIND_CELLPHONE = 5;
    // 进入类型：解绑手机
    public static final int TYPE_UNBIND_CELLPHONE = 6;
    // 发送邀请短信
    public static final int TYPE_INVITE = 7;

    // 默认显示国家号码
    public static final String DEFAULT_COUNTRY_CODE = "+86";

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

    @Bind(R.id.v_block)
    View mBlock;

    // 是否同意协议
    private boolean mAgree = true;

    private int mType = TYPE_REGISTER;

    private String mRegisterId;

    /**
     * 验证码获取间隔
     */
    private int mCountdown = 60;

    private Runnable mValidateRunnable = new Runnable() {
        @Override
        public void run() {
            // 验证码倒计时
            mCountdown--;
            if (mCountdown == 0) {
                // 验证码重新归零，重置按钮状态
                mGetVerificationCodeTextView.setText(R.string.send_verification_code);
                mCountdown = 60;
                mGetVerificationCodeTextView.setEnabled(true);
                mGetVerificationCodeTextView.setTextColor(
                        ContextCompat.getColor(SignupActivity.this, R.color.primary));
                mHandler.removeCallbacks(mValidateRunnable);
            } else {
                mGetVerificationCodeTextView.setText("(" + mCountdown + "秒)");
                mHandler.postDelayed(mValidateRunnable, 1000);
            }
        }
    };

    private Handler mHandler = new Handler();

    public static Intent getIntent(Context context, int type) {
        Intent intent = new Intent(context, SignupActivity.class);
        intent.putExtra(EXTRA_ENTER_TYPE, type);

        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_signup;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // 初始化登录按钮不可用
        LoginActivity.setButtonEnable(this, mSignupButton, false);

        // 根据mType判断进入类型
        mType = getIntent().getIntExtra(EXTRA_ENTER_TYPE, -1);
        switch (mType) {
            // 注册
            case TYPE_REGISTER:
                mTitleBar.setTitle("新用户注册");
                break;
            // 验证码登录
            case TYPE_VERIFICATION_CODE:
                mTitleBar.setTitle("验证码登录");
                mProtocolLayout.setVisibility(View.GONE);
                mSignupButton.setText(R.string.login);
                mBlock.setVisibility(View.VISIBLE);
                break;
            // 忘记密码
            case TYPE_FORGET_PASSWORD:
                mTitleBar.setTitle("忘记密码");
                mProtocolLayout.setVisibility(View.GONE);
                mSignupButton.setText(R.string.next_step);
                mBlock.setVisibility(View.VISIBLE);
                break;
        }
        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });

        mVerificationCodeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 当手机号和验证码有值的时候可以点击注册按钮
                if (mCellphoneEditText.getText().toString().length() > 0 &&
                        mVerificationCodeEditText.getText().toString().length() > 0) {
                    LoginActivity.setButtonEnable(SignupActivity.this, mSignupButton, true);
                } else {
                    LoginActivity.setButtonEnable(SignupActivity.this, mSignupButton, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * 发送验证码点击事件
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

        // 读取国家代码
        String countryCode = DEFAULT_COUNTRY_CODE;
        if (mCountryTextView.getText().toString().split(" ").length == 2) {
            countryCode = mCountryTextView.getText().toString().split(" ")[1];
        }

        // 向服务端发送请求，接收验证码
        try {
            countryCode = URLEncoder.encode(countryCode, "UTF-8");
            mProgressDialogManager.show("发送验证码...");
            OkHttpUtils.get().url(ServiceConstant.GET_VERIFICATION_CODE)
                    .addParams("Phone", cellphone)
                    .addParams("CountryCode", countryCode)
                    .addParams("Type", String.valueOf(mType))
                    .build()
                    .execute(new FilterStringCallback(mProgressDialogManager) {

                        @Override
                        public void onFilterResponse(String response, int id) {
                            CommonResult result = new Gson().fromJson(response, CommonResult.class);
                            if (result.getCode() == RESPONSE_SUCCESS) {
                                // 聚焦到验证码输入框
                                mVerificationCodeEditText.requestFocus();
                                // 控制发送验证码的状态
                                mGetVerificationCodeTextView.setEnabled(false);
                                mGetVerificationCodeTextView
                                        .setTextColor(ContextCompat
                                                .getColor(SignupActivity
                                                        .this, R.color.gray_textColor));
                                mGetVerificationCodeTextView.setText("(" + mCountdown + "秒)");
                                mHandler.postDelayed(mValidateRunnable, 1000);
                                T.showLong(SignupActivity.this, R.string.get_verification_finished);
                            } else {
                                T.showLong(SignupActivity.this, result.getMsg());
                            }
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 注册按钮点击事件
     *
     * @param view
     */
    @OnClick(R.id.btn_signup)
    void onSignup(View view) {
        // 本地验证用户名和密码格式是否符合
        String cellphone = mCellphoneEditText.getText().toString();
        String verificationCode = mVerificationCodeEditText.getText().toString();

        int result = localValidate(cellphone, verificationCode);

        if (result != 0) {
            T.showShort(this, result);
            return;
        }

        // 勾选同意
        if (!mAgree) {
            T.showLong(this, R.string.please_agree);
            return;
        }

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
        ShortMessage shortMessage = new ShortMessage("", cellphone, code, mType);
        shortMessage.setDeviceRegId(JPushInterface.getRegistrationID(this));
        // 发送请求
        mProgressDialogManager.show("请稍后");
        OkHttpUtils.postString()
                .url(ServiceConstant.VALIDATE_VERIFICATION_CODE)
                .content(StringUtil.addModelWithJson(shortMessage))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        switch (mType) {
                            // 注册
                            // 忘记密码
                            case TYPE_REGISTER:
                            case TYPE_FORGET_PASSWORD:
                                SignupResult signupResult = new Gson().fromJson(response, SignupResult.class);
                                if (signupResult.getCode() == RESPONSE_SUCCESS) {
                                    // 验证成功
                                    mRegisterId = signupResult.getBody().getRegisterId();
                                    Intent intent = PasswordActivity.getIntent(SignupActivity.this, mRegisterId);
                                    startActivity(intent);
                                } else {
                                    T.showShort(SignupActivity.this, signupResult.getMsg());
                                }
                                break;
                            // 验证码登录
                            case TYPE_VERIFICATION_CODE:
                                LoginInfoResult loginResult = new Gson().fromJson(response, LoginInfoResult.class);
                                LoginManager loginManager = new LoginManager(SignupActivity.this, null);
                                loginManager.saveAndEnter(loginResult);
                                break;
                            default:
                                break;

                        }
                    }
                });
    }


    /**
     * 本地验证注册
     */
    public static int localValidate(String cellphone, String
            verificationCode) {
        // 本地验证手机号
        if (cellphone.isEmpty()) {
            return R.string.cellphone_empty;
        }
        if (!AccountValidatorUtil.isMobile(cellphone)) {
            return R.string.cellphone_invalid;
        }

        // 本地验证验证码
        if (verificationCode.isEmpty()) {
            return R.string.verification_empty;
        }

        if (verificationCode.length() != 6) {
            return R.string.verification_code_length_invalid;
        }
        return 0;
    }

    /**
     * 国家代码点击事件
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

    /**
     * 燕尾帽协议
     *
     * @param view
     */
    @OnClick(R.id.tv_protocol)
    void onProtocol(View view) {
        Intent intent = HtmlActivity.getIntent(this, ServiceConstant.PROTOCOL_URL, "服务协议");
        startActivity(intent);
    }

    /**
     * 同意协议
     *
     * @param v
     */
    @OnClick({R.id.ll_agree, R.id.tv_agree})
    void onAgree(View v) {
        if (mAgree) {
            mAgreeImageView.setVisibility(View.INVISIBLE);
        } else {
            mAgreeImageView.setVisibility(View.VISIBLE);
        }
        mAgree = !mAgree;
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
