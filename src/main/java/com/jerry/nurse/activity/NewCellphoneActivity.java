package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.ShortMessage;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.AccountValidatorUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import butterknife.Bind;
import butterknife.BindColor;
import butterknife.OnClick;
import okhttp3.MediaType;

import static com.jerry.nurse.activity.CountryActivity.EXTRA_COUNTRY_CODE;
import static com.jerry.nurse.activity.CountryActivity.EXTRA_COUNTRY_NAME;
import static com.jerry.nurse.activity.SignupActivity.DEFAULT_COUNTRY_CODE;
import static com.jerry.nurse.activity.SignupActivity.TYPE_NEW_CELLPHONE;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class NewCellphoneActivity extends BaseActivity {

    private static final int REQUEST_COUNTRY = 0x00000101;

    @Bind(R.id.tv_country)
    TextView mCountryTextView;

    @Bind(R.id.cet_cellphone)
    EditText mCellphoneEditText;

    @Bind(R.id.et_verification_code)
    EditText mVerificationCodeEditText;

    @Bind(R.id.tv_send_verification_code)
    TextView mGetVerificationCodeTextView;

    @Bind(R.id.acb_next)
    AppCompatButton mNextButton;

    @BindColor(R.color.primary)
    int mPrimaryColor;

    @BindColor(R.color.gray_textColor)
    int mGrayColor;

    /**
     * 验证码获取间隔
     */
    private int mValidateCountDown = 60;

    private Runnable mValidateRunnable = new Runnable() {
        @Override
        public void run() {
            // 验证码倒计时
            mValidateCountDown--;
            if (mValidateCountDown == 0) {
                mGetVerificationCodeTextView.setText(R.string.send_verification_code);
                mValidateCountDown = 60;
                mGetVerificationCodeTextView.setEnabled(true);
                mGetVerificationCodeTextView.setTextColor(mPrimaryColor);
                mHandler.removeCallbacks(mValidateRunnable);
            } else {
                mGetVerificationCodeTextView.setText("(" + mValidateCountDown + "秒)");
                mHandler.postDelayed(mValidateRunnable, 1000);
            }
        }
    };

    private Handler mHandler = new Handler();
    private ProgressDialogManager mProgressDialogManager;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, NewCellphoneActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_new_cellphone;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        mProgressDialogManager = new ProgressDialogManager(this);

    }

    @OnClick(R.id.tv_send_verification_code)
    void onSendVerificationCode(View view) {
        // 首先验证手机号是否为空和是否合法
        String cellphone = mCellphoneEditText.getText().toString();
        if (cellphone.isEmpty()) {
            T.showLong(this, R.string.cellphone_empty);
            return;
        }
        if (!AccountValidatorUtil.isMobile(cellphone)) {
            T.showLong(this, R.string.cellphone_invalid);
            return;
        }

        // 读取国家代码
        String countryCode = DEFAULT_COUNTRY_CODE;
        if (mCountryTextView.getText().toString().split(" ").length == 2) {
            countryCode = mCountryTextView.getText().toString().split(" ")[1];
        }

        try {
            mProgressDialogManager.show();
            countryCode = URLEncoder.encode(countryCode, "UTF-8");
            OkHttpUtils.get().url(ServiceConstant.GET_VERIFICATION_CODE)
                    .addParams("Phone", cellphone)
                    .addParams("CountryCode", countryCode)
                    .addParams("Type", String.valueOf(TYPE_NEW_CELLPHONE))
                    .build()
                    .execute(new FilterStringCallback(mProgressDialogManager) {


                        @Override
                        public void onFilterResponse(String response, int id) {
                            CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                            if (commonResult.getCode() == RESPONSE_SUCCESS) {
                                // 控制发送验证码的状态
                                mGetVerificationCodeTextView.setEnabled(false);
                                mGetVerificationCodeTextView.setTextColor(mGrayColor);
                                mGetVerificationCodeTextView.setText("(" + mValidateCountDown + "秒)");
                                mHandler.postDelayed(mValidateRunnable, 1000);
                                T.showLong(NewCellphoneActivity.this, R.string.get_verification_finished);
                            } else {
                                T.showLong(NewCellphoneActivity.this, commonResult.getMsg());
                            }
                        }
                    });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @OnClick(R.id.acb_next)
    void onNext(View view) {
        // 本地验证用户名和密码格式是否符合
        String cellphone = mCellphoneEditText.getText().toString();
        String verificationCode = mVerificationCodeEditText.getText().toString();

        String errorMessage = SignupActivity.localValidate(cellphone, verificationCode);
        if (errorMessage != null) {
            T.showLong(this, errorMessage);
            return;
        }

        mNextButton.setEnabled(false);

        // 远端服务验证
        validateVerificationCode(cellphone, verificationCode);
    }

    /**
     * 验证验证码服务
     *
     * @param cellphone
     * @param code
     */
    private void validateVerificationCode(final String cellphone, String code) {
        mProgressDialogManager.show();
        String registerId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        ShortMessage shortMessage = new ShortMessage(
                registerId, cellphone, code, TYPE_NEW_CELLPHONE);
        OkHttpUtils.postString()
                .url(ServiceConstant.VALIDATE_VERIFICATION_CODE)
                .content(StringUtil.addModelWithJson(shortMessage))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            String cellphone = mCellphoneEditText.getText().toString();
                            postNewCellphone(cellphone);
                        } else {
                            T.showShort(NewCellphoneActivity.this, R.string.validate_failed);
                        }
                        mNextButton.setEnabled(true);
                    }
                });
    }

    /**
     * 设置新手机号
     *
     * @param cellphone
     */
    void postNewCellphone(String cellphone) {
        mProgressDialogManager.show();
        String registerId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        UserRegisterInfo userRegisterInfo = new UserRegisterInfo();
        userRegisterInfo.setPhone(cellphone);
        userRegisterInfo.setRegisterId(registerId);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_REGISTER_INFO)
                .content(StringUtil.addModelWithJson(userRegisterInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            L.i("设置手机号成功");
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            L.i("设置昵称失败");
                        }
                    }
                });
    }

    /**
     * 国家代码点击事件
     *
     * @param view
     */
    @OnClick(R.id.rl_country)
    void onCountry(View view) {
        if (mCountryTextView.getText().toString().split(" ").length == 2) {
            String countryCode = mCountryTextView.getText().toString().split(" ")[1];
            Intent intent = CountryActivity.getIntent(this, countryCode);
            startActivityForResult(intent, REQUEST_COUNTRY);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_COUNTRY) {
                // 获取国家地区编号并显示
                Bundle bundle = data.getExtras();
                String countryName = bundle.getString(EXTRA_COUNTRY_NAME);
                String countryCode = bundle.getString(EXTRA_COUNTRY_CODE);
                mCountryTextView.setText(countryName + " " + countryCode);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mValidateRunnable);
    }
}
