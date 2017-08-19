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
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.OnDateSelectListener;
import com.jerry.nurse.listener.OnPhotoSelectListener;
import com.jerry.nurse.model.CommonResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.UploadResult;
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
import com.jerry.nurse.util.LitePalUtil;
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
import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class PersonalInfoActivity extends BaseActivity {

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

        mLoginInfo = DataSupport.findFirst(LoginInfo.class);

        initData();

        // 设置图片点击监听
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
            Glide.with(this).load(mLoginInfo.getAvatar()).placeholder(R.drawable.icon_avatar_default)
                    .into(mAvatarView);
        }
        mNameTextView.setText(mLoginInfo.getName());
        mNicknameTextView.setText(mLoginInfo.getNickName());
        mHospitalTextView.setText(mLoginInfo.getHospitalName());
        mOfficeTextView.setText(mLoginInfo.getDepartmentName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUseInfo();
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
                        updateUseInfo();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        UserInfoResult userInfoResult = new Gson().fromJson(response, UserInfoResult.class);
                        if (userInfoResult.getCode() == RESPONSE_SUCCESS) {
                            mUserInfo = userInfoResult.getBody();
                            if (mUserInfo != null) {
                                LoginInfo loginInfo = DataSupport.findFirst(LoginInfo.class);
                                // 更新登录数据的两证状态
                                loginInfo.setPStatus(mUserInfo.getPVerifyStatus());
                                loginInfo.setQStatus(mUserInfo.getQVerifyStatus());
                                loginInfo.save();

                                LitePalUtil.saveUserInfo(PersonalInfoActivity.this, mUserInfo);
                            }
                        } else {
                            T.showShort(PersonalInfoActivity.this, "获取基本信息失败");
                            L.i("获取基本信息失败");
                        }
                        updateUseInfo();
                    }
                });
    }

    /**
     * 更新界面信息
     */
    private void updateUseInfo() {
        mUserInfo = DataSupport.findFirst(UserInfo.class);
        if (mUserInfo != null) {
            // 头像
            if (!TextUtils.isEmpty(mUserInfo.getAvatar())) {
                if (mUserInfo.getAvatar().startsWith("http")) {
                    Glide.with(this).load(mUserInfo.getAvatar()).into(mAvatarView);
                } else {
                    Glide.with(this).load(AVATAR_ADDRESS + mUserInfo.getAvatar()).into(mAvatarView);
                }
            }

            // 个人信息
            // 设置姓名
            if (mUserInfo.getName() != null) {
                mNameTextView.setText(mUserInfo.getName());
                mNameTextView.setTextColor(getResources().getColor(R.color.normal_textColor));
            }
            // 设置昵称
            if (mUserInfo.getNickName() != null) {
                mNicknameTextView.setText(mUserInfo.getNickName());
            } else {
                mNicknameTextView.setText("未设置");
            }
            mSexTextView.setText(mUserInfo.getSex());
            mBirthdayTextView.setText(DateUtil.parseMysqlDateToString(mUserInfo.getBirthday()));


            // 执业信息
            mProfessionalCertificateTextView.setText(getAuditString(mUserInfo.getQVerifyStatus()));
            mPractisingCertificateTextView.setText(getAuditString(mUserInfo.getPVerifyStatus()));
            if (mUserInfo.getPVerifyStatus() == AUDIT_SUCCESS &&
                    mUserInfo.getQVerifyStatus() == AUDIT_SUCCESS) {
                String nursingAge = getWorkingTime(mUserInfo.getFirstJobTime());
                mNursingAgeTextView.setText(nursingAge);
            }

            // 医院信息
            mHospitalTextView.setText(mUserInfo.getHospitalName());
            mOfficeTextView.setText(mUserInfo.getDepartmentName());
            mJobNumberTextView.setText(mUserInfo.getEmployeeId());

            // 设置生日数据和监听
            setDateSelectListener(mBirthdayLayout, DateUtil.parseMysqlDateToDate(mUserInfo.getBirthday()),
                    true, new OnDateSelectListener() {
                        @Override
                        public void onDateSelected(Date date) {
                            UserBasicInfo userBasicInfo = new UserBasicInfo();
                            userBasicInfo.setRegisterId(mUserInfo.getRegisterId());
                            userBasicInfo.setBirthday(DateUtil.parseDateToMysqlDate(date));
                            postUserBasicInfo(userBasicInfo);
                        }
                    }
            );
        }
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
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        UploadResult uploadResult = new Gson().fromJson(response, UploadResult.class);
                        if (uploadResult.getCode() == RESPONSE_SUCCESS) {
                            String fileName = uploadResult.getBody().getFilename();
                            postAvatar(fileName);
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
     * @param fileName
     */
    void postAvatar(final String fileName) {
        mProgressDialogManager.show();
        UserRegisterInfo info = new UserRegisterInfo();
        info.setRegisterId(mUserInfo.getRegisterId());
        info.setAvatar(fileName);
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_REGISTER_INFO)
                .content(StringUtil.addModelWithJson(info))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            L.i("设置头像成功");
                            mAvatarView.setImageBitmap(mAvatarBitmap);
                            // 设置成功后更新数据库
                            mUserInfo.setAvatar(fileName);
                            LitePalUtil.updateUserInfo(PersonalInfoActivity.this, mUserInfo);

                            mLoginInfo.setAvatar(fileName);
                            LitePalUtil.updateLoginInfo(PersonalInfoActivity.this, mLoginInfo);
                        } else {
                            L.i("设置头像失败");
                        }
                    }
                });
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
            return mouths / 12 + "年" + mouths % 12 + "月";
        } else {
            return mouths + "个月";
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
        startActivity(intent);
    }

    /**
     * 修改性别
     *
     * @param view
     */
    @OnClick(R.id.rl_sex)
    void onSex(View view) {
        String sex = mUserInfo.getSex();
        Intent intent = SexActivity.getIntent(this, sex);
        startActivity(intent);
    }

    /**
     * 更新用户基础信息
     *
     * @param userBasicInfo
     */
    private void postUserBasicInfo(final UserBasicInfo userBasicInfo) {
        mProgressDialogManager.show();
        OkHttpUtils.postString()
                .url(ServiceConstant.UPDATE_BASIC_INFO)
                .content(StringUtil.addModelWithJson(userBasicInfo))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CommonResult commonResult = new Gson().fromJson(response, CommonResult.class);
                        if (commonResult.getCode() == RESPONSE_SUCCESS) {
                            L.i("设置生日成功");
                            // 更新数据库
                            mUserInfo.setBirthday(userBasicInfo.getBirthday());
                            LitePalUtil.updateUserInfo(PersonalInfoActivity.this, mUserInfo);
                            updateUseInfo();
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
        startActivity(intent);
    }

    /**
     * 修改科室信息
     *
     * @param view
     */
    @OnClick(R.id.rl_office)
    void onOffice(View view) {
        if (!TextUtils.isEmpty(mUserInfo.getHospitalId())) {
            Intent intent = OfficeActivity.getIntent(this);
            startActivity(intent);
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
        startActivity(intent);
    }
}
