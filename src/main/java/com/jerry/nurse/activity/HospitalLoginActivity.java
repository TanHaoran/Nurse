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

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.HospitalResult;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.model.Register;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.BottomDialogManager;
import com.jerry.nurse.util.LoginManager;
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

import static com.jerry.nurse.activity.LoginActivity.setButtonEnable;
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

    @Bind(R.id.btn_login)
    Button mLoginButton;

    private List<HospitalResult.Hospital> mHospitals;
    private List<String> mHospitalNames;

    private HospitalResult.Hospital mHospital;

    private int mType;

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
        setButtonEnable(this, mLoginButton, false);
        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 当所有信息都填写完毕，登录按钮可用
                if (mHospitalTextView.getText().toString().length() > 0 &&
                        mTypeTextView.getText().toString().length() > 0 &&
                        mAccountEditText.getText().toString().length() > 0 &&
                        mPasswordEditText.getText().toString().length() > 0) {
                    setButtonEnable(HospitalLoginActivity.this, mLoginButton, true);
                } else {
                    setButtonEnable(HospitalLoginActivity.this, mLoginButton, false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // 获取所有医院信息
        getHospitals();
    }

    /**
     * 获取所有医院信息
     */
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

    /**
     * 点击选择医院
     *
     * @param view
     */
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
        // 创建并显示底布弹出选择框
        BottomDialogManager dialog = new BottomDialogManager(HospitalLoginActivity.this);
        dialog.setOnItemSelectedListener(mHospitalNames, new BottomDialogManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, String item) {
                mHospitalTextView.setText(item);
                mHospital = mHospitals.get(position);
            }
        });
        dialog.showSelectDialog(mHospitalTextView.getText().toString());
        dialog.setTitle("请选择医院");
    }

    /**
     * 点击选择类别
     *
     * @param view
     */
    @OnClick(R.id.tv_type)
    void onType(View view) {
        BottomDialogManager dialog = new BottomDialogManager(this);
        List<String> items = new ArrayList<>();
        items.add("护理不良事件");
        items.add("学分");
        dialog.setOnItemSelectedListener(items, new BottomDialogManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, String item) {
                mTypeTextView.setText(item);
                mType = position;
            }
        });
        dialog.showSelectDialog(mTypeTextView.getText().toString());
        dialog.setTitle("请选择类别");
    }

    /**
     * 登录
     *
     * @param view
     */
    @OnClick(R.id.btn_login)
    void onLogin(View view) {
        String account = mAccountEditText.getText().toString();
        String password = mPasswordEditText.getText().toString();

        // 本地验证用户名和密码格式是否符合
        int result = localValidate(account, password);
        if (result != 0) {
            T.showShort(this, result);
            return;
        }

        // 远端登录
        login(account, password);
    }

    /**
     * 本地验证注册
     */
    public static int localValidate(String account, String password) {
        // 本地验证账号
        if (account.isEmpty()) {
            return R.string.account_empty;
        }

        // 本地验证验证码
        if (password.isEmpty()) {
            return R.string.password_empty;
        }

        return 0;
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
                        LoginInfoResult result = new Gson().fromJson(response, LoginInfoResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            // 登录成功并保存登录信息
                            LoginManager loginManager = new LoginManager(HospitalLoginActivity.this, null);
                            loginManager.saveAndEnter(result.getBody());
                        } else {
                            T.showShort(HospitalLoginActivity.this, result.getMsg());
                        }
                    }
                });
    }

    /**
     * 使用账号登录
     *
     * @param view
     */
    @OnClick(R.id.tv_account_login)
    void onAccountLogin(View view) {
        finish();
    }
}
