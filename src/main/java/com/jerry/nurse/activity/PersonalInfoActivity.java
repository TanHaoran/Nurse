package com.jerry.nurse.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.listener.PhotoSelectListener;
import com.jerry.nurse.model.User;
import com.jerry.nurse.model.UserBasicInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.util.UserUtil;
import com.jerry.nurse.view.CircleImageView;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;
import okhttp3.Call;

import static com.jerry.nurse.constant.ExtraValue.EXTRA_IS_FIRST_LOGIN;

public class PersonalInfoActivity extends BaseActivity {

    public static final int REQUEST_NICKNAME = 0x00000101;

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

    @Bind(R.id.tv_certificate)
    TextView mCertificateTextView;

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

    @BindString(R.string.change_avatar)
    String mStringChangeAvatar;

    @BindString(R.string.male)
    String mStringMale;

    @BindString(R.string.female)
    String mStringFemale;

    private User mUser;

    private String mRegisterId;

    public static Intent getIntent(Context context, boolean isFirstLogin) {
        Intent intent = new Intent(context, PersonalInfoActivity.class);
        intent.putExtra(EXTRA_IS_FIRST_LOGIN, isFirstLogin);
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

        mUser = DataSupport.findFirst(User.class);
        getUserBasicInfo(mRegisterId);
    }

    /**
     * 获取用户基本信息
     */
    private void getUserBasicInfo(final String registerId) {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_USER_BASIC_INFO)
                .addParams("RegisterId", registerId)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        try {
                            UserBasicInfo userBasicInfo = new Gson().fromJson(response, UserBasicInfo.class);
                            if (userBasicInfo != null) {
                                // 更新个人基本信息
                                updateUserBasicInfo(userBasicInfo);
                                setUserInfo();
                            }
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /**
     * 跟新个人基本信息
     *
     * @param userBasicInfo
     */
    private void updateUserBasicInfo(UserBasicInfo userBasicInfo) {
        mUser = DataSupport.findFirst(User.class);
        mUser.setBirthday(userBasicInfo.getBirthday());
        mUser.setName(userBasicInfo.getName());
        mUser.setPhone(userBasicInfo.getPhone());
        mUser.setQQ(userBasicInfo.getQQ());
        mUser.setSex(userBasicInfo.getSex());

        // 更新数据库
        UserUtil.updateUser(this, mUser);
    }


    /**
     * 初始化用户信息
     */
    private void setUserInfo() {

        mUser = DataSupport.findFirst(User.class);

        if (!TextUtils.isEmpty(mUser.getAvatar())) {

        }
        if (!TextUtils.isEmpty(mUser.getName())) {
            mNameTextView.setText(mUser.getName());
        }
        if (!TextUtils.isEmpty(mUser.getNickName())) {
            mNicknameTextView.setText(mUser.getNickName());
        }
        if (!TextUtils.isEmpty(mUser.getSex())) {
            mSexTextView.setText(mUser.getSex());
        }
        if (!TextUtils.isEmpty(mUser.getBirthday())) {
            mBirthdayTextView.setText(mUser.getBirthday());
        }
        if (!TextUtils.isEmpty(mUser.getName())) {
            mCertificateTextView.setText(mUser.getName());
        }
        if (!TextUtils.isEmpty(mUser.getName())) {
            mPractisingCertificateTextView.setText(mUser.getName());
        }
        if (!TextUtils.isEmpty(mUser.getName())) {
            mNursingAgeTextView.setText(mUser.getName());
        }
        if (!TextUtils.isEmpty(mUser.getName())) {
            mHospitalTextView.setText(mUser.getName());
        }
        if (!TextUtils.isEmpty(mUser.getName())) {
            mOfficeTextView.setText(mUser.getName());
        }
        if (!TextUtils.isEmpty(mUser.getName())) {
            mJobNumberTextView.setText(mUser.getName());
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
        startActivityForResult(intent, REQUEST_NICKNAME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            mUser = DataSupport.findFirst(User.class);
            if (requestCode == REQUEST_NICKNAME) {
                mNicknameTextView.setText(mUser.getNickName());
            }
        }
    }

    /**
     * 修改性别
     *
     * @param view
     */
    @OnClick(R.id.rl_sex)
    void onSex(View view) {
        Intent intent = SexActivity.getIntent(this);
        startActivity(intent);
    }

    /**
     * 修改生日
     *
     * @param view
     */
    @OnClick(R.id.rl_birthday)
    void onBirthday(View view) {

        if (!TextUtils.isEmpty(mUser.getBirthday())) {
            Date birthDate = DateUtil.parseStringToDate(mUser.getBirthday());

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(birthDate);

            final DatePickerDialog datePickerDialog = new DatePickerDialog(this, null,
                    calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));

            //手动设置按钮
            datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //通过mDialog.getDatePicker()获得dialog上的DatePicker组件，然后可以获取日期信息
                            DatePicker datePicker = datePickerDialog.getDatePicker();
                            int year = datePicker.getYear();
                            int month = datePicker.getMonth();
                            int day = datePicker.getDayOfMonth();
                            L.i(year + "," + month + "," + day);
                        }
                    });

            //取消按钮，如果不需要直接不设置即可
            datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            System.out.println("BUTTON_NEGATIVE~~");
                        }
                    });
            datePickerDialog.show();
        } else {
            L.i("生日格式出错");
        }
    }

    @OnClick(R.id.rl_certificate)
    void onNurseCertificate(View view) {
        Intent intent = CertificateActivity.getIntent(this, R.string.nurse_certificate);
        startActivity(intent);
    }

    @OnClick(R.id.rl_practising_certificate)
    void onNursePractisingCertificate(View view) {
        Intent intent = CertificateActivity.getIntent(this, R.string.nurse_practising_certificate);
        startActivity(intent);
    }

    @OnClick(R.id.rl_nursing_age)
    void onNursingAge(View view) {

    }

    @OnClick(R.id.rl_hospital)
    void onHospital(View view) {
        Intent intent = InputActivity.getIntent(this, R.string.hospital);
        startActivity(intent);
    }

    @OnClick(R.id.rl_office)
    void onOffice(View view) {
        Intent intent = InputActivity.getIntent(this, R.string.business);
        startActivity(intent);
    }

    @OnClick(R.id.rl_job_number)
    void onJobNumber(View view) {
        Intent intent = InputActivity.getIntent(this, R.string.job_number);
        startActivity(intent);
    }
}
