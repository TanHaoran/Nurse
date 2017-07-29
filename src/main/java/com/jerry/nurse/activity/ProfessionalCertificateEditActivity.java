package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.OnDateSelectListener;
import com.jerry.nurse.model.UserProfessionalCertificateInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.util.UserUtil;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.Date;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;


public class ProfessionalCertificateEditActivity extends BaseActivity {

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

    private UserProfessionalCertificateInfo mInfo;

    private String mErrorMessage;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ProfessionalCertificateEditActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_professional_certificate_edit;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        setDateSelectListener(mBirthdayLayout, null, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mBirthdayTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mApproveDateLayout, null, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mApproveDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });
        setDateSelectListener(mSignDateLayout, null, new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mSignDateTextView.setText(DateUtil.parseDateToString(date));
            }
        });

        mInfo = DataSupport.findFirst(UserProfessionalCertificateInfo.class);

        if (mInfo != null) {
            mIdNumberEditText.setText(mInfo.getCertificateId());
            mManagementNumberEditText.setText(mInfo.getManageId());
            mNameEditText.setText(mInfo.getName());
            mBirthdayTextView.setText(mInfo.getDateBirth());
            mProfessionNameEditText.setText(mInfo.getMajorName());
            mCertificateLevelEditText.setText(mInfo.getCategory());
            mTypeEditText.setText(mInfo.getQuaLevel());
            mApproveDateTextView.setText(mInfo.getApproveDate());
            mSignOrganizationEditText.setText(mInfo.getIssuingAgency());
            mSignDateTextView.setText(mInfo.getIssuingDate());
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
        mInfo.setSex("女");
        mInfo.setDateBirth(DateUtil.parseStringToMysqlDate(mBirthdayTextView.getText().toString()));
        mInfo.setMajorName(mProfessionNameEditText.getText().toString());
        mInfo.setQuaLevel(mCertificateLevelEditText.getText().toString());
        mInfo.setCategory(mTypeEditText.getText().toString());
        mInfo.setApproveDate(DateUtil.parseStringToMysqlDate(mApproveDateTextView.getText().toString()));
        mInfo.setIssuingAgency(mSignOrganizationEditText.getText().toString());
        mInfo.setIssuingDate(DateUtil.parseStringToMysqlDate(mSignDateTextView.getText().toString()));

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
//        if (TextUtils.isEmpty(mSex.getText().toString())) {
//            mErrorMessage = "编号为空！";
//        return false;
//        }
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

        return true;
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
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        if (response.equals(REQUEST_SUCCESS)) {
                            L.i("设置专业资格证成功");
                            // 设置成功后更新数据库
                            UserUtil.saveProfessionalCertificateInfo(mInfo);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            L.i("设置专业资格证失败");
                        }
                    }
                });
    }

}
