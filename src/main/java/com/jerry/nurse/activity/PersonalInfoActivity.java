package com.jerry.nurse.activity;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;

import com.jerry.nurse.R;
import com.jerry.nurse.util.L;

import java.util.Calendar;

import butterknife.BindString;
import butterknife.OnClick;


public class PersonalInfoActivity extends BaseActivity {

    @BindString(R.string.changeAvatar)
    String mStringChangeAvatar;

    @BindString(R.string.photograph)
    String mStringPhotograph;

    @BindString(R.string.selectFromAlbum)
    String mStringSelectFromAlbum;

    @BindString(R.string.ok)
    String mStringOk;

    @BindString(R.string.cancel)
    String mStringCancel;

    @BindString(R.string.sex)
    String mStringSex;

    @BindString(R.string.male)
    String mStringMale;

    @BindString(R.string.female)
    String mStringFemale;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_personal_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    /**
     * 修改头像
     * @param view
     */
    @OnClick(R.id.rl_avatar)
    void onAvatar(View view) {

        String[] items = {mStringPhotograph, mStringSelectFromAlbum};

        new AlertDialog
                .Builder(this)
                .setTitle(mStringChangeAvatar)
                .setNegativeButton(mStringCancel, null)
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            // 拍照
                            case 0:
                                break;
                            // 从相册选择
                            case 1:
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                }).show();

    }

    /**
     * 修改昵称
     * @param view
     */
    @OnClick(R.id.rl_nickname)
    void onNickname(View view) {
        Intent intent = NicknameActivity.getIntent(this);
        startActivity(intent);
    }

    /**
     * 修改性别
     * @param view
     */
    @OnClick(R.id.rl_sex)
    void onSex(View view) {

        String[] items = {mStringMale, mStringFemale};

        new AlertDialog
                .Builder(this)
                .setTitle(mStringSex)
                .setPositiveButton(mStringOk, null)
                .setNegativeButton(mStringCancel, null)
                .setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();

    }

    /**
     * 修改生日
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

    }

    @OnClick(R.id.rl_nurse_practising_certificate)
    void onNursePractisingCertificate(View view) {

    }

    @OnClick(R.id.rl_nursing_age)
    void onNursingAge(View view) {

    }

    @OnClick(R.id.rl_hospital)
    void onHospital(View view) {

    }

    @OnClick(R.id.rl_office)
    void onOffice(View view) {

    }

    @OnClick(R.id.rl_job_number)
    void onJobNumber(View view) {

    }
}
