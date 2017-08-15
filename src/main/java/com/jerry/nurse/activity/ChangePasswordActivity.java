package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.Password;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class ChangePasswordActivity extends BaseActivity {

    @Bind(R.id.et_origin_password)
    EditText mOriginPasswordEditText;

    @Bind(R.id.et_new_password)
    EditText mNewPasswordEditText;
    private ProgressDialogManager mProgressDialogManager;


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_change_password;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);
    }

    @OnClick(R.id.acb_change_password)
    void onChangePassword(View view) {
        String originPassword = mOriginPasswordEditText.getText().toString().trim();
        String newPassword = mNewPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(originPassword)) {
            T.showShort(this, R.string.origin_password_empty);
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            T.showShort(this, R.string.new_password_empty);
            return;
        }

        if (originPassword.length() < 4 || originPassword.length() > 12) {
            T.showShort(this, "原密码长度应在4和12之间");
            return;
        }

        if (newPassword.length() < 4 || newPassword.length() > 12) {
            T.showShort(this, "新密码长度应在4和12之间");
            return;
        }

        if (originPassword.equals(newPassword)) {
            T.showShort(this, R.string.password_same);
            return;
        }

        changePassword(originPassword, newPassword);

    }

    /**
     * @param originPassword
     * @param newPassword
     */
    private void changePassword(String originPassword, String newPassword) {
        mProgressDialogManager.show();
        String registerId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        Password password = new Password(registerId, originPassword, newPassword);
        OkHttpUtils.postString()
                .url(ServiceConstant.CHANGE_PASSWORD)
                .content(StringUtil.addModelWithJson(password))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            T.showShort(ChangePasswordActivity.this, R.string.password_change_success);
                            finish();
                        } else {
                            T.showShort(ChangePasswordActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }
}
