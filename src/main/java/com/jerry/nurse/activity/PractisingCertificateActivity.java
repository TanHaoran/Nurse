package com.jerry.nurse.activity;

import android.app.ProgressDialog;
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
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.OnDateSelectListener;
import com.jerry.nurse.listener.OnPhotoSelectListener;
import com.jerry.nurse.model.UserPractisingCertificateInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.UserUtil;
import com.jerry.nurse.view.ToggleButton;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.AUDIT_EMPTY;
import static com.jerry.nurse.constant.ServiceConstant.AUDIT_FAILED;
import static com.jerry.nurse.constant.ServiceConstant.AUDIT_ING;
import static com.jerry.nurse.constant.ServiceConstant.AUDIT_SUCCESS;
import static com.jerry.nurse.constant.ServiceConstant.PRACTISING_ADDRESS;
import static com.jerry.nurse.constant.ServiceConstant.PROFESSIONAL_ADDRESS;
import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;
import static com.jerry.nurse.constant.ServiceConstant.USER_COLON;
import static com.jerry.nurse.constant.ServiceConstant.USER_FILE;


public class PractisingCertificateActivity extends BaseActivity {

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

    private Bitmap mBitmap1;
    private Bitmap mBitmap2;
    private Bitmap mBitmap3;


    private UserPractisingCertificateInfo mInfo;

    private String mErrorMessage;
    private ProgressDialog mProgressDialog;

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


        // 初始化等待框
        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("请稍后...");


        mInfo = DataSupport.findFirst(UserPractisingCertificateInfo.class);

        if (mInfo != null) {
            mNameEditText.setText(mInfo.getName());
            mBirthdayTextView.setText(DateUtil.parseMysqlDateToString(mInfo.getBirthDate()));
            mCountryNationEditText.setText(mInfo.getCountry());
            mPractisingLocationEditText.setText(mInfo.getPracticeAddress());
            mCertificateNumberEditText.setText(mInfo.getCertificateId());
            mIdCardNumberEditText.setText(mInfo.getIDCard());
            mFirstSignDateTextView.setText(DateUtil.parseMysqlDateToString(mInfo.getFirstRegisterDate()));
            mLastSignDateTextView.setText(DateUtil.parseMysqlDateToString(mInfo.getLastRegisterDate()));
            mLastSignDateValidTextView.setText(DateUtil.parseMysqlDateToString(mInfo.getRegisterToDate()));
            mSignDepartmentTextView.setText(mInfo.getRegisterAuthority());
            mCertificateOrganizationTextView.setText(mInfo.getCertificateAuthority());
            mCertificateDateTextView.setText(DateUtil.parseMysqlDateToString(mInfo.getCertificateDate()));
            mFirstWorkDateTextView.setText(DateUtil.parseMysqlDateToString(mInfo.getFirstJobTime()));

            if (mInfo.getVerifyStatus() != AUDIT_EMPTY) {
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
                mSexTextView.setText(mInfo.getSex());

                if (mInfo.getVerifyStatus() == AUDIT_ING) {
                    mAuditingLayout.setVisibility(View.VISIBLE);
                    mStatusImageView.setVisibility(View.VISIBLE);
                    mAuditingTextView.setVisibility(View.VISIBLE);
                    mFailedTextView.setVisibility(View.INVISIBLE);
                    mAuditAgainTextView.setVisibility(View.INVISIBLE);
                    mStatusImageView.setImageResource(R.drawable.zyzgz_rzz);
                }  else if (mInfo.getVerifyStatus() == AUDIT_FAILED) {
                    mAuditingLayout.setVisibility(View.VISIBLE);
                    mStatusImageView.setVisibility(View.VISIBLE);
                    mAuditingTextView.setVisibility(View.INVISIBLE);
                    mStatusImageView.setImageResource(R.drawable.zyzgz_wtg);
                    mFailedTextView.setVisibility(View.VISIBLE);
                    mAuditAgainTextView.setVisibility(View.VISIBLE);
                    mFailedMessageLayout.setVisibility(View.VISIBLE);
                }else if (mInfo.getVerifyStatus() == AUDIT_SUCCESS) {
                    mSuccessImageView.setVisibility(View.VISIBLE);
                    mAuditingLayout.setVisibility(View.GONE);
                }

                // 加载图片显示
                Glide.with(this).load(PRACTISING_ADDRESS + mInfo.getPicture1()).into(mPicture1ImageView);
                Glide.with(this).load(PRACTISING_ADDRESS + mInfo.getPicture2()).into(mPicture2ImageView);
                Glide.with(this).load(PRACTISING_ADDRESS + mInfo.getPicture2()).into(mPicture3ImageView);
            } else {
                mSexTextView.setVisibility(View.INVISIBLE);
                mSubmitButton.setVisibility(View.VISIBLE);
                setDateSelectListener(mBirthdayLayout, null, new OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Date date) {
                        mBirthdayTextView.setText(DateUtil.parseDateToString(date));
                    }
                });
                setDateSelectListener(mFirstSignDateLayout, null, new OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Date date) {
                        mFirstSignDateTextView.setText(DateUtil.parseDateToString(date));
                    }
                });
                setDateSelectListener(mLastSignDateLayout, null, new OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Date date) {
                        mLastSignDateTextView.setText(DateUtil.parseDateToString(date));
                    }
                });
                setDateSelectListener(mLastSignDateValidLayout, null, new OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Date date) {
                        mLastSignDateValidTextView.setText(DateUtil.parseDateToString(date));
                    }
                });
                setDateSelectListener(mCertificateDateLayout, null, new OnDateSelectListener() {
                    @Override
                    public void onDateSelected(Date date) {
                        mCertificateDateTextView.setText(DateUtil.parseDateToString(date));
                    }
                });
                setDateSelectListener(mFirstWordDateLayout, null, new OnDateSelectListener() {
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
        }
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
        mInfo.setName(mNameEditText.getText().toString());
        mInfo.setBirthDate(DateUtil.parseStringToMysqlDate(mBirthdayTextView.getText().toString()));
        if (mSexButton.getSex() == 0) {
            mInfo.setSex("男");
        } else {
            mInfo.setSex("女");
        }
        mInfo.setCountry(mCountryNationEditText.getText().toString());
        mInfo.setPracticeAddress(mPractisingLocationEditText.getText().toString());
        mInfo.setCertificateId(mCertificateNumberEditText.getText().toString());
        mInfo.setIDCard(mIdCardNumberEditText.getText().toString());
        mInfo.setFirstRegisterDate(DateUtil.parseStringToMysqlDate(mFirstSignDateTextView.getText().toString()));
        mInfo.setLastRegisterDate(DateUtil.parseStringToMysqlDate(mLastSignDateTextView.getText().toString()));
        mInfo.setRegisterToDate(DateUtil.parseStringToMysqlDate(mLastSignDateValidTextView.getText().toString()));
        mInfo.setRegisterAuthority(mSignDepartmentTextView.getText().toString());
        mInfo.setCertificateAuthority(mCertificateOrganizationTextView.getText().toString());
        mInfo.setCertificateDate(DateUtil.parseStringToMysqlDate(mCertificateDateTextView.getText().toString()));
        mInfo.setFirstJobTime(DateUtil.parseStringToMysqlDate(mFirstWorkDateTextView.getText().toString()));
        mInfo.setVerifyStatus(1);

        postProfessionalCertificate();
    }

    private boolean validate() {
        if (TextUtils.isEmpty(mNameEditText.getText().toString())) {
            mErrorMessage = "姓名为空！";
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
        if (TextUtils.isEmpty(mPractisingLocationEditText.getText().toString())) {
            mErrorMessage = "执业地点为空！";
            return false;
        }
        if (TextUtils.isEmpty(mCertificateNumberEditText.getText().toString())) {
            mErrorMessage = "执业编号为空！";
            return false;
        }
        if (TextUtils.isEmpty(mIdCardNumberEditText.getText().toString())) {
            mErrorMessage = "身份证为空！";
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
        if (TextUtils.isEmpty(mCertificateOrganizationTextView.getText().toString())) {
            mErrorMessage = "发证机关为空！";
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
        if (TextUtils.isEmpty(mInfo.getPicture1())) {
            mErrorMessage = "图片1为空！";
            return false;
        }

        if (TextUtils.isEmpty(mInfo.getPicture2())) {
            mErrorMessage = "图片2为空！";
            return false;
        }

        if (TextUtils.isEmpty(mInfo.getPicture3())) {
            mErrorMessage = "图片3为空！";
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
        mProgressDialog.show();
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        OkHttpUtils.post()
                .addFile("professional", file.getName(), file)
                .url(ServiceConstant.UPLOAD_PRACTISING_PICTURE)
                .headers(headers)
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    protected void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        if (response.startsWith(USER_FILE)) {
                            if (response.split(USER_COLON).length == 2) {
                                String name = response.split(USER_COLON)[1];
                                if (index == 0) {
                                    mInfo.setPicture1(name);
                                    mPicture1ImageView.setImageBitmap(mBitmap1);
                                } else if (index == 1) {
                                    mInfo.setPicture2(name);
                                    mPicture2ImageView.setImageBitmap(mBitmap2);
                                } else if (index == 2) {
                                    mInfo.setPicture3(name);
                                    mPicture3ImageView.setImageBitmap(mBitmap3);
                                }
                            }
                        } else {
                            L.i("上传专业资格证失败");
                            T.showShort(PractisingCertificateActivity.this, R.string.upload_failed);
                        }
                    }
                });
    }

    private void postProfessionalCertificate() {
        mProgressDialog.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_PRACTISING_CERTIFICATE_INFO)
                .content(StringUtil.addModelWithJson(mInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        if (response.equals(REQUEST_SUCCESS)) {
                            L.i("设置执业证成功");
                            T.showShort(PractisingCertificateActivity.this,
                                    R.string.submit_success_please_wait);
                            // 设置成功后更新数据库
                            UserUtil.savePractisingCertificateInfo(mInfo);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            L.i("设置执业证失败");
                        }
                    }
                });
    }
}
