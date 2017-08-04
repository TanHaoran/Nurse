package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.QQUserInfo;
import com.jerry.nurse.model.Register;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.AccountValidatorUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.TencentLoginUtil;
import com.jerry.nurse.util.UserUtil;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.Tencent;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.activity.SignupActivity.EXTRA_TYPE_FORGET_PASSWORD;
import static com.jerry.nurse.activity.SignupActivity.EXTRA_TYPE_REGISTER;
import static com.jerry.nurse.activity.SignupActivity.EXTRA_TYPE_VERIFICATION_CODE;
import static com.jerry.nurse.constant.ServiceConstant.EASE_MOB_PASSWORD;
import static com.jerry.nurse.constant.ServiceConstant.USER_COLON;
import static com.jerry.nurse.constant.ServiceConstant.USER_REGISTER_ID;

public class LoginActivity extends BaseActivity {

    private static final int MESSAGE_EASE_MOB_LOGIN_FAILED = 0;
    private static final int MESSAGE_EASE_MOB_LOGIN_SUCCESS = 1;

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

    private UserRegisterInfo mUserRegisterInfo;

    private String mErrorMessage;

    private ProgressDialogManager mProgressDialogManager;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_EASE_MOB_LOGIN_SUCCESS:
                    String cellphone = mCellphoneEditText.getText().toString();
                    getUserInfoByPhone(cellphone);
                    break;
                case MESSAGE_EASE_MOB_LOGIN_FAILED:
                    onLoginFailed();
                    break;
                default:
                    break;
            }
        }
    };

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

        // 入口处判断用户是否已经登陆
        String registerId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "-1");
        if (!registerId.equals("-1")) {
            Intent intent = MainActivity.getIntent(this);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.btn_login)
    void onLoginButton(View view) {
        String cellphone = mCellphoneEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        //验证用户名和密码格式是否符合
        if (!localValidate(cellphone, password)) {
            if (mErrorMessage != null) {
                T.showShort(this, mErrorMessage);
            }
            return;
        }

        mLoginButton.setEnabled(false);

        mProgressDialogManager.setMessage("登录中...");

        // 第一步：登陆护士通账号
        login(cellphone, password);
    }


    /**
     * 本地验证登陆
     */
    private boolean localValidate(String cellphone, String password) {

        if (cellphone.isEmpty()) {
            mErrorMessage = "手机号不能为空";
            return false;
        } else {
            mErrorMessage = null;
        }

        if (!AccountValidatorUtil.isMobile(cellphone)) {
            mErrorMessage = "手机号不合法";
            return false;
        }

        if (password.isEmpty()) {
            mErrorMessage = "密码不能为空";
            return false;
        } else {
            mErrorMessage = null;
        }

        // 密码的长度要介于4和10之间
        if (password.length() < 4 || password.length() > 10) {
            mErrorMessage = mStringPasswordInvalid;
            return false;
        } else {
            mErrorMessage = null;
        }

        return true;
    }

    private void onLoginSuccess() {
        resetUI();
        // 登陆成功后，保存用户数据信息
        UserUtil.saveRegisterInfo(this, mUserRegisterInfo);

        Intent intent = MainActivity.getIntent(this);
        startActivity(intent);
        finish();
    }

    private void resetUI() {
        mProgressDialogManager.dismiss();
        mLoginButton.setEnabled(true);
    }


    private void onLoginFailed() {
        resetUI();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 腾讯的第三方登陆
        if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mTencentLoginUtil.getIUiListener());
        }
        super.onActivityResult(requestCode, resultCode, data);
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
     * 登录
     *
     * @param cellphone
     * @param password
     */
    private void login(final String cellphone, final String password) {

        Register register = new Register(cellphone, password);
        OkHttpUtils.postString()
                .url(ServiceConstant.LOGIN)
                .content(StringUtil.addModelWithJson(register))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        onLoginFailed();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        // 判断登陆是否成功
                        if (response.startsWith(USER_REGISTER_ID)) {
                            if (response.split(":").length == 2) {
                                String registerId = response.split(":")[1];
                                easeMobLogin(registerId, EASE_MOB_PASSWORD);
                            } else {
                                L.i("护士通登录失败");
                                T.showShort(LoginActivity.this, response);
                                onLoginFailed();
                            }
                        } else {
                            L.i("护士通登录失败");
                            T.showShort(LoginActivity.this, response);
                            onLoginFailed();
                        }
                    }
                });
    }

    /**
     * 获取用户信息
     */
    private void getUserInfoByPhone(final String cellphone) {
        OkHttpUtils.get().url(ServiceConstant.GET_USER_REGISTER_INFO_BY_PHONE)
                .addParams("Phone", cellphone)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        onLoginFailed();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mUserRegisterInfo = new Gson().fromJson(response, UserRegisterInfo.class);
                        onLoginSuccess();
                    }
                });
    }

    /**
     * 获取用户信息
     */
    private void getUserInfoByRegisterId(final String registerId) {
        OkHttpUtils.get().url(ServiceConstant.GET_USER_REGISTER_INFO_BY_ID)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        onLoginFailed();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mUserRegisterInfo = new Gson().fromJson(response, UserRegisterInfo.class);
                        onLoginSuccess();
                    }
                });
    }


    @OnClick(R.id.tv_protocol)
    void onProtocol(View view) {
        Intent intent = HtmlActivity.getIntent(this, "");
        startActivity(intent);
    }

    /**
     * 忘记密码
     *
     * @param view
     */
    @OnClick(R.id.tv_forget_password)
    void onForgetPassword(View view) {
        Intent intent = SignupActivity.getIntent(this, EXTRA_TYPE_FORGET_PASSWORD);
        startActivity(intent);
    }

    /**
     * 验证码登陆
     *
     * @param view
     */
    @OnClick(R.id.tv_verification_code_login)
    void onVerificationCodeLogin(View view) {
        Intent intent = SignupActivity.getIntent(this, EXTRA_TYPE_VERIFICATION_CODE);
        startActivity(intent);
    }

    /**
     * 注册按钮
     *
     * @param view
     */
    @OnClick(R.id.tv_signup)
    void onSignup(View view) {
        Intent intent = SignupActivity.getIntent(this, EXTRA_TYPE_REGISTER);
        startActivity(intent);
    }

    /********************************QQ第三方登陆********************************************

     /**
     * 使用qq登录
     *
     * @param view
     */
    @OnClick(R.id.ll_qq)
    void onQQ(View view) {
        mTencentLoginUtil = new TencentLoginUtil(this) {

            @Override
            public void loginComplete(QQUserInfo info) {
                postQQLogin(info);
            }
        };
        mTencentLoginUtil.login();
    }

    /**
     * 使用qq登陆
     *
     * @param info
     */
    private void postQQLogin(QQUserInfo info) {
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.LOGIN_BY_QQ)
                .content(StringUtil.addModelWithJson(info))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialogManager.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialogManager.dismiss();
                        if (response.startsWith(USER_REGISTER_ID)) {
                            L.i("qq授权应用注册成功");
                            if (response.split(USER_COLON).length == 2) {
                                String registerId = response.split(USER_COLON)[1];
                                getUserInfoByRegisterId(registerId);
                            } else {
                                T.showShort(LoginActivity.this, response);
                            }
                        }
                    }
                });
    }
}
