package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.OnDateSelectListener;
import com.jerry.nurse.listener.OnPhotoSelectListener;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.UserBasicInfo;
import com.jerry.nurse.model.UserHospitalInfo;
import com.jerry.nurse.model.UserInfo;
import com.jerry.nurse.model.UserInfoResult;
import com.jerry.nurse.model.UserPractisingCertificateInfo;
import com.jerry.nurse.model.UserProfessionalCertificateInfo;
import com.jerry.nurse.model.UserRegisterInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;

import static com.jerry.nurse.constant.ServiceConstant.AUDIT_SUCCESS;
import static com.jerry.nurse.constant.ServiceConstant.AVATAR_ADDRESS;
import static com.jerry.nurse.constant.ServiceConstant.REQUEST_SUCCESS;
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;
import static com.jerry.nurse.constant.ServiceConstant.USER_COLON;
import static com.jerry.nurse.constant.ServiceConstant.USER_FILE;
import static org.litepal.crud.DataSupport.findFirst;

public class PersonalInfoActivity extends BaseActivity {

    public static final int REQUEST_REGISTER_INFO = 0x00000101;
    public static final int REQUEST_BASIC_INFO = 0x00000102;
    public static final int REQUEST_HOSPITAL_INFO = 0x00000105;

    @Bind(R.id.civ_avatar)
    ImageView mAvatarView;

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


    private Bitmap mAvatarBitmap;

    private ProgressDialogManager mProgressDialogManager;

    private LoginInfo mLoginInfo;

    private UserInfo mUserInfo;

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
        mProgressDialogManager = new ProgressDialogManager(this);

        mLoginInfo = findFirst(LoginInfo.class);

        initData();

        // 设置图片
        setPhotoSelectListener(mAvatarLayout, 0, new OnPhotoSelectListener() {
            @Override
            public void onPhotoSelected(Bitmap bitmap, File file) {
                mAvatarBitmap = bitmap;
                postUserAvatar(file);
            }
        });

        // 获取个人信息
        getUserInfo(mLoginInfo.getRegisterId());
    }

    /**
     * 根据登陆信息初始化界面
     */
    private void initData() {
        if (!TextUtils.isEmpty(mLoginInfo.getAvatar())) {
            if (mLoginInfo.getAvatar().startsWith("http")) {
                Glide.with(this).load(mLoginInfo.getAvatar()).into(mAvatarView);
            } else {
                Glide.with(this).load(AVATAR_ADDRESS + mLoginInfo.getAvatar()).into(mAvatarView);
            }
        }
        mNameTextView.setText(mLoginInfo.getName());
        mHospitalTextView.setText(mLoginInfo.getHospitalName());
        mOfficeTextView.setText(mLoginInfo.getDepartmentName());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * 获取用户信息
     */
    private void getUserInfo(final String registerId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_USER_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    protected void onFilterError(Call call, Exception e, int id) {
                        mUserInfo = DataSupport.findFirst(UserInfo.class);
                        updateUseInfo();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        UserInfoResult userInfoResult = new Gson().fromJson(response, UserInfoResult.class);
                        if (userInfoResult.getCode() == RESPONSE_SUCCESS) {
                            mUserInfo = userInfoResult.getBody();
                            if (mUserInfo != null) {
                                DataSupport.deleteAll(UserInfo.class);
                                mUserInfo.save();
                                updateUseInfo();
                            } else {
                                mUserInfo = DataSupport.findFirst(UserInfo.class);
                                updateUseInfo();
                            }
                        } else {
                            T.showShort(PersonalInfoActivity.this, "获取基本信息失败");
                            L.i("获取基本信息失败");
                        }
                    }
                });
    }

    /**
     * 更新界面信息
     */
    private void updateUseInfo() {
        if (!TextUtils.isEmpty(mLoginInfo.getAvatar())) {
            if (mUserInfo.getAvatar().startsWith("http")) {
                Glide.with(this).load(mUserInfo.getAvatar()).into(mAvatarView);
            } else {
                Glide.with(this).load(AVATAR_ADDRESS + mUserInfo.getAvatar()).into(mAvatarView);
            }
        }

        mNameTextView.setText(mUserInfo.getName());
        mNicknameTextView.setText(mUserInfo.getNickName());
        mSexTextView.setText(mUserInfo.getSex());
        mBirthdayTextView.setText(DateUtil.parseMysqlDateToString(mUserInfo.getBirthday()));

        mProfessionalCertificateTextView.setText(getAuditString(mUserInfo.getPVerifyStatus()));
        mPractisingCertificateTextView.setText(getAuditString(mUserInfo.getQVerifyStatus()));
        if (mUserInfo.getPVerifyStatus() == AUDIT_SUCCESS &&
                mUserInfo.getQVerifyStatus() == AUDIT_SUCCESS) {
            String nursingAge = getWorkingTime(mUserInfo.getFirstJobTime());
            mNursingAgeTextView.setText(nursingAge);
        }

        mHospitalTextView.setText(mUserInfo.getHospitalName());
        mOfficeTextView.setText(mUserInfo.getDepartmentName());
        mJobNumberTextView.setText(mUserInfo.getEmployeeId());
    }

    /**
     * 获取用户专业技术资格证信息
     */
    private void getProfessionalCertificateInfo(final String registerId) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_PROFESSIONAL_CERTIFICATE_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialogManager.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialogManager.dismiss();
                        try {
                            mProfessionalCertificateInfo = new Gson().fromJson(response, UserProfessionalCertificateInfo.class);

                            if (mProfessionalCertificateInfo != null) {
                                // 更新个人执业证信息
//                                LitePalUtil.saveProfessionalCertificateInfo(mProfessionalCertificateInfo);
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
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_PRACTISING_CERTIFICATE_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialogManager.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialogManager.dismiss();
                        try {
                            mPractisingCertificateInfo = new Gson().fromJson(response, UserPractisingCertificateInfo.class);
                            if (mPractisingCertificateInfo != null) {
                                // 更新个人专业技术资格证信息
//                                LitePalUtil.savePractisingCertificateInfo(mPractisingCertificateInfo);
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
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_USER_HOSPITAL_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialogManager.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialogManager.dismiss();
                        try {
                            mHospitalInfo = new Gson().fromJson(response, UserHospitalInfo.class);
                            if (mHospitalInfo != null) {
                                // 更新个人医院信息
//                                LitePalUtil.saveHospitalInfo(mHospitalInfo);
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
     * 上传头像
     *
     * @param file
     */
    private void postUserAvatar(File file) {
        mProgressDialogManager.show();
        Map<String, String> headers = new HashMap<>();
        headers.put("Accept", "*/*");
        OkHttpUtils.post()
                .addFile("avatar", file.getName(), file)
                .url(ServiceConstant.UPLOAD_AVATAR)
                .headers(headers)
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    protected void onFilterError(Call call, Exception e, int id) {
                        mProgressDialogManager.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialogManager.dismiss();
                        if (response.startsWith(USER_FILE)) {
                            if (response.split(USER_COLON).length == 2) {
                                String name = response.split(USER_COLON)[1];
                                postAvatar(name);
                            }
                        } else {
                            L.i("上传头像失败");
                            T.showShort(PersonalInfoActivity.this, R.string.upload_failed);
                        }
                    }
                });
    }

    /**
     * 设置头像
     *
     * @param name
     */
    void postAvatar(String name) {
        mProgressDialogManager.show();
        mRegisterInfo.setAvatar(name);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_REGISTER_INFO)
                .content(StringUtil.addModelWithJson(mRegisterInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback() {
                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialogManager.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialogManager.dismiss();
                        if (response.equals(REQUEST_SUCCESS)) {
                            L.i("设置头像成功");
                            mAvatarView.setImageBitmap(mAvatarBitmap);
                            // 设置成功后更新数据库
//                            LitePalUtil.saveRegisterInfo(PersonalInfoActivity.this, mRegisterInfo);
                            updateRegisterInfo();
                        } else {
                            L.i("设置头像失败");
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
            // 设置头像
            if (!TextUtils.isEmpty(mRegisterInfo.getAvatar())) {
                if (mRegisterInfo.getAvatar().startsWith("http")) {
                    Glide.with(this).load(mRegisterInfo.getAvatar()).into(mAvatarView);
                } else {
                    Glide.with(this).load(AVATAR_ADDRESS + mRegisterInfo.getAvatar()).into(mAvatarView);
                }
            }
            // 设置姓名
            if (mRegisterInfo.getName() != null) {
                mNameTextView.setText(mRegisterInfo.getName());
                mNameTextView.setTextColor(getResources().getColor(R.color.normal_textColor));
            }
            // 设置昵称
            if (mRegisterInfo.getNickName() != null) {
                mNicknameTextView.setText(mRegisterInfo.getNickName());
            } else {
                mNicknameTextView.setText("未设置");
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

            setDateSelectListener(mBirthdayLayout, origin, true, new OnDateSelectListener() {
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
            int professionalStatus = mProfessionalCertificateInfo.getVerifyStatus();
            mProfessionalCertificateTextView.
                    setText(getAuditString(professionalStatus));
        }

        if (mProfessionalCertificateInfo != null && mPractisingCertificateInfo != null) {
            // 显示认证情况
            int professionalStatus = mProfessionalCertificateInfo.getVerifyStatus();
            int practisingStatus = mPractisingCertificateInfo.getVerifyStatus();
            // 如果两证都审核通过，就计算工龄
            if (professionalStatus == AUDIT_SUCCESS && practisingStatus == AUDIT_SUCCESS) {
                String nursingAge = getWorkingTime(mPractisingCertificateInfo.getFirstJobTime());
                mNursingAgeTextView.setText(nursingAge);
            }
        }
    }

    /**
     * 更新用户执业证信息
     */
    private void updatePractisingCertificateInfo() {
        mPractisingCertificateInfo = DataSupport.
                findLast(UserPractisingCertificateInfo.class);

        if (mPractisingCertificateInfo != null) {
            int practisingStatus = mPractisingCertificateInfo.getVerifyStatus();
            mPractisingCertificateTextView.
                    setText(getAuditString(practisingStatus));
        }

        if (mPractisingCertificateInfo != null && mProfessionalCertificateInfo != null) {
            // 显示认证情况
            int professionalStatus = mProfessionalCertificateInfo.getVerifyStatus();
            int practisingStatus = mPractisingCertificateInfo.getVerifyStatus();
            // 如果两证都审核通过，就计算工龄
            if (professionalStatus == AUDIT_SUCCESS && practisingStatus == AUDIT_SUCCESS) {
                String nursingAge = getWorkingTime(mPractisingCertificateInfo.getFirstJobTime());
                mNursingAgeTextView.setText(nursingAge);
            }

        }
    }

    /**
     * 计算工龄
     *
     * @return
     */
    private String getWorkingTime(String firstWorkDate) {
        Date firstDate = DateUtil.parseMysqlDateToDate(firstWorkDate);
        Date now = new Date();
        int mouths = DateUtil.getMonthsBetweenTwoDate(firstDate, now);
        if (mouths >= 12) {
            return mouths / 12 + "年";
        } else {
            return mouths + "个月";
        }
    }

    /**
     * 更新用户医院信息
     */
    private void updateHospitalInfo() {
        mHospitalInfo = DataSupport.findLast(UserHospitalInfo.class);

        if (mHospitalInfo != null) {
            if (!TextUtils.isEmpty(mHospitalInfo.getHospitalName())) {
                mHospitalTextView.setText(mHospitalInfo.getHospitalName());
            }
            if (!TextUtils.isEmpty(mHospitalInfo.getDepartmentName())) {
                mOfficeTextView.setText(mHospitalInfo.getDepartmentName());
            }
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
//                            LitePalUtil.saveBasicInfo(mBasicInfo);
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

    /**
     * 修改医院信息
     *
     * @param view
     */
    @OnClick(R.id.rl_hospital)
    void onHospital(View view) {
        Intent intent = HospitalActivity.getIntent(this);
        startActivityForResult(intent, REQUEST_HOSPITAL_INFO);
    }

    /**
     * 修改科室信息
     *
     * @param view
     */
    @OnClick(R.id.rl_office)
    void onOffice(View view) {
        if (!TextUtils.isEmpty(mHospitalInfo.getHospitalId())) {
            Intent intent = OfficeActivity.getIntent(this);
            startActivityForResult(intent, REQUEST_HOSPITAL_INFO);
        } else {
            T.showShort(this, R.string.please_select_hospital_first);
        }
    }

    /**
     * 修改工号
     *
     * @param view
     */
    @OnClick(R.id.rl_job_number)
    void onJobNumber(View view) {
        Intent intent = InputActivity.getIntent(this, "工号");
        startActivityForResult(intent, REQUEST_HOSPITAL_INFO);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
