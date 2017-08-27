package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.model.Qq;
import com.jerry.nurse.model.Register;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.AccountValidatorUtil;
import com.jerry.nurse.util.LoginManager;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.TencentLoginUtil;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.Tencent;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;

import static com.jerry.nurse.activity.CountryActivity.EXTRA_COUNTRY_CODE;
import static com.jerry.nurse.activity.SignupActivity.TYPE_FORGET_PASSWORD;
import static com.jerry.nurse.activity.SignupActivity.TYPE_REGISTER;
import static com.jerry.nurse.activity.SignupActivity.TYPE_VERIFICATION_CODE;
import static com.jerry.nurse.activity.WXEntryActivity.WX_APP_ID;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class LoginActivity extends BaseActivity {

    public static final int REQUEST_COUNTRY = 0x00000101;

    @Bind(R.id.tv_country_code)
    TextView mCountryCodeTextView;

    @Bind(R.id.cet_cellphone)
    EditText mCellphoneEditText;

    @Bind(R.id.et_password)
    EditText mPasswordEditText;

    @Bind(R.id.btn_login)
    AppCompatButton mLoginButton;

    @BindString(R.string.password_length_invalid)
    String mStringPasswordInvalid;

    // 腾讯官方获取的APPID
    private TencentLoginUtil mTencentLoginUtil;

    private LoginInfo mLoginInfo;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_login;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);

        // 入口处判断用户是否已经登录
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        if (mLoginInfo != null) {
            goToMainActivity();
        }
    }

    /**
     * 跳转到主页面
     */
    private void goToMainActivity() {
        Intent intent = MainActivity.getIntent(LoginActivity.this);
        startActivity(intent);
        finish();
    }


    @OnClick(R.id.btn_login)
    void onLoginButton(View view) {
        String countryCode = mCountryCodeTextView.getText().toString();
        String cellphone = mCellphoneEditText.getText().toString();
        String password = mPasswordEditText.getText().toString().trim();

        //验证用户名和密码格式是否符合
        String errorMessage = localValidate(cellphone, password);
        if (errorMessage != null) {
            T.showShort(this, errorMessage);
            return;
        }

        // 第一步：登录护士通账号
        login(countryCode, cellphone, password);
    }


    /**
     * 本地验证登录
     */
    private String localValidate(String cellphone, String password) {

        if (cellphone.isEmpty()) {
            return "手机号不能为空";
        }
        if (!AccountValidatorUtil.isMobile(cellphone)) {
            return "手机号不合法";
        }

        if (password.isEmpty()) {
            return "密码不能为空";
        }

        // 密码的长度要介于4和10之间
        if (password.length() < 4 || password.length() > 12) {
            return mStringPasswordInvalid;
        }
        return null;
    }

    /**
     * 国家代码点击事件
     *
     * @param view
     */
    @OnClick(R.id.tv_country_code)
    void onCountry(View view) {
        String countryCode = mCountryCodeTextView.getText().toString();
        Intent intent = CountryActivity.getIntent(this, countryCode);
        startActivityForResult(intent, REQUEST_COUNTRY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 腾讯的第三方登录
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mTencentLoginUtil.getIUiListener());
        }

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_COUNTRY) {
                // 获取国家地区编号并显示
                Bundle bundle = data.getExtras();
                String countryCode = bundle.getString(EXTRA_COUNTRY_CODE);
                mCountryCodeTextView.setText(countryCode);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 登录
     *
     * @param countryCode
     * @param cellphone
     * @param password
     */
    private void login(String countryCode, final String cellphone, final String password) {
        mProgressDialogManager.setMessage("登录中...");
        mProgressDialogManager.show();
        Register register = new Register(cellphone, password, countryCode);
        register.setDeviceId(JPushInterface.getRegistrationID(this));
        OkHttpUtils.postString()
                .url(ServiceConstant.LOGIN)
                .content(StringUtil.addModelWithJson(register))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        final LoginInfoResult loginInfoResult = new Gson().fromJson(response, LoginInfoResult.class);
                        if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
                            LoginManager loginManager = new LoginManager(LoginActivity.this, null);
                            loginManager.saveAndEnter(loginInfoResult.getBody());
                        } else {
                            T.showShort(LoginActivity.this, loginInfoResult.getMsg());
                        }
                    }
                });
    }

    @OnClick(R.id.tv_protocol)
    void onProtocol(View view) {
        Intent intent = HtmlActivity.getIntent(this, "", "格格服务协议");
        startActivity(intent);
    }

    /**
     * 忘记密码
     *
     * @param view
     */
    @OnClick(R.id.tv_forget_password)
    void onForgetPassword(View view) {
        Intent intent = SignupActivity.getIntent(this, TYPE_FORGET_PASSWORD);
        startActivity(intent);
    }

    /**
     * 验证码登录
     *
     * @param view
     */
    @OnClick(R.id.tv_verification_code_login)
    void onVerificationCodeLogin(View view) {
        Intent intent = SignupActivity.getIntent(this, TYPE_VERIFICATION_CODE);
        startActivity(intent);
    }

    /**
     * 注册点击事件
     *
     * @param view
     */
    @OnClick(R.id.tv_signup)
    void onSignup(View view) {
        Intent intent = SignupActivity.getIntent(this, TYPE_REGISTER);
        startActivity(intent);
    }

    /********************************使用院内账号登录***************************************/
    @OnClick(R.id.ll_in_hospital)
    void onHospitalLogin(View view) {
        Intent intent = HospitalLoginActivity.getIntent(this);
        startActivity(intent);
    }


    /********************************QQ第三方登录******************************************/

    /**
     * 使用qq登录
     *
     * @param view
     */
    @OnClick(R.id.ll_qq)
    void onQQ(View view) {
        mTencentLoginUtil = new TencentLoginUtil(this) {

            @Override
            public void loginComplete(Qq info) {
                postQQLogin(info);
            }
        };
        mTencentLoginUtil.login();
    }

    /**
     * 使用qq登录
     *
     * @param info
     */
    private void postQQLogin(Qq info) {
        info.setDeviceRegId(JPushInterface.getRegistrationID(this));
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.LOGIN_BY_QQ)
                .content(StringUtil.addModelWithJson(info))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        final LoginInfoResult loginInfoResult = new Gson().fromJson(response, LoginInfoResult.class);
                        if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
                            LoginManager loginManager = new LoginManager(LoginActivity.this, mProgressDialogManager);
                            loginManager.saveAndEnter(loginInfoResult.getBody());
                        } else {
                            T.showShort(LoginActivity.this, loginInfoResult.getMsg());
                        }
                    }
                });
    }

    @OnClick(R.id.ll_wechat)
    void onWechat(View view) {
        IWXAPI WXapi = WXAPIFactory.createWXAPI(LoginActivity.this, WX_APP_ID, true);
        WXapi.registerApp(WX_APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_demo";
        WXapi.sendReq(req);
    }
}
