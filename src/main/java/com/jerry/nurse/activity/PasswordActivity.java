package com.jerry.nurse.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Register;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import butterknife.BindString;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ExtraValue.EXTRA_CELLPHONE;
import static com.jerry.nurse.constant.ExtraValue.EXTRA_TITLE;
import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;

public class PasswordActivity extends BaseActivity {


    @Bind(R.id.tb_password)
    TitleBar mTitleBar;

    @Bind(R.id.et_password)
    EditText mPasswordEditText;

    @BindString(R.string.password_length_invalid)
    String mStringPasswordInvalid;

    private String mErrorMessage;

    private ProgressDialog mProgressDialog;

    public static Intent getIntent(Context context, String title, String cellphone) {
        Intent intent = new Intent(context, PasswordActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_CELLPHONE, cellphone);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_password;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        final String cellphone = getIntent().getStringExtra(EXTRA_CELLPHONE);
        mTitleBar.setTitle(title);

        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);

        mTitleBar.setRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                if (!localValidate()) {
                    T.showLong(PasswordActivity.this, mErrorMessage);
                    return;
                }
                String password = mPasswordEditText.getText().toString();
                // 设置密码
                setPassword(cellphone, password);
            }
        });
    }

    /**
     * 验证密码是否合法
     *
     * @return
     */
    private boolean localValidate() {
        String password = mPasswordEditText.getText().toString();
        // 本地验证密码
        if (password.isEmpty() || password.length() < 4 || password
                .length() > 10) {
            mErrorMessage = mStringPasswordInvalid;
            return false;
        } else {
            mErrorMessage = null;
        }
        return true;
    }

    /**
     * 设置密码
     *
     * @param cellphone
     * @param password
     */
    private void setPassword(String cellphone, String password) {
        mProgressDialog.show();
        Register register = new Register(cellphone, password);
        OkHttpUtils.postString()
                .url(ServiceConstant.SET_PASSWORD)
                .content(StringUtil.addModelWithJson(register))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        L.e("设置密码失败");
                        mProgressDialog.dismiss();
                        T.showLong(PasswordActivity.this, R.string.set_password_failed);
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        if (response.equals(REQUEST_SUCCESS)) {
                            L.i("设置密码成功");
                            Intent intent = MainActivity.getIntent(PasswordActivity.this);
                            startActivity(intent);
                        } else {
                            L.i("设置密码失败");
                            T.showLong(PasswordActivity.this, R.string.set_password_failed);
                        }
                    }
                });
    }
}
