package com.jerry.nurse.activity;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.RelativeLayout;

import com.jerry.nurse.R;
import com.jerry.nurse.listener.PhotoSelectListener;
import com.jerry.nurse.util.L;
import com.jerry.nurse.view.CircleImageView;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

import static com.jerry.nurse.constant.ExtraValue.EXTRA_IS_FIRST_LOGIN;

public class PersonalInfoActivity extends BaseActivity {

    @Bind(R.id.civ_avatar)
    CircleImageView mAvatarView;

    @Bind(R.id.rl_avatar)
    RelativeLayout mAvatarLayout;

    @BindString(R.string.change_avatar)
    String mStringChangeAvatar;

    @BindString(R.string.male)
    String mStringMale;

    @BindString(R.string.female)
    String mStringFemale;

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
        setPhotoSelectListener(mAvatarLayout, new PhotoSelectListener() {
            @Override
            public void onPhotoSelected(Bitmap bitmap) {
                mAvatarView.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * 修改昵称
     *
     * @param view
     */
    @OnClick(R.id.rl_nickname)
    void onNickname(View view) {
        Intent intent = InputActivity.getIntent(this, R.string.nickname);
        startActivity(intent);
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

        Calendar cal = Calendar.getInstance();
        final DatePickerDialog datePickerDialog = new DatePickerDialog(this, null,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
        //手动设置按钮
        datePickerDialog.setButton(DialogInterface.BUTTON_POSITIVE, "完成", new DialogInterface.OnClickListener() {
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
        datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("BUTTON_NEGATIVE~~");
            }
        });
        datePickerDialog.show();
    }

    @OnClick(R.id.rl_nurse_certificate)
    void onNurseCertificate(View view) {
        Intent intent = CertificateActivity.getIntent(this, R.string.nurse_certificate);
        startActivity(intent);
    }

    @OnClick(R.id.rl_nurse_practising_certificate)
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
