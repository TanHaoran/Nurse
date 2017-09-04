package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.LoginManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class PasswordActivity extends BaseActivity {

    private static final String EXTRA_REGISTER_ID = "extra_register_id";

    @Bind(R.id.tb_password)
    TitleBar mTitleBar;

    @Bind(R.id.et_password)
    EditText mPasswordEditText;

    private String mRegisterId;

    public static Intent getIntent(Context context, String registerId) {
        Intent intent = new Intent(context, PasswordActivity.class);
        intent.putExtra(EXTRA_REGISTER_ID, registerId);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_password;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mRegisterId = getIntent().getStringExtra(EXTRA_REGISTER_ID);

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                int result = localValidate();
                if (result != 0) {
                    T.showLong(PasswordActivity.this, result);
                    return;
                }
                String password = mPasswordEditText.getText().toString().trim();
                // 设置密码
                setPassword(mRegisterId, password);
            }
        });
    }

    /**
     * 验证密码是否合法
     *
     * @return
     */
    private int localValidate() {
        String password = mPasswordEditText.getText().toString().trim();
        // 本地验证密码
        if (password.isEmpty() || password.length() < 4 || password
                .length() > 12) {
            return R.string.password_length_invalid;
        }
        return 0;
    }

    /**
     * 设置密码
     *
     * @param registerId
     * @param password
     */
    private void setPassword(final String registerId, final String password) {
        mProgressDialogManager.show();
        UserRegisterInfo info = new UserRegisterInfo();
        info.setRegisterId(registerId);
        info.setPassword(password);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_REGISTER_INFO)
                .content(StringUtil.addModelWithJson(info))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult result = new Gson().fromJson(response, CommonResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            // 调用登陆管理器登陆并保存登陆信息
                            LoginManager loginManager = new LoginManager(PasswordActivity.this,
                                    mProgressDialogManager);
                            loginManager.getLoginInfoByRegisterId(mRegisterId);
                        } else {
                            T.showShort(PasswordActivity.this, result.getMsg());
                        }
                    }
                });
    }
}
