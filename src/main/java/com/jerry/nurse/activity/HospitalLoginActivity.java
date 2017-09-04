package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.HospitalResult;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.model.Register;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.BottomDialogManager;
import com.jerry.nurse.util.LoginManager;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class HospitalLoginActivity extends BaseActivity {

    @Bind(R.id.tb_login)
    TitleBar mTitleBar;

    @Bind(R.id.tv_hospital)
    TextView mHospitalTextView;

    @Bind(R.id.tv_type)
    TextView mTypeTextView;

    @Bind(R.id.et_account)
    EditText mAccountEditText;

    @Bind(R.id.et_password)
    EditText mPasswordEditText;


    private List<HospitalResult.Hospital> mHospitals;
    private List<String> mHospitalNames;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, HospitalLoginActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_hospital_login;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        mProgressDialogManager = new ProgressDialogManager(this);

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });

        getHospitals();
    }

    private void getHospitals() {
        OkHttpUtils.get().url(ServiceConstant.GET_ALL_HOSPITALS)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        HospitalResult result = new Gson().fromJson(response, HospitalResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            mHospitals = result.getBody();
                        } else {
                            T.showShort(HospitalLoginActivity.this, result.getMsg());
                        }
                    }
                });
    }

    @OnClick(R.id.tv_hospital)
    void onHospital(View view) {
        if (mHospitals == null) {
            T.showShort(this, "医院列表为空");
            return;
        } else {
            mHospitalNames = new ArrayList<>();
            for (HospitalResult.Hospital h : mHospitals) {
                mHospitalNames.add(h.getName());
            }
        }
        BottomDialogManager bottomDialogManager = new BottomDialogManager(HospitalLoginActivity.this);
        bottomDialogManager.setOnItemSelectedListener(mHospitalNames, new BottomDialogManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, String item) {
                mHospitalTextView.setText(item);
            }
        });
        bottomDialogManager.showSelectDialog(mHospitalTextView.getText().toString());
        bottomDialogManager.setTitle("请选择医院");
    }

    @OnClick(R.id.tv_type)
    void onType(View view) {
        BottomDialogManager bottomDialogManager = new BottomDialogManager(this);
        List<String> items = new ArrayList<>();
        items.add("护理不良事件");
        items.add("学分");
        bottomDialogManager.setOnItemSelectedListener(items, new BottomDialogManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, String item) {
                mTypeTextView.setText(item);
            }
        });
        bottomDialogManager.showSelectDialog(mTypeTextView.getText().toString());
        bottomDialogManager.setTitle("请选择类别");
    }

    @OnClick(R.id.acb_login)
    void onLogin(View view) {
        String account = mAccountEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        //验证用户名和密码格式是否符合
        String errorMessage = localValidate(account, password);
        if (errorMessage != null) {
            T.showShort(this, errorMessage);
            return;
        }

        // 远端登录
        login(account, password);
    }

    /**
     * 本地验证注册
     */
    public static String localValidate(String account, String password) {
        // 本地验证账号
        if (account.isEmpty()) {
            return "账号号不能为空";
        }

        // 本地验证验证码
        if (password.isEmpty()) {
            return "密码不能为空";
        }

        return null;
    }

    /**
     * 登录
     *
     * @param account
     * @param password
     */
    private void login(final String account, final String password) {
        mProgressDialogManager.show();
        Register register = new Register(account, password);
        register.setDeviceRegId(JPushInterface.getRegistrationID(this));
        OkHttpUtils.postString()
                .url(ServiceConstant.HOSPITAL_LOGIN)
                .content(StringUtil.addModelWithJson(register))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        final LoginInfoResult loginInfoResult = new Gson().fromJson(response, LoginInfoResult.class);
                        if (loginInfoResult.getCode() == RESPONSE_SUCCESS) {
                            LoginManager loginManager = new LoginManager(HospitalLoginActivity.this, null);
                            loginManager.saveAndEnter(loginInfoResult.getBody());
                        } else {
                            T.showShort(HospitalLoginActivity.this, loginInfoResult.getMsg());
                        }
                    }
                });
    }


    @OnClick(R.id.tv_account_login)
    void onAccountLogin(View view) {
        finish();
    }
}
