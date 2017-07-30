package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.OnDateSelectListener;
import com.jerry.nurse.listener.PhotoSelectListener;
import com.jerry.nurse.model.UserBasicInfo;
import com.jerry.nurse.model.UserHospitalInfo;
import com.jerry.nurse.model.UserPractisingCertificateInfo;
import com.jerry.nurse.model.UserProfessionalCertificateInfo;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.UserUtil;
import com.jerry.nurse.view.CircleImageView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.Date;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;

public class PersonalInfoActivity extends BaseActivity {

    public static final int REQUEST_REGISTER_INFO = 0x00000101;
    public static final int REQUEST_BASIC_INFO = 0x00000102;
    public static final int REQUEST_PROFESSIONAL_CERTIFICATE_INFO = 0x00000103;
    public static final int REQUEST_PRACTISING_CERTIFICATE_INFO = 0x00000104;
    public static final int REQUEST_HOSPITAL_INFO = 0x00000105;

    @Bind(R.id.civ_avatar)
    CircleImageView mAvatarView;

    @Bind(R.id.rl_avatar)
    RelativeLayout mAvatarLayout;

    @Bind(R.id.tv_name)
    TextView mNameTextView;

    @Bind(R.id.tv_nickname)
    TextView mNicknameTextView;

    @Bind(R.id.tv_sex)
    TextView mSexTextView;

    @Bind(R.id.tv_birthday)
    TextView mBirthdayTextView;

    @Bind(R.id.tv_professional_certificate)
    TextView mProfessionalCertificateTextView;

    @Bind(R.id.tv_practising_certificate)
    TextView mPractisingCertificateTextView;

    @Bind(R.id.tv_nursing_age)
    TextView mNursingAgeTextView;

    @Bind(R.id.tv_hospital)
    TextView mHospitalTextView;

    @Bind(R.id.tv_office)
    TextView mOfficeTextView;

    @Bind(R.id.tv_job_number)
    TextView mJobNumberTextView;

    @Bind(R.id.rl_birthday)
    RelativeLayout mBirthdayLayout;

    @BindString(R.string.change_avatar)
    String mStringChangeAvatar;

    @BindString(R.string.male)
    String mStringMale;

    @BindString(R.string.female)
    String mStringFemale;

    private UserRegisterInfo mRegisterInfo;
    private UserBasicInfo mBasicInfo;
    private UserProfessionalCertificateInfo mProfessionalCertificateInfo;
    private UserPractisingCertificateInfo mPractisingCertificateInfo;
    private UserHospitalInfo mHospitalInfo;

    private String mRegisterId;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, PersonalInfoActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_personal_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // 设置图片
        setPhotoSelectListener(mAvatarLayout, new PhotoSelectListener() {
            @Override
            public void onPhotoSelected(Bitmap bitmap) {
                mAvatarView.setImageBitmap(bitmap);
            }
        });
        mRegisterId = (String) SPUtil.get(this, SPUtil.REGISTER_ID, "");

        // 读取用户注册信息
        updateRegisterInfo();
        updateBasicInfo();
        updateProfessionalCertificateInfo();
        updatePractisingCertificateInfo();
        ;
        updateHospitalInfo();

        // 获取用户各类信息
        getBasicInfo(mRegisterId);
        getProfessionalCertificateInfo(mRegisterId);
        getPractisingCertificateInfo(mRegisterId);
        getHospitalInfo(mRegisterId);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateProfessionalCertificateInfo();
        updatePractisingCertificateInfo();
    }

    /**
     * 获取用户基本信息
     */
    private void getBasicInfo(final String registerId) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_USER_BASIC_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        try {
                            mBasicInfo = new Gson().fromJson(response, UserBasicInfo.class);
                            if (mBasicInfo != null) {
                                // 更新个人基本信息

                                L.i("服务读取的生日是：" + DateUtil.parseMysqlDateToString(mBasicInfo.getBirthday()));
                                UserUtil.saveBasicInfo(mBasicInfo);
                                updateBasicInfo();
                            }
                        } catch (JsonSyntaxException e) {
                            L.i("获取基本信息失败");
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获取用户专业技术资格证信息
     */
    private void getProfessionalCertificateInfo(final String registerId) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_PROFESSIONAL_CERTIFICATE_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        try {
                            mProfessionalCertificateInfo = new Gson().fromJson(response, UserProfessionalCertificateInfo.class);

                            if (mProfessionalCertificateInfo != null) {
                                // 更新个人执业证信息
                                UserUtil.saveProfessionalCertificateInfo(mProfessionalCertificateInfo);
                                updateProfessionalCertificateInfo();
                            }
                        } catch (JsonSyntaxException e) {
                            L.i("获取执业证信息失败");
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 获取用户执业证信息
     */
    private void getPractisingCertificateInfo(final String registerId) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_PRACTISING_CERTIFICATE_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        try {
                            mPractisingCertificateInfo = new Gson().fromJson(response, UserPractisingCertificateInfo.class);
                            if (mPractisingCertificateInfo != null) {
                                // 更新个人专业技术资格证信息
                                UserUtil.savePractisingCertificateInfo(mPractisingCertificateInfo);
                                updatePractisingCertificateInfo();
                            }
                        } catch (JsonSyntaxException e) {
                            L.i("获取专业技术资格证信息失败");
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 获取用户医院信息
     */
    private void getHospitalInfo(final String registerId) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_USER_HOSPITAL_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        try {
                            mHospitalInfo = new Gson().fromJson(response, UserHospitalInfo.class);
                            if (mHospitalInfo != null) {
                                // 更新个人医院信息
                                UserUtil.saveHospitalInfo(mHospitalInfo);
                                updateHospitalInfo();
                            }
                        } catch (JsonSyntaxException e) {
                            L.i("获取医院信息失败");
                            e.printStackTrace();
                        }
                    }
                });
    }


    /**
     * 更新用户注册信息
     */
    private void updateRegisterInfo() {

        mRegisterInfo = DataSupport.findLast(UserRegisterInfo.class);


        if (mRegisterInfo != null) {
            if (!TextUtils.isEmpty(mRegisterInfo.getAvatar())) {

            }
            if (!TextUtils.isEmpty(mRegisterInfo.getName())) {
                mNameTextView.setText(mRegisterInfo.getName());
            }
            if (!TextUtils.isEmpty(mRegisterInfo.getNickName())) {
                mNicknameTextView.setText(mRegisterInfo.getNickName());
            }
        }
    }


    /**
     * 更新用户基础信息
     */
    private void updateBasicInfo() {

        mBasicInfo = DataSupport.findLast(UserBasicInfo.class);

        if (mBasicInfo != null) {
            if (!TextUtils.isEmpty(mBasicInfo.getSex())) {
                mSexTextView.setText(mBasicInfo.getSex());
            }
            if (!TextUtils.isEmpty(mBasicInfo.getBirthday())) {
                L.i("数据库读取的生日是：" + DateUtil.parseMysqlDateToString(mBasicInfo.getBirthday()));
                mBirthdayTextView.setText(DateUtil.parseMysqlDateToString(mBasicInfo.getBirthday()));
            }

            Date origin = DateUtil.parseMysqlDateToDate(mBasicInfo.getBirthday());

            setDateSelectListener(mBirthdayLayout, origin, new OnDateSelectListener() {
                        @Override
                        public void onDateSelected(Date date) {
                            mBasicInfo.setBirthday(DateUtil.parseDateToMysqlDate(date));
                            L.i("设置的生日是：" + DateUtil.parseMysqlDateToString(mBasicInfo.getBirthday()));
                            postUserBasicInfo();
                        }
                    }
            );
        }
    }


    /**
     * 更新用户专业技术资格证信息
     */
    private void updateProfessionalCertificateInfo() {

        mProfessionalCertificateInfo = DataSupport.
                findLast(UserProfessionalCertificateInfo.class);

        if (mProfessionalCertificateInfo != null) {
            mProfessionalCertificateTextView.
                    setText(getAuditString(mProfessionalCertificateInfo.getVerifyStatus()));

        }
    }

    /**
     * 更新用户执业证信息
     */
    private void updatePractisingCertificateInfo() {
        mPractisingCertificateInfo = DataSupport.
                findLast(UserPractisingCertificateInfo.class);

        if (mPractisingCertificateInfo != null) {
            mPractisingCertificateTextView.
                    setText(getAuditString(mPractisingCertificateInfo.getVerifyStatus()));

        }
    }

    /**
     * 更新用户医院信息
     */
    private void updateHospitalInfo() {
        mHospitalInfo = DataSupport.findLast(UserHospitalInfo.class);

        if (mHospitalInfo != null) {
            if (!TextUtils.isEmpty(mHospitalInfo.getEmployeeId())) {
                mJobNumberTextView.setText(mHospitalInfo.getEmployeeId());
            }
        }
    }

    private String getAuditString(int status) {
        if (status == ServiceConstant.AUDIT_EMPTY) {
            return "未认证";
        } else if (status == ServiceConstant.AUDIT_ING) {
            return "认证中";
        } else if (status == ServiceConstant.AUDIT_FAILED) {
            return "未通过";
        } else if (status == ServiceConstant.AUDIT_SUCCESS) {
            return "已认证";
        } else {
            return "未认证";
        }
    }


    /**
     * 修改昵称
     *
     * @param view
     */
    @OnClick(R.id.rl_nickname)
    void onNickname(View view) {
        Intent intent = InputActivity.getIntent(this, InputActivity.NICKNAME);
        startActivityForResult(intent, REQUEST_REGISTER_INFO);
    }

    /**
     * 修改性别
     *
     * @param view
     */
    @OnClick(R.id.rl_sex)
    void onSex(View view) {
        String sex = mBasicInfo.getSex();
        Intent intent = SexActivity.getIntent(this, sex);
        startActivityForResult(intent, REQUEST_BASIC_INFO);
    }

    /**
     * 更新用户基础信息
     */
    private void postUserBasicInfo() {
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_BASIC_INFO)
                .content(StringUtil.addModelWithJson(mBasicInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        if (response.equals(REQUEST_SUCCESS)) {
                            L.i("设置生日成功");
                            // 设置成功后更新数据库
                            UserUtil.saveBasicInfo(mBasicInfo);
                            updateBasicInfo();
                        } else {
                            L.i("设置生日失败");
                        }
                    }
                });
    }

    /**
     * 专业技术资格证书
     *
     * @param view
     */
    @OnClick(R.id.rl_professional_certificate)
    void onNurseCertificate(View view) {
        Intent intent = NoCertificateActivity.getIntent(this, NoCertificateActivity.PROFESSIONAL_CERTIFICATE);
        startActivity(intent);
    }

    /**
     * 护士执业证书
     *
     * @param view
     */
    @OnClick(R.id.rl_practising_certificate)
    void onNursePractisingCertificate(View view) {
        Intent intent = NoCertificateActivity.getIntent(this, NoCertificateActivity.PRACTISING_CERTIFICATE);
        startActivity(intent);
    }

    @OnClick(R.id.rl_hospital)
    void onHospital(View view) {
        Intent intent = HospitalActivity.getIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.rl_office)
    void onOffice(View view) {
        Intent intent = OfficeActivity.getIntent(this);
        startActivity(intent);
    }

    @OnClick(R.id.rl_job_number)
    void onJobNumber(View view) {
        Intent intent = InputActivity.getIntent(this, "工号");
        startActivityForResult(intent, REQUEST_HOSPITAL_INFO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 通过回调类型更新界面
            if (requestCode == REQUEST_REGISTER_INFO) {
                updateRegisterInfo();
            } else if (requestCode == REQUEST_BASIC_INFO) {
                updateBasicInfo();
            } else if (requestCode == REQUEST_HOSPITAL_INFO) {
                updateHospitalInfo();
            }
        }
    }
}
