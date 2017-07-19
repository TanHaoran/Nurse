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
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.jerry.nurse.R;
import com.jerry.nurse.bean.User;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.T;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.connect.common.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

public class LoginActivity extends BaseActivity {

    // 注册请求码
    private static final int REQUEST_SIGNUP_CODE = 0;

    private static final int MESSAGE_LOGIN_FAILED = 0;
    private static final int MESSAGE_LOGIN_SUCCESS = 1;

    @Bind(R.id.et_cellphone)
    EditText mCellphoneEditText;

    @Bind(R.id.et_password)
    EditText mPasswordEditText;

    @Bind(R.id.btn_login)
    AppCompatButton mLoginButton;


    @BindString(R.string.cellphone_invalid)
    String mStringCellphoneInvalid;

    @BindString(R.string.password_length_invalid)
    String mStringPasswordInvalid;

    private static final String APP_ID = "1106292702";//官方获取的APPID
    private Tencent mTencent;
    private BaseUiListener mIUiListener;
    private UserInfo mUserInfo;
    private ProgressDialog mProgressDialog;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_LOGIN_SUCCESS:
                    onLoginSuccess();
                    break;
                case MESSAGE_LOGIN_FAILED:
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
        //传入参数APPID和全局Context上下文
        mTencent = Tencent.createInstance(APP_ID, this);
    }

    @OnClick(R.id.btn_login)
    void onLoginButton(View view) {
        L.i("登陆");

        //验证用户名和密码格式是否符合
//        if (!validate()) {
//            return;
//        }

        mLoginButton.setEnabled(false);

        // 创建等待框
        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("登录中...");
        mProgressDialog.show();

        String cellphone = mCellphoneEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        // TODO
//        easeMobLogin();
        onLoginSuccess();
    }

    /**
     * 本地验证登陆
     */
    private boolean validate() {
        boolean valid = true;

        String cellphone = mCellphoneEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

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
        mLoginButton.setEnabled(true);
        mProgressDialog.dismiss();
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
        mLoginButton.setEnabled(true);
        mProgressDialog.dismiss();
        T.showShort(this, R.string.login_failed);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                L.i("注册成功");
            }
        } else if (requestCode == Constants.REQUEST_LOGIN) {
            Tencent.onActivityResultData(requestCode, resultCode, data, mIUiListener);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 环信的登陆方法，是一个异步方法
     */
    private void easeMobLogin() {
        String cellphone = mCellphoneEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();
        EMClient.getInstance().login(cellphone, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                L.i("环信登陆成功");
                mHandler.sendEmptyMessage(MESSAGE_LOGIN_SUCCESS);
            }


            @Override
            public void onError(int code, String error) {
                L.i("环信登陆失败，错误码：" + code + "，错误信息：" + error);
                mHandler.sendEmptyMessage(MESSAGE_LOGIN_FAILED);
            }

            @Override
            public void onProgress(int progress, String status) {

            }
        });
    }

    /**
     * 注册按钮
     *
     * @param view
     */
    @OnClick(R.id.tv_signup)
    void onSignup(View view) {
        Intent intent = new Intent(this, SignupActivity.class);
        startActivityForResult(intent, REQUEST_SIGNUP_CODE);
    }

    /**
     * 使用qq登录
     *
     * @param view
     */
    @OnClick(R.id.tv_qq)
    void onQQ(View view) {
        /**通过这句代码，SDK实现了QQ的登录，这个方法有三个参数，第一个参数是context上下文，第二个参数SCOPO 是一个String类型的字符串，表示一些权限
         官方文档中的说明：应用需要获得哪些API的权限，由“，”分隔。例如：SCOPE = “get_user_info,add_t”；所有权限用“all”
         第三个参数，是一个事件监听器，IUiListener接口的实例，这里用的是该接口的实现类 */
        mIUiListener = new BaseUiListener();
        //all表示获取所有权限
        mTencent.login(LoginActivity.this, "all", mIUiListener);
    }

    /**
     * 自定义监听器实现IUiListener接口后，需要实现的3个方法
     * onComplete完成 onError错误 onCancel取消
     */
    private class BaseUiListener implements IUiListener {

        @Override
        public void onComplete(Object response) {
            Toast.makeText(LoginActivity.this, "授权成功", Toast.LENGTH_SHORT).show();
            L.e("response:" + response);
            JSONObject obj = (JSONObject) response;
            try {
                String openID = obj.getString("openid");
                String accessToken = obj.getString("access_token");
                String expires = obj.getString("expires_in");
                mTencent.setOpenId(openID);
                mTencent.setAccessToken(accessToken, expires);
                QQToken qqToken = mTencent.getQQToken();
                mUserInfo = new UserInfo(getApplicationContext(), qqToken);
                mUserInfo.getUserInfo(new IUiListener() {
                    @Override
                    public void onComplete(Object response) {
                        L.e("登录成功" + response.toString());
                    }

                    @Override
                    public void onError(UiError uiError) {
                        L.e("登录失败" + uiError.toString());
                    }

                    @Override
                    public void onCancel() {
                        L.e("登录取消");

                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(UiError uiError) {
            T.showShort(LoginActivity.this, "授权失败");

        }

        @Override
        public void onCancel() {
            T.showShort(LoginActivity.this, "授权取消");
        }
    }


}
