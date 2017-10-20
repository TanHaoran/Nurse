package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.HospitalResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.LoginInfoResult;
import com.jerry.nurse.model.Register;
import com.jerry.nurse.model.ThirdPartInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.BaiduLocationManager;
import com.jerry.nurse.util.BottomDialogManager;
import com.jerry.nurse.util.LitePalUtil;
import com.jerry.nurse.util.LoginManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import okhttp3.MediaType;

import static com.jerry.nurse.activity.LoginActivity.setButtonEnable;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;
import static com.jerry.nurse.model.ThirdPartInfo.TYPE_CREDIT;
import static com.jerry.nurse.model.ThirdPartInfo.TYPE_EVENT_REPORT;
import static com.jerry.nurse.model.ThirdPartInfo.TYPE_SCHEDULE;
import static com.jerry.nurse.util.T.showShort;

public class HospitalLoginActivity extends BaseActivity {

    public static final int TYPE_LOGIN = 0;
    public static final int TYPE_BIND = 1;

    private static final String EVENT_REPORT = "护理不良事件";
    private static final String CREDIT = "学分";
    private static final String SCHEDULE = "排班";


    public static final String EXTRA_TYPE = "extra_type";
    public static final String EXTRA_ACCOUNT_TYPE = "extra_account_type";

    @Bind(R.id.rl_hospital)
    RelativeLayout mHospitalLayout;

    @Bind(R.id.rl_type)
    RelativeLayout mTypeLayout;

    @Bind(R.id.iv_logo)
    ImageView mLogoImageView;

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

    @Bind(R.id.tv_account_login)
    TextView mAccountLoginTextView;

    private List<HospitalResult.Hospital> mHospitals;
    private List<String> mHospitalNames;

    private HospitalResult.Hospital mHospital;

    // 登录或者绑定
    private int mType;
    // 账号类型
    private int mAccountType;

    private BaiduLocationManager mLocationManager;

    public static Intent getIntent(Context context, int type, int accountType) {
        Intent intent = new Intent(context, HospitalLoginActivity.class);
        intent.putExtra(EXTRA_TYPE, type);
        intent.putExtra(EXTRA_ACCOUNT_TYPE, accountType);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_hospital_login;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mType = getIntent().getIntExtra(EXTRA_TYPE, 0);
        mAccountType = getIntent().getIntExtra(EXTRA_ACCOUNT_TYPE, 0);
        if (mType == TYPE_LOGIN) {
            mLogoImageView.setVisibility(View.GONE);
        } else {
            mLogoImageView.setVisibility(View.VISIBLE);

            switch (mAccountType) {
                case TYPE_EVENT_REPORT:
                    mHospitalLayout.setVisibility(View.GONE);
                    mTitleBar.setTitle("护理不良事件账号绑定");
                    setupBindState();
                    break;
                case TYPE_CREDIT:
                    mTitleBar.setTitle("学分账号绑定");
                    mHospitalLayout.setVisibility(View.VISIBLE);
                    setupBindState();
                    break;
                case TYPE_SCHEDULE:
                    mTitleBar.setTitle("考试账号绑定");
                    mHospitalLayout.setVisibility(View.VISIBLE);
                    setupBindState();
                    break;
            }
        }
        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });
        mAccountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 检测信息是否填写完整
                checkIfEmpty();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // 检测信息是否填写完整
                checkIfEmpty();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mLocationManager = new BaiduLocationManager(this);
        mLocationManager.setLocationListener(new BaiduLocationManager.LocationListener() {
            @Override
            public void onLocationFinished(double latitude, double longitude) {
                // 获取所有医院信息
                getHospitals(latitude, longitude);
            }
        });

        mLocationManager.start();
    }

    private void setupBindState() {
        mTypeLayout.setVisibility(View.GONE);
        mLoginButton.setText(R.string.bind);
        mLogoImageView.setVisibility(View.VISIBLE);
        mTitleBar.setRightText("");
        mTitleBar.showBack(true);
        mAccountLoginTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * 检测信息是否填写完整
     */
    private void checkIfEmpty() {
        // 当所有信息都填写完毕，登录按钮可用
        if (mTypeTextView.getText().toString().length() > 0 &&
                mAccountEditText.getText().toString().length() > 0 &&
                mPasswordEditText.getText().toString().length() > 0) {
            if (mHospitalLayout.getVisibility() == View.VISIBLE) {
                if (mHospitalTextView.getText().toString().length() > 0) {
                    setButtonEnable(HospitalLoginActivity.this, mLoginButton, true);
                } else {
                    setButtonEnable(HospitalLoginActivity.this, mLoginButton, false);
                }
            } else {
                setButtonEnable(HospitalLoginActivity.this, mLoginButton, true);
            }
        } else {
            setButtonEnable(HospitalLoginActivity.this, mLoginButton, false);
        }
        if (mType == TYPE_BIND) {
            if (mHospitalLayout.getVisibility() == View.VISIBLE) {
                if (mHospitalTextView.getText().toString().length() > 0 &&
                        mAccountEditText.getText().toString().length() > 0 &&
                        mPasswordEditText.getText().toString().length() > 0) {
                    setButtonEnable(HospitalLoginActivity.this, mLoginButton, true);
                }
            } else if (mAccountEditText.getText().toString().length() > 0 &&
                    mPasswordEditText.getText().toString().length() > 0) {
                setButtonEnable(HospitalLoginActivity.this, mLoginButton, true);
            }
        }
    }

    /**
     * 获取所有医院信息
     *
     * @param latitude
     * @param longitude
     */
    private void getHospitals(double latitude, double longitude) {
        OkHttpUtils.get().url(ServiceConstant.GET_NEARBY_HOSPITAL_LIST)
                .addParams("lat", String.valueOf(latitude))
                .addParams("lng", String.valueOf(longitude))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        HospitalResult result = new Gson().fromJson(response, HospitalResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            mHospitals = result.getBody();
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
            showShort(this, "医院列表为空");
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
                // 检测信息是否填写完整
                checkIfEmpty();
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
        items.add(EVENT_REPORT);
        items.add(CREDIT);
        items.add(SCHEDULE);
        dialog.setOnItemSelectedListener(items, new BottomDialogManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, String item) {
                mTypeTextView.setText(item);
                switch (item) {
                    case EVENT_REPORT:
                        mAccountType = ThirdPartInfo.TYPE_EVENT_REPORT;
                        mHospitalLayout.setVisibility(View.GONE);
                        break;
                    case CREDIT:
                        mAccountType = ThirdPartInfo.TYPE_CREDIT;
                        mHospitalLayout.setVisibility(View.VISIBLE);
                    case SCHEDULE:
                        mAccountType = ThirdPartInfo.TYPE_SCHEDULE;
                        mHospitalLayout.setVisibility(View.VISIBLE);
                        break;
                }

                // 检测信息是否填写完整
                checkIfEmpty();
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
            showShort(this, result);
            return;
        }

        // 判断是登录还是绑定

        if (mType == TYPE_LOGIN) {
            login(account, password);
        } else {
            String registerId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
            ThirdPartInfo thirdPartInfo = new ThirdPartInfo();
            thirdPartInfo.setRegisterId(registerId);
            thirdPartInfo.setLoginType(mAccountType);
            if (mAccountType != ThirdPartInfo.TYPE_EVENT_REPORT) {
                thirdPartInfo.setHospitalId(mHospital.getHospitalId());
            }
            thirdPartInfo.setLoginName(account);
            thirdPartInfo.setPassword(password);
            bind(thirdPartInfo);
        }
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
        if (mAccountType != TYPE_EVENT_REPORT) {
            register.setHospitalId(mHospital.getHospitalId());
        }
        register.setLoginType(mAccountType);
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
                            showShort(HospitalLoginActivity.this, result.getMsg());
                        }
                    }
                });
    }

    /**
     * 绑定账号
     *
     * @param thirdPartInfo
     */
    private void bind(final ThirdPartInfo thirdPartInfo) {
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.BIND)
                .content(StringUtil.addModelWithJson(thirdPartInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        LoginInfoResult result = new Gson().fromJson(response, LoginInfoResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            LoginInfo loginInfo = DataSupport.findFirst(LoginInfo.class);
                            if (!TextUtils.isEmpty(result.getBody().getReguserId())) {
                                loginInfo.setReguserId(result.getBody().getReguserId());
                            } else if (!TextUtils.isEmpty(result.getBody().getXFId())) {
                                loginInfo.setXFId(result.getBody().getXFId());
                            } else if (!TextUtils.isEmpty(result.getBody().getPBId())) {
                                loginInfo.setPBId(result.getBody().getPBId());
                            }
                            loginInfo.setDepartmentUserCount(result.getBody().getDepartmentUserCount());
                            loginInfo.setName(result.getBody().getName());
                            loginInfo.setDepartmentId(result.getBody().getDepartmentId());
                            loginInfo.setDepartmentName(result.getBody().getDepartmentName());
                            loginInfo.setHospitalId(result.getBody().getHospitalId());
                            loginInfo.setHospitalName(result.getBody().getHospitalName());
                            LitePalUtil.updateLoginInfo(HospitalLoginActivity.this, loginInfo);
                            T.showShort(HospitalLoginActivity.this, "绑定成功");
                            finish();
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


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mLocationManager.isStarted()) {
            mLocationManager.stop();
        }
    }
}
