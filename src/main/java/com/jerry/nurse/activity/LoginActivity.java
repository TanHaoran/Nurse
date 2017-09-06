package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.app.MyApplication;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.model.MicroBlog;
import com.jerry.nurse.model.MicroBlogResult;
import com.jerry.nurse.model.Qq;
import com.jerry.nurse.model.Register;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.AccountValidatorUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LoginManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.TencentLoginManager;
import com.jerry.nurse.view.PasswordEditText;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WbConnectErrorMessage;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.tencent.connect.common.Constants;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.tauth.Tencent;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.activity.CountryActivity.EXTRA_COUNTRY_CODE;
import static com.jerry.nurse.activity.SignupActivity.TYPE_FORGET_PASSWORD;
import static com.jerry.nurse.activity.SignupActivity.TYPE_REGISTER;
import static com.jerry.nurse.activity.SignupActivity.TYPE_VERIFICATION_CODE;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class LoginActivity extends BaseActivity {

    /**
     * 选择医院请求码
     */
    public static final int REQUEST_COUNTRY = 0x00000101;

    @Bind(R.id.tv_country_code)
    TextView mCountryCodeTextView;

    @Bind(R.id.cet_cellphone)
    EditText mCellphoneEditText;

    @Bind(R.id.et_password)
    PasswordEditText mPasswordEditText;

    @Bind(R.id.btn_login)
    Button mLoginButton;

    // 腾讯官方获取的APPID
    private TencentLoginManager mTencentLoginManager;

    // 用户登录信息
    private LoginInfo mLoginInfo;

    /**
     * 新浪微博
     */
    private SsoHandler mSsoHandler;

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
        // 初始化登录按钮为不可用
        setButtonEnable(this, mLoginButton, false);

        // 入口处判断用户是否已经登录，如果已经登录直接跳转到主界面
        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        if (mLoginInfo != null) {
            goToMainActivity();
        }

        // 监听密码的填写状态
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 如果手机号和密码都不为空的时候，登录按钮可用
                if (mPasswordEditText.getText().toString().length() > 0 &&
                        mCellphoneEditText.getText().toString().length() > 0) {
                    setButtonEnable(LoginActivity.this, mLoginButton, true);
                } else {
                    setButtonEnable(LoginActivity.this, mLoginButton, false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        // 初始化微博登录的对象
        mSsoHandler = new SsoHandler(this);
    }

    /**
     * 设置登录按钮的可用状态
     *
     * @param context
     * @param button  按钮
     * @param enable  是否可用
     */
    public static void setButtonEnable(Context context, Button button, boolean enable) {
        if (enable) {
            button.setEnabled(true);
            button.setBackgroundResource(R.drawable.button_bg);
        } else {
            button.setEnabled(false);
            button.setBackgroundColor(context.getResources().getColor(R.color.button_disable));
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
        // 获取国家码、手机号和密码
        String countryCode = mCountryCodeTextView.getText().toString();
        String cellphone = mCellphoneEditText.getText().toString();
        String password = mPasswordEditText.getText().toString().trim();

        //验证用户名和密码格式是否符合
        int result = localValidate(cellphone, password);
        if (result != 0) {
            T.showShort(this, result);
            return;
        }

        // 登录系统
        login(countryCode, cellphone, password);
    }


    /**
     * 本地验证登录
     */
    private int localValidate(String cellphone, String password) {
        int result = 0;

        if (cellphone.isEmpty()) {
            result = R.string.cellphone_empty;
        }
        if (!AccountValidatorUtil.isMobile(cellphone)) {
            result = R.string.cellphone_invalid;
        }

        if (password.isEmpty()) {
            result = R.string.password_empty;
        }

        // 密码的长度要介于4和10之间
        if (password.length() < 4 || password.length() > 12) {
            result = R.string.password_length_invalid;
        }
        return result;
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
        // 处理微博
        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
        // 腾讯的第三方登录
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mTencentLoginManager.getIUiListener());
            return;
        }

        // 回调正常
        if (resultCode == RESULT_OK) {
            // 获取国家码
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
        mProgressDialogManager.show("登录中...");
        // 构建登录类
        Register register = new Register(cellphone, password, countryCode);
        // 设置推送的Id
        register.setDeviceId(JPushInterface.getRegistrationID(this));
        OkHttpUtils.postString()
                .url(ServiceConstant.LOGIN)
                .content(StringUtil.addModelWithJson(register))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        LoginInfoResult result = new Gson().fromJson(response, LoginInfoResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            // 用登录管理器登录成功后保存登录信息
                            onLoginSuccess(result);
                        } else {
                            T.showShort(LoginActivity.this, result.getMsg());
                        }
                    }
                });
    }

    /**
     * 使用登录管理器登录成功后保存登录信息
     *
     * @param loginInfoResult
     */
    private void onLoginSuccess(LoginInfoResult loginInfoResult) {
        LoginManager loginManager = new LoginManager(LoginActivity.this, mProgressDialogManager);
        loginManager.saveAndEnter(loginInfoResult.getBody());
    }

    /**
     * 服务协议
     *
     * @param view
     */
    @OnClick(R.id.tv_protocol)
    void onProtocol(View view) {
        Intent intent = HtmlActivity.getIntent(this, ServiceConstant.PROTOCOL_URL, "格格服务协议");
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

    /*************************************************************************************
     *
     * 第三方登陆
     *
     ************************************************************************************/

    /**
     * 使用院内账号登录
     *
     * @param view
     */
    @OnClick(R.id.ll_in_hospital)
    void onHospitalLogin(View view) {
        Intent intent = HospitalLoginActivity.getIntent(this, HospitalLoginActivity.TYPE_LOGIN);
        startActivity(intent);
    }


    /********************************使用QQ方登录******************************************/

    /**
     * 使用qq登录
     *
     * @param view
     */
    @OnClick(R.id.ll_qq)
    void onQQ(View view) {
        // 使用腾讯登录管理器登录
        mTencentLoginManager = new TencentLoginManager(this) {

            @Override
            public void loginComplete(Qq info) {
                // 根据回传的登录信息进行系统注册(登录)
                postQQLogin(info);
            }
        };
        mTencentLoginManager.login();
    }

    /**
     * qq登录系统
     *
     * @param info
     */
    private void postQQLogin(Qq info) {
        // 设置极光推送Id
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
                        LoginInfoResult result = new Gson().fromJson(response, LoginInfoResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            // 使用登录管理器登录成功后保存登录信息
                            onLoginSuccess(result);
                        } else {
                            T.showShort(LoginActivity.this, result.getMsg());
                        }
                    }
                });
    }

    /********************************使用微信方登录*****************************************/
    /**
     * 微信登录
     *
     * @param view
     */
    @OnClick(R.id.ll_wechat)
    void onWechat(View view) {
        if (!MyApplication.sWxApi.isWXAppInstalled()) {
            // 未安装微信
            T.showShort(this, R.string.wechat_not_install);
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "diandi_wx_login";
        MyApplication.sWxApi.sendReq(req);
    }


    /******************************使用微博登录*********************************************/
    /**
     * 微博登录
     *
     * @param view
     */
    @OnClick(R.id.ll_microblog)
    void onMicroBlog(View view) {
        mSsoHandler.authorize(new SelfWbAuthListener());
    }

    private class SelfWbAuthListener implements com.sina.weibo.sdk.auth.WbAuthListener {
        @Override
        public void onSuccess(final Oauth2AccessToken token) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Oauth2AccessToken mAccessToken = token;
                    L.i("Token:" + mAccessToken.getToken());
                    L.i("Uid:" + mAccessToken.getUid());
                    if (mAccessToken.isSessionValid()) {
                        L.i("授权成功");
                        //获取个人资料
                        OkHttpUtils.get()
                                .url(ServiceConstant.MICRO_BLOG_GET_USER_INFO)
                                .addParams("access_token", mAccessToken.getToken())
                                .addParams("uid", mAccessToken.getUid())
                                .build()
                                .execute(new FilterStringCallback() {

                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        L.i("获取失败：" + e.getMessage());
                                        e.printStackTrace();
                                    }

                                    @Override
                                    public void onFilterResponse(String response, int id) {
                                        int start = response.indexOf("source");
                                        int end = response.indexOf(",\"favorited");
                                        String href = response.substring(start, end);
                                        response = response.replace(href, "source\":\"\"");
                                        L.i("处理后结果:" + response);
                                        MicroBlogResult result = new Gson().fromJson(response, MicroBlogResult.class);
                                        // 微博登录
                                        postMicroBlogLogin(result);
                                    }
                                });

                    }
                }
            });
        }

        @Override
        public void cancel() {
            L.i("取消授权");
        }

        @Override
        public void onFailure(WbConnectErrorMessage errorMessage) {
            Toast.makeText(LoginActivity.this, errorMessage.getErrorMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 微博登录系统
     *
     * @param info
     */
    private void postMicroBlogLogin(MicroBlogResult info) {
        MicroBlog microBlog = new MicroBlog();
        microBlog.setIdstr(info.getIdstr());
        microBlog.setName(info.getName());
        microBlog.setLocation(info.getLocation());
        microBlog.setDescription(info.getDescription());
        microBlog.setProfile_image_url(info.getProfile_image_url());
        microBlog.setGender(info.getGender());
        // 设置极光推送Id
        microBlog.setDeviceRegId(JPushInterface.getRegistrationID(this));
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.LOGIN_BY_WB)
                .content(StringUtil.addModelWithJson(microBlog))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        LoginInfoResult result = new Gson().fromJson(response, LoginInfoResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            // 使用登录管理器登录成功后保存登录信息
                            onLoginSuccess(result);
                        } else {
                            T.showShort(LoginActivity.this, result.getMsg());
                        }
                    }
                });
    }

}
