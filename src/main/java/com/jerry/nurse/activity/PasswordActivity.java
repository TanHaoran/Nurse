package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Register;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.UserUtil;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import butterknife.Bind;
import butterknife.BindString;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;

public class PasswordActivity extends BaseActivity {


    private static final String EXTRA_TITLE = "extra_title";
    private static final String EXTRA_CELLPHONE = "extra_cellphone";
    private static final String EXTRA_REGISTER_ID = "extra_register_id";

    @Bind(R.id.tb_password)
    TitleBar mTitleBar;

    @Bind(R.id.et_password)
    EditText mPasswordEditText;

    @BindString(R.string.password_length_invalid)
    String mStringPasswordInvalid;

    private String mErrorMessage;


    private String mCellphone;
    private String mRegisterId;

    public static Intent getIntent(Context context, String title, String cellphone, String registerId) {
        Intent intent = new Intent(context, PasswordActivity.class);
        intent.putExtra(EXTRA_TITLE, title);
        intent.putExtra(EXTRA_CELLPHONE, cellphone);
        intent.putExtra(EXTRA_REGISTER_ID, registerId);

        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_password;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        String title = getIntent().getStringExtra(EXTRA_TITLE);
        mCellphone = getIntent().getStringExtra(EXTRA_CELLPHONE);
        mRegisterId = getIntent().getStringExtra(EXTRA_REGISTER_ID);
        mTitleBar.setTitle(title);

        mTitleBar.setRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                if (!localValidate()) {
                    T.showLong(PasswordActivity.this, mErrorMessage);
                    return;
                }
                String password = mPasswordEditText.getText().toString();
                // 设置密码
                setPassword(mCellphone, password);
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
    private void setPassword(final String cellphone, String password) {
        mProgressDialog.setMessage("请稍后...");
        mProgressDialog.show();
        Register register = new Register(cellphone, password);
        OkHttpUtils.postString()
                .url(ServiceConstant.SET_PASSWORD)
                .content(StringUtil.addModelWithJson(register))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                        T.showLong(PasswordActivity.this, R.string.set_password_failed);
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        if (response.equals(REQUEST_SUCCESS)) {
                            L.i("设置密码成功");
                            getUserRegisterInfo(mRegisterId);
                        } else {
                            L.i("设置密码失败");
                            mProgressDialog.dismiss();
                        }
                    }
                });
    }


    /**
     * 获取用户注册信息
     */
    private void getUserRegisterInfo(final String registerId) {
        OkHttpUtils.get().url(ServiceConstant.GET_USER_REGISTER_INFO)
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
                        L.e("获取用户注册信息成功，护士通登陆");

                        UserRegisterInfo userRegisterInfo = new Gson().fromJson(response, UserRegisterInfo.class);
                        onSuccess(userRegisterInfo);
                    }
                });
    }

    /**
     * 设置密码成功后执行
     *
     * @param userRegisterInfo
     */
    private void onSuccess(UserRegisterInfo userRegisterInfo) {
        // 首先处理界面

        UserUtil.saveRegisterInfo(this, userRegisterInfo);

        ActivityCollector.removeAllActivity();
        Intent intent = MainActivity.getIntent(this);
        startActivity(intent);
    }
}
