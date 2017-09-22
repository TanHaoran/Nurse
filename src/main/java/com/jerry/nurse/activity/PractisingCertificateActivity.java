package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.OnDateSelectListener;
import com.jerry.nurse.listener.OnPhotoSelectListener;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.PractisingResult;
import com.jerry.nurse.model.UploadResult;
import com.jerry.nurse.model.UserInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LitePalUtil;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.jerry.nurse.view.ToggleButton;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.AUDIT_EMPTY;
import static com.jerry.nurse.constant.ServiceConstant.AUDIT_FAILED;
import static com.jerry.nurse.constant.ServiceConstant.AUDIT_ING;
import static com.jerry.nurse.constant.ServiceConstant.AUDIT_SUCCESS;
import static com.jerry.nurse.constant.ServiceConstant.PRACTISING_ADDRESS;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;


public class PractisingCertificateActivity extends BaseActivity {

    private static final int DEFAULT_LENGTH = 20;

    @Bind(R.id.tb_certificate)
    TitleBar mTitleBar;

    @Bind(R.id.ll_auditing)
    LinearLayout mAuditingLayout;

    @Bind(R.id.rl_birthday)
    RelativeLayout mBirthdayLayout;

    @Bind(R.id.tv_sex)
    TextView mSexTextView;

    @Bind(R.id.rl_first_sign_date)
    RelativeLayout mFirstSignDateLayout;

    @Bind(R.id.rl_last_sign_date)
    RelativeLayout mLastSignDateLayout;

    @Bind(R.id.rl_last_sign_date_valid)
    RelativeLayout mLastSignDateValidLayout;

    @Bind(R.id.rl_certificate_date)
    RelativeLayout mCertificateDateLayout;

    @Bind(R.id.rl_first_work_date)
    RelativeLayout mFirstWordDateLayout;

    @Bind(R.id.et_name)
    EditText mNameEditText;

    @Bind(R.id.tb_sex)
    ToggleButton mSexButton;

    @Bind(R.id.tv_birthday)
    TextView mBirthdayTextView;

    @Bind(R.id.et_country_nation)
    EditText mCountryNationEditText;

    @Bind(R.id.et_practising_location)
    EditText mPractisingLocationEditText;

    @Bind(R.id.et_certificate_number)
    EditText mCertificateNumberEditText;

    @Bind(R.id.et_id_card_number)
    EditText mIdCardNumberEditText;

    @Bind(R.id.tv_first_sign_date)
    TextView mFirstSignDateTextView;

    @Bind(R.id.tv_last_sign_date)
    TextView mLastSignDateTextView;

    @Bind(R.id.tv_last_sign_date_valid)
    TextView mLastSignDateValidTextView;

    @Bind(R.id.et_sign_department)
    EditText mSignDepartmentTextView;

    @Bind(R.id.et_certificate_organization)
    EditText mCertificateOrganizationTextView;

    @Bind(R.id.tv_certificate_date)
    TextView mCertificateDateTextView;

    @Bind(R.id.tv_first_work_date)
    TextView mFirstWorkDateTextView;

    @Bind(R.id.ll_pic1)
    LinearLayout mPictureLayout1;

    @Bind(R.id.ll_pic2)
    LinearLayout mPictureLayout2;

    @Bind(R.id.ll_pic3)
    LinearLayout mPictureLayout3;

    @Bind(R.id.iv_picture1)
    ImageView mPicture1ImageView;

    @Bind(R.id.iv_picture2)
    ImageView mPicture2ImageView;

    @Bind(R.id.iv_picture3)
    ImageView mPicture3ImageView;

    @Bind(R.id.tv_tip)
    TextView mTipTextView;

    @Bind(R.id.acb_submit)
    AppCompatButton mSubmitButton;

    @Bind(R.id.iv_photo1)
    ImageView mPhoto1ImageView;

    @Bind(R.id.iv_photo2)
    ImageView mPhoto2ImageView;

    @Bind(R.id.iv_photo3)
    ImageView mPhoto3ImageView;

    @Bind(R.id.iv_audit_status)
    ImageView mStatusImageView;

    @Bind(R.id.tv_auditing)
    TextView mAuditingTextView;

    @Bind(R.id.tv_failed)
    TextView mFailedTextView;

    @Bind(R.id.tv_audit_again)
    TextView mAuditAgainTextView;

    @Bind(R.id.ll_failed_message)
    LinearLayout mFailedMessageLayout;

    @Bind(R.id.iv_audit_success)
    ImageView mSuccessImageView;

    @Bind(R.id.tv_failed_message)
    TextView mFailedMessageTextView;

    private Bitmap mBitmap1;
    private Bitmap mBitmap2;
    private Bitmap mBitmap3;


    private String mErrorMessage;
    private ProgressDialogManager mProgressDialogManager;
    private PractisingResult.Practising mPractising;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PractisingCertificateActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_practising_certificate;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mProgressDialogManager = new ProgressDialogManager(this);

        String registerId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");
        getPractisingCertificateInfo(registerId);

    }

    private void setPractisingData() {
        if (mPractising != null) {
            mNameEditText.setText(mPractising.getName());
            mSexTextView.setText(mPractising.getSex());
            mBirthdayTextView.setText(DateUtil.parseMysqlDateToString(mPractising.getBirthDate()));
            mCountryNationEditText.setText(mPractising.getCountry());
            mPractisingLocationEditText.setText(mPractising.getPracticeAddress());
            mCertificateNumberEditText.setText(mPractising.getCertificateId());
            mIdCardNumberEditText.setText(mPractising.getIDCard());
            mFirstSignDateTextView.setText(DateUtil.parseMysqlDateToString(mPractising.getFirstRegisterDate()));
            mLastSignDateTextView.setText(DateUtil.parseMysqlDateToString(mPractising.getLastRegisterDate()));
            mLastSignDateValidTextView.setText(DateUtil.parseMysqlDateToString(mPractising.getRegisterToDate()));
            mSignDepartmentTextView.setText(mPractising.getRegisterAuthority());
            mCertificateOrganizationTextView.setText(mPractising.getCertificateAuthority());
            mCertificateDateTextView.setText(DateUtil.parseMysqlDateToString(mPractising.getCertificateDate()));
            mFirstWorkDateTextView.setText(DateUtil.parseMysqlDateToString(mPractising.getFirstJobTime()));

            if (mPractising.getVerifyStatus() != AUDIT_EMPTY) {
                makeUneditable();

                // 审核中
                if (mPractising.getVerifyStatus() == AUDIT_ING) {
                    setStatus(AUDIT_ING);
                }
                // 审核失败
                else if (mPractising.getVerifyStatus() == AUDIT_FAILED) {
                    setStatus(AUDIT_FAILED);
                }
                // 审核通过
                else if (mPractising.getVerifyStatus() == AUDIT_SUCCESS) {
                    setStatus(AUDIT_SUCCESS);
                }

                // 加载图片显示
                Glide.with(this).load(PRACTISING_ADDRESS + mPractising.getPicture1())
                        .error(R.drawable.default_rz).into(mPicture1ImageView);
                Glide.with(this).load(PRACTISING_ADDRESS + mPractising.getPicture2())
                        .error(R.drawable.default_rz).into(mPicture2ImageView);
                Glide.with(this).load(PRACTISING_ADDRESS + mPractising.getPicture3())
                        .error(R.drawable.default_rz).into(mPicture3ImageView);
            } else {
                makeEditable();
            }
        }
    }


    /**
     * 获取用户执业证信息
     */
    private void getPractisingCertificateInfo(final String registerId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_PRACTISING_CERTIFICATE_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {


                    @Override
                    public void onFilterResponse(String response, int id) {
                        PractisingResult practisingResult = new Gson().fromJson(response, PractisingResult.class);
                        if (practisingResult.getCode() == RESPONSE_SUCCESS) {
                            mPractising = practisingResult.getBody();
                            setPractisingData();
                        } else {
                            T.showShort(PractisingCertificateActivity.this, "获取执业证失败");
                        }
                    }
                });
    }

    /**
     * 根据不同审核状态改变显示状态
     *
     * @param status
     */
    private void setStatus(int status) {
        if (status == AUDIT_ING) {

            mAuditingLayout.setVisibility(View.VISIBLE);
            mStatusImageView.setVisibility(View.VISIBLE);
            mAuditingTextView.setVisibility(View.VISIBLE);
            mFailedTextView.setVisibility(View.GONE);
            mAuditAgainTextView.setVisibility(View.GONE);
            mStatusImageView.setImageResource(R.drawable.zyzgz_rzz);
        } else if (status == AUDIT_FAILED) {

            mAuditingLayout.setVisibility(View.VISIBLE);
            mStatusImageView.setVisibility(View.VISIBLE);
            mAuditingTextView.setVisibility(View.GONE);
            mStatusImageView.setImageResource(R.drawable.zyzgz_wtg);
            mFailedTextView.setVisibility(View.VISIBLE);
            mAuditAgainTextView.setVisibility(View.VISIBLE);
            mFailedMessageLayout.setVisibility(View.VISIBLE);
            mFailedMessageTextView.setVisibility(View.VISIBLE);
            mFailedMessageTextView.setText(mPractising.getVerifyView());
            mTitleBar.setRightText("认证").setOnRightClickListener(new TitleBar.OnRightClickListener() {
                @Override
                public void onRightClick(View view) {
                    makeEditable();
                }
            });
        } else if (status == AUDIT_SUCCESS) {
            mSuccessImageView.setVisibility(View.VISIBLE);
            mAuditingLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 进入预览状态
     */
    private void makeUneditable() {
        mNameEditText.setEnabled(false);
        mBirthdayTextView.setEnabled(false);
        mCountryNationEditText.setEnabled(false);
        mPractisingLocationEditText.setEnabled(false);
        mCertificateNumberEditText.setEnabled(false);
        mIdCardNumberEditText.setEnabled(false);
        mFirstSignDateTextView.setEnabled(false);
        mLastSignDateTextView.setEnabled(false);
        mLastSignDateValidTextView.setEnabled(false);
        mSignDepartmentTextView.setEnabled(false);
        mCertificateOrganizationTextView.setEnabled(false);
        mCertificateDateTextView.setEnabled(false);
        mFirstWorkDateTextView.setEnabled(false);

        mPhoto1ImageView.setVisibility(View.INVISIBLE);
        mPhoto2ImageView.setVisibility(View.INVISIBLE);
        mPhoto3ImageView.setVisibility(View.INVISIBLE);
        mSexButton.setVisibility(View.INVISIBLE);
        mTipTextView.setVisibility(View.GONE);
        mSubmitButton.setVisibility(View.GONE);
        mSexTextView.setVisibility(View.VISIBLE);
    }


    /**
     * 进入编辑状态
     */
    private void makeEditable() {
        mNameEditText.setEnabled(true);
        mBirthdayTextView.setEnabled(true);
        mCountryNationEditText.setEnabled(true);
        mPractisingLocationEditText.setEnabled(true);
        mCertificateNumberEditText.setEnabled(true);
        mIdCardNumberEditText.setEnabled(true);
        mFirstSignDateTextView.setEnabled(true);
        mLastSignDateTextView.setEnabled(true);
        mLastSignDateValidTextView.setEnabled(true);
        mSignDepartmentTextView.setEnabled(true);
        mCertificateOrganizationTextView.setEnabled(true);
        mCertificateDateTextView.setEnabled(true);
        mFirstWorkDateTextView.setEnabled(true);

        mAuditingLayout.setVisibility(View.GONE);
        mFailedMessageLayout.setVisibility(View.GONE);
        mPhoto1ImageView.setVisibility(View.VISIBLE);
        mPhoto2ImageView.setVisibility(View.VISIBLE);
        mPhoto3ImageView.setVisibility(View.VISIBLE);
        mSexButton.setVisibility(View.VISIBLE);
        mTipTextView.setVisibility(View.VISIBLE);
        mSubmitButton.setVisibility(View.VISIBLE);
        mSexTextView.setVisibility(View.INVISIBLE);

        mTitleBar.setRightVisible(View.INVISIBLE);

        setDateSelectListener(mBirthdayLayout, mBirthdayTextView, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mBirthdayTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mFirstSignDateLayout, mFirstSignDateTextView, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mFirstSignDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mLastSignDateLayout, mLastSignDateTextView, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mLastSignDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mLastSignDateValidLayout, mLastSignDateValidTextView, true, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mLastSignDateValidTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mCertificateDateLayout, mCertificateDateTextView, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mCertificateDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mFirstWordDateLayout, mFirstWorkDateTextView, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mFirstWorkDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });

        setPhotoSelectListener(mPictureLayout1, 0, new OnPhotoSelectListener() {
            @Override
            public void onPhotoSelected(Bitmap bitmap, File file) {
                mBitmap1 = bitmap;
                postPicture(file, 0);
            }
        });
        setPhotoSelectListener(mPictureLayout2, 1, new OnPhotoSelectListener() {
            @Override
            public void onPhotoSelected(Bitmap bitmap, File file) {
                mBitmap2 = bitmap;
                postPicture(file, 1);
            }
        });
        setPhotoSelectListener(mPictureLayout3, 2, new OnPhotoSelectListener() {
            @Override
            public void onPhotoSelected(Bitmap bitmap, File file) {
                mBitmap3 = bitmap;
                postPicture(file, 2);
            }
        });
    }

    @OnClick(R.id.acb_submit)
    void onSubmit(View view) {
        if (!validate()) {
            if (!TextUtils.isEmpty(mErrorMessage)) {
                T.showShort(this, mErrorMessage);
            }
            return;
        }

        // 组装数据
        mPractising.setName(mNameEditText.getText().toString());
        mPractising.setBirthDate(DateUtil.parseStringToMysqlDate(mBirthdayTextView.getText().toString()));
        if (mSexButton.getOpen() == 1) {
            mPractising.setSex("男");
        } else {
            mPractising.setSex("女");
        }
        mPractising.setCountry(mCountryNationEditText.getText().toString());
        mPractising.setPracticeAddress(mPractisingLocationEditText.getText().toString());
        mPractising.setCertificateId(mCertificateNumberEditText.getText().toString());
        mPractising.setIDCard(mIdCardNumberEditText.getText().toString());
        mPractising.setFirstRegisterDate(DateUtil.parseStringToMysqlDate(mFirstSignDateTextView.getText().toString()));
        mPractising.setLastRegisterDate(DateUtil.parseStringToMysqlDate(mLastSignDateTextView.getText().toString()));
        mPractising.setRegisterToDate(DateUtil.parseStringToMysqlDate(mLastSignDateValidTextView.getText().toString()));
        mPractising.setRegisterAuthority(mSignDepartmentTextView.getText().toString());
        mPractising.setCertificateAuthority(mCertificateOrganizationTextView.getText().toString());
        mPractising.setCertificateDate(DateUtil.parseStringToMysqlDate(mCertificateDateTextView.getText().toString()));
        mPractising.setFirstJobTime(DateUtil.parseStringToMysqlDate(mFirstWorkDateTextView.getText().toString()));
        mPractising.setVerifyStatus(1);

        postProfessionalCertificate();
    }

    private boolean validate() {
        if (TextUtils.isEmpty(mNameEditText.getText().toString())) {
            mErrorMessage = "姓名为空！";
            return false;
        }
        if (mNameEditText.getText().toString().length() > DEFAULT_LENGTH) {
            mErrorMessage = "姓名过长！";
            return false;
        }
        if (TextUtils.isEmpty(mBirthdayTextView.getText().toString())) {
            mErrorMessage = "生日为空！";
            return false;
        }
        if (TextUtils.isEmpty(mCountryNationEditText.getText().toString())) {
            mErrorMessage = "国籍为空！";
            return false;
        }
        if (mCountryNationEditText.getText().toString().length() > DEFAULT_LENGTH) {
            mErrorMessage = "国籍名称过长！";
            return false;
        }
        if (TextUtils.isEmpty(mPractisingLocationEditText.getText().toString())) {
            mErrorMessage = "执业地点为空！";
            return false;
        }
        if (mPractisingLocationEditText.getText().toString().length() > DEFAULT_LENGTH) {
            mErrorMessage = "执业地点名称过长！";
            return false;
        }
        if (TextUtils.isEmpty(mCertificateNumberEditText.getText().toString())) {
            mErrorMessage = "执业编号为空！";
            return false;
        }
        if (mCertificateNumberEditText.getText().toString().length() > DEFAULT_LENGTH) {
            mErrorMessage = "执业编号名称过长！";
            return false;
        }
        if (TextUtils.isEmpty(mIdCardNumberEditText.getText().toString())) {
            mErrorMessage = "身份证为空！";
            return false;
        }
//        if (!AccountValidatorUtil.isIDCard(mIdCardNumberEditText.getText().toString())) {
//            mErrorMessage = "身份证格式不正确！";
//            return false;
//        }
        if (mIdCardNumberEditText.getText().toString().length() > 18) {
            mErrorMessage = "身份证过长！";
            return false;
        }
        if (TextUtils.isEmpty(mFirstSignDateTextView.getText().toString())) {
            mErrorMessage = "首次注册日期为空！";
            return false;
        }
        if (TextUtils.isEmpty(mLastSignDateTextView.getText().toString())) {
            mErrorMessage = "最近注册日期为空！";
            return false;
        }
        if (TextUtils.isEmpty(mLastSignDateValidTextView.getText().toString())) {
            mErrorMessage = "最近一次注册有效期为空！";
            return false;
        }
        if (TextUtils.isEmpty(mSignDepartmentTextView.getText().toString())) {
            mErrorMessage = "注册机关为空！";
            return false;
        }
        if (mSignDepartmentTextView.getText().toString().length() > DEFAULT_LENGTH) {
            mErrorMessage = "注册机关名称过长！";
            return false;
        }
        if (TextUtils.isEmpty(mCertificateOrganizationTextView.getText().toString())) {
            mErrorMessage = "发证机关为空！";
            return false;
        }
        if (mCertificateOrganizationTextView.getText().toString().length() > DEFAULT_LENGTH) {
            mErrorMessage = "发证机关名称过长！";
            return false;
        }

        if (TextUtils.isEmpty(mCertificateDateTextView.getText().toString())) {
            mErrorMessage = "发证日期为空！";
            return false;
        }

        if (TextUtils.isEmpty(mFirstWorkDateTextView.getText().toString())) {
            mErrorMessage = "首次工作日期为空！";
            return false;
        }
        if (TextUtils.isEmpty(mPractising.getPicture1())) {
            mErrorMessage = "证件页1为空！";
            return false;
        }

        if (TextUtils.isEmpty(mPractising.getPicture2())) {
            mErrorMessage = "证件页2为空！";
            return false;
        }

        if (TextUtils.isEmpty(mPractising.getPicture3())) {
            mErrorMessage = "证件页3为空！";
            return false;
        }

        return true;
    }

    /**
     * 上传照片
     *
     * @param file
     * @param index
     */
    private void postPicture(File file, final int index) {
        mProgressDialogManager.show();
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        OkHttpUtils.post()
                .addFile("professional", file.getName(), file)
                .url(ServiceConstant.UPLOAD_PRACTISING_PICTURE)
                .headers(headers)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        UploadResult uploadResult = new Gson().fromJson(response, UploadResult.class);
                        if (uploadResult.getCode() == RESPONSE_SUCCESS) {
                            String fileName = uploadResult.getBody().getFilename();
                            if (index == 0) {
                                mPractising.setPicture1(fileName);
                                mPicture1ImageView.setImageBitmap(mBitmap1);
                            } else if (index == 1) {
                                mPractising.setPicture2(fileName);
                                mPicture2ImageView.setImageBitmap(mBitmap2);
                            } else if (index == 2) {
                                mPractising.setPicture3(fileName);
                                mPicture3ImageView.setImageBitmap(mBitmap3);
                            }
                        } else {
                            L.i("上传执业证照片失败");
                            T.showShort(PractisingCertificateActivity.this, R.string.upload_failed);
                        }
                    }
                });
    }

    private void postProfessionalCertificate() {
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_PRACTISING_CERTIFICATE_INFO)
                .content(StringUtil.addModelWithJson(mPractising))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            L.i("设置执业证成功");
                            // 设置成功后更新数据库
                            LoginInfo loginInfo = DataSupport.findFirst(LoginInfo.class);
                            loginInfo.setPStatus(AUDIT_ING);
                            LitePalUtil.updateLoginInfo(PractisingCertificateActivity.this, loginInfo);

                            UserInfo userInfo = DataSupport.findFirst(UserInfo.class);
                            userInfo.setPVerifyStatus(AUDIT_ING);
                            LitePalUtil.updateUserInfo(PractisingCertificateActivity.this, userInfo);

                            T.showShort(PractisingCertificateActivity.this,
                                    R.string.submit_success_please_wait);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            T.showShort(PractisingCertificateActivity.this, commonResult.getMsg());
                        }
                    }
                });
    }

    @OnClick(R.id.tv_audit_again)
    void onAuditAgain(View view) {
        makeEditable();
    }

}
