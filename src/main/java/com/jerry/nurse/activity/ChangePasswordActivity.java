package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Password;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;

public class ChangePasswordActivity extends BaseActivity {

    @Bind(R.id.et_origin_password)
    EditText mOriginPasswordEditText;

    @Bind(R.id.et_new_password)
    EditText mNewPasswordEditText;


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
    }

    @OnClick(R.id.acb_change_password)
    void onChangePassword(View view) {
        String originPassword = mOriginPasswordEditText.getText().toString();
        String newPassword = mNewPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(originPassword)) {
            T.showShort(this, R.string.origin_password_empty);
            return;
        }

        if (TextUtils.isEmpty(newPassword)) {
            T.showShort(this, R.string.new_password_empty);
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
        mProgressDialog.show();
        UserRegisterInfo userRegisterInfo = DataSupport.findFirst(UserRegisterInfo.class);
        Password password = new Password(userRegisterInfo.getRegisterId(), originPassword, newPassword);
        OkHttpUtils.postString()
                .url(ServiceConstant.CHANGE_PASSWORD)
                .content(StringUtil.addModelWithJson(password))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        if (response.equals(REQUEST_SUCCESS)) {
                            T.showShort(ChangePasswordActivity.this, R.string.password_change_success);
                            finish();
                        } else {
                            L.i("密码修改失败");
                        }
                    }
                });
    }
}
