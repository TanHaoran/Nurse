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
import com.jerry.nurse.model.UserProfessionalCertificateInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.LitePalUtil;
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
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.AUDIT_EMPTY;
import static com.jerry.nurse.constant.ServiceConstant.AUDIT_FAILED;
import static com.jerry.nurse.constant.ServiceConstant.AUDIT_ING;
import static com.jerry.nurse.constant.ServiceConstant.AUDIT_SUCCESS;
import static com.jerry.nurse.constant.ServiceConstant.PROFESSIONAL_ADDRESS;
import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;
import static com.jerry.nurse.constant.ServiceConstant.USER_COLON;
import static com.jerry.nurse.constant.ServiceConstant.USER_FILE;


public class ProfessionalCertificateActivity extends BaseActivity {

    @Bind(R.id.tb_certificate)
    TitleBar mTitleBar;

    @Bind(R.id.ll_auditing)
    LinearLayout mAuditingLayout;

    @Bind(R.id.rl_birthday)
    RelativeLayout mBirthdayLayout;

    @Bind(R.id.rl_approve_date)
    RelativeLayout mApproveDateLayout;

    @Bind(R.id.et_id_number)
    EditText mIdNumberEditText;

    @Bind(R.id.et_management_number)
    EditText mManagementNumberEditText;

    @Bind(R.id.et_name)
    EditText mNameEditText;

    @Bind(R.id.tb_sex)
    ToggleButton mSexButton;

    @Bind(R.id.tv_sex)
    TextView mSexTextView;

    @Bind(R.id.tv_birthday)
    TextView mBirthdayTextView;

    @Bind(R.id.et_profession_name)
    EditText mProfessionNameEditText;

    @Bind(R.id.et_certificate_level)
    EditText mCertificateLevelEditText;

    @Bind(R.id.et_type)
    EditText mTypeEditText;

    @Bind(R.id.tv_approve_date)
    TextView mApproveDateTextView;

    @Bind(R.id.et_sign_organization)
    EditText mSignOrganizationEditText;

    @Bind(R.id.tv_sign_date)
    TextView mSignDateTextView;

    @Bind(R.id.rl_sign_date)
    RelativeLayout mSignDateLayout;

    @Bind(R.id.ll_pic1)
    LinearLayout mPicture1Layout;

    @Bind(R.id.ll_pic2)
    LinearLayout mPicture2Layout;

    @Bind(R.id.iv_picture1)
    ImageView mPicture1ImageView;

    @Bind(R.id.iv_picture2)
    ImageView mPicture2ImageView;

    @Bind(R.id.iv_photo1)
    ImageView mPhoto1ImageView;

    @Bind(R.id.iv_photo2)
    ImageView mPhoto2ImageView;

    @Bind(R.id.tv_tip)
    TextView mTipTextView;

    @Bind(R.id.acb_submit)
    AppCompatButton mSubmitButton;


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

    private UserProfessionalCertificateInfo mInfo;

    private String mErrorMessage;

    private Bitmap mBitmap1;
    private Bitmap mBitmap2;

    private ProgressDialog mProgressDialog;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ProfessionalCertificateActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_professional_certificate;
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

        mInfo = DataSupport.findFirst(UserProfessionalCertificateInfo.class);

        if (mInfo != null) {
            mIdNumberEditText.setText(mInfo.getCertificateId());
            mSexTextView.setText(mInfo.getSex());
            mManagementNumberEditText.setText(mInfo.getManageId());
            mNameEditText.setText(mInfo.getName());
            mBirthdayTextView.setText(DateUtil.parseMysqlDateToString(mInfo.getDateBirth()));
            mProfessionNameEditText.setText(mInfo.getMajorName());
            mCertificateLevelEditText.setText(mInfo.getCategory());
            mTypeEditText.setText(mInfo.getQuaLevel());
            mApproveDateTextView.setText(DateUtil.parseMysqlDateToString(mInfo.getApproveDate()));
            mSignOrganizationEditText.setText(mInfo.getIssuingAgency());
            mSignDateTextView.setText(DateUtil.parseMysqlDateToString(mInfo.getIssuingDate()));

            if (mInfo.getVerifyStatus() != AUDIT_EMPTY) {
                makeUneditable();

                // 审核中
                if (mInfo.getVerifyStatus() == AUDIT_ING) {
                    setStatus(AUDIT_ING);
                }
                // 审核失败
                else if (mInfo.getVerifyStatus() == AUDIT_FAILED) {
                    setStatus(AUDIT_FAILED);
                }
                // 审核通过
                else if (mInfo.getVerifyStatus() == AUDIT_SUCCESS) {
                    setStatus(AUDIT_SUCCESS);
                }
                Glide.with(this).load(PROFESSIONAL_ADDRESS + mInfo.getPicture1()).into(mPicture1ImageView);
                Glide.with(this).load(PROFESSIONAL_ADDRESS + mInfo.getPicture2()).into(mPicture2ImageView);
            } else {
                makeEditable();
            }
        }
    }

    /**
     * 进入预览状态
     */
    private void makeUneditable() {
        mIdNumberEditText.setEnabled(false);
        mManagementNumberEditText.setEnabled(false);
        mNameEditText.setEnabled(false);
        mBirthdayTextView.setEnabled(false);
        mProfessionNameEditText.setEnabled(false);
        mCertificateLevelEditText.setEnabled(false);
        mTypeEditText.setEnabled(false);
        mApproveDateTextView.setEnabled(false);
        mSignOrganizationEditText.setEnabled(false);
        mSignDateTextView.setEnabled(false);
        mPhoto1ImageView.setVisibility(View.INVISIBLE);
        mPhoto2ImageView.setVisibility(View.INVISIBLE);
        mSexButton.setVisibility(View.INVISIBLE);
        mTipTextView.setVisibility(View.GONE);
        mSubmitButton.setVisibility(View.GONE);
        mSexTextView.setVisibility(View.VISIBLE);
    }

    /**
     * 进入编辑状态
     */
    private void makeEditable() {
        mIdNumberEditText.setEnabled(true);
        mManagementNumberEditText.setEnabled(true);
        mNameEditText.setEnabled(true);
        mBirthdayTextView.setEnabled(true);
        mProfessionNameEditText.setEnabled(true);
        mCertificateLevelEditText.setEnabled(true);
        mTypeEditText.setEnabled(true);
        mApproveDateTextView.setEnabled(true);
        mSignOrganizationEditText.setEnabled(true);
        mSignDateTextView.setEnabled(true);
        mPhoto1ImageView.setVisibility(View.VISIBLE);
        mPhoto2ImageView.setVisibility(View.VISIBLE);
        mSexButton.setVisibility(View.VISIBLE);
        mTipTextView.setVisibility(View.VISIBLE);
        mTitleBar.setRightVisible(View.INVISIBLE);
        mAuditingLayout.setVisibility(View.GONE);
        mFailedMessageLayout.setVisibility(View.GONE);
        mSexTextView.setVisibility(View.INVISIBLE);
        mSubmitButton.setVisibility(View.VISIBLE);

        setDateSelectListener(mBirthdayLayout, null, false, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mBirthdayTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mApproveDateLayout, null, false, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mApproveDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mSignDateLayout, null, false, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mSignDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });

        setPhotoSelectListener(mPicture1Layout, 0, new OnPhotoSelectListener() {
            @Override
            public void onPhotoSelected(Bitmap bitmap, File file) {
                mBitmap1 = bitmap;
                postPicture(file, 0);
            }
        });
        setPhotoSelectListener(mPicture2Layout, 1, new OnPhotoSelectListener() {
            @Override
            public void onPhotoSelected(Bitmap bitmap, File file) {
                mBitmap2 = bitmap;
                postPicture(file, 1);
            }
        });
    }

    /**
     * 根据不同状态改变显示的不同状态
     */
    private void setStatus(int status) {
        // 审核中
        if (status == AUDIT_ING) {
            mAuditingLayout.setVisibility(View.VISIBLE);
            mStatusImageView.setVisibility(View.VISIBLE);
            mAuditingTextView.setVisibility(View.VISIBLE);
            mFailedTextView.setVisibility(View.GONE);
            mAuditAgainTextView.setVisibility(View.GONE);
            mStatusImageView.setImageResource(R.drawable.zyzgz_rzz);
        }
        // 审核失败
        else if (status == AUDIT_FAILED) {
            mAuditingLayout.setVisibility(View.VISIBLE);
            mStatusImageView.setVisibility(View.VISIBLE);
            mAuditingTextView.setVisibility(View.GONE);
            mStatusImageView.setImageResource(R.drawable.zyzgz_wtg);
            mFailedTextView.setVisibility(View.VISIBLE);
            mAuditAgainTextView.setVisibility(View.VISIBLE);
            mFailedMessageLayout.setVisibility(View.VISIBLE);
            mFailedMessageTextView.setVisibility(View.VISIBLE);
            mFailedMessageTextView.setText(mInfo.getVerifyView());
            mTitleBar.setRightText("认证").setOnRightClickListener(new TitleBar.OnRightClickListener() {
                @Override
                public void onRightClick(View view) {
                    makeEditable();
                }
            });
        }
        // 审核通过
        else if (status == AUDIT_SUCCESS) {
            mSuccessImageView.setVisibility(View.VISIBLE);
            mAuditingLayout.setVisibility(View.GONE);
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
        mInfo.setCertificateId(mIdNumberEditText.getText().toString());
        mInfo.setManageId(mManagementNumberEditText.getText().toString());
        mInfo.setName(mNameEditText.getText().toString());
        mInfo.setDateBirth(DateUtil.parseStringToMysqlDate(mBirthdayTextView.getText().toString()));
        if (mSexButton.getSex() == 0) {
            mInfo.setSex("男");
        } else {
            mInfo.setSex("女");
        }
        mInfo.setDateBirth(DateUtil.parseStringToMysqlDate(mBirthdayTextView.getText().toString()));
        mInfo.setMajorName(mProfessionNameEditText.getText().toString());
        mInfo.setQuaLevel(mCertificateLevelEditText.getText().toString());
        mInfo.setCategory(mTypeEditText.getText().toString());
        mInfo.setApproveDate(DateUtil.parseStringToMysqlDate(mApproveDateTextView.getText().toString()));
        mInfo.setIssuingAgency(mSignOrganizationEditText.getText().toString());
        mInfo.setIssuingDate(DateUtil.parseStringToMysqlDate(mSignDateTextView.getText().toString()));
        mInfo.setVerifyStatus(1);

        postProfessionalCertificate();
    }

    private boolean validate() {
        if (TextUtils.isEmpty(mIdNumberEditText.getText().toString())) {
            mErrorMessage = "编号为空！";
            return false;
        }
        if (TextUtils.isEmpty(mManagementNumberEditText.getText().toString())) {
            mErrorMessage = "管理号为空！";
            return false;
        }
        if (TextUtils.isEmpty(mNameEditText.getText().toString())) {
            mErrorMessage = "姓名为空！";
            return false;
        }
        if (TextUtils.isEmpty(mBirthdayTextView.getText().toString())) {
            mErrorMessage = "生日为空！";
            return false;
        }
        if (TextUtils.isEmpty(mProfessionNameEditText.getText().toString())) {
            mErrorMessage = "专业名称为空！";
            return false;
        }
        if (TextUtils.isEmpty(mCertificateLevelEditText.getText().toString())) {
            mErrorMessage = "资格级别为空！";
            return false;
        }
        if (TextUtils.isEmpty(mTypeEditText.getText().toString())) {
            mErrorMessage = "类别为空！";
            return false;
        }
        if (TextUtils.isEmpty(mApproveDateTextView.getText().toString())) {
            mErrorMessage = "批准日期为空！";
            return false;
        }
        if (TextUtils.isEmpty(mSignOrganizationEditText.getText().toString())) {
            mErrorMessage = "签发单位为空！";
            return false;
        }
        if (TextUtils.isEmpty(mSignDateTextView.getText().toString())) {
            mErrorMessage = "签发日期为空！";
            return false;
        }
        if (TextUtils.isEmpty(mInfo.getPicture1())) {
            mErrorMessage = "证件页1为空！";
            return false;
        }

        if (TextUtils.isEmpty(mInfo.getPicture2())) {
            mErrorMessage = "证件页2为空！";
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
                .url(ServiceConstant.UPLOAD_PROFESSIONAL_PICTURE)
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
                                    L.i("图片1设置好图片" + name);
                                    mInfo.setPicture1(name);
                                    mPicture1ImageView.setImageBitmap(mBitmap1);
                                } else if (index == 1) {
                                    L.i("图片2设置好图片" + name);
                                    mInfo.setPicture2(name);
                                    mPicture2ImageView.setImageBitmap(mBitmap2);
                                }
                            }
                        } else {
                            L.i("上传专业资格证失败");
                            T.showShort(ProfessionalCertificateActivity.this, R.string.upload_failed);
                        }
                    }
                });
    }

    private void postProfessionalCertificate() {
        mProgressDialog.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_PROFESSIONAL_CERTIFICATE_INFO)
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
                            L.i("设置专业资格证成功");
                            // 设置成功后更新数据库
//                            LitePalUtil.saveProfessionalCertificateInfo(mInfo);
                            T.showShort(ProfessionalCertificateActivity.this,
                                    R.string.submit_success_please_wait);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            L.i("设置专业资格证失败");
                        }
                    }
                });
    }

    @OnClick(R.id.tv_audit_again)
    void onAuditAgain(View view) {
        makeEditable();
    }
}
