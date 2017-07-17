package com.jerry.nurse.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.DatePicker;

import com.jerry.nurse.R;
import com.jerry.nurse.util.FileUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.PermissionListener;
import com.jerry.nurse.util.PictureUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;
import butterknife.OnClick;

public class PersonalInfoActivity extends BaseActivity {

    private static final int REQUEST_PHOTOGRAPH = 0;
    private static final int REQUEST_SELECT_FROM_ALBUM = 1;

    @Bind(R.id.civ_avatar)
    CircleImageView mAvatarView;

    @BindString(R.string.change_avatar)
    String mStringChangeAvatar;

    @BindString(R.string.photograph)
    String mStringPhotograph;

    @BindString(R.string.select_from_album)
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

    private Bitmap mAvatarBitmap;


    @Override
    public int getContentViewResId() {
        return R.layout.activity_personal_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    /**
     * 修改头像
     *
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
                                photograph();
                                break;
                            // 从相册选择
                            case 1:
                                selectFromAlbum();
                                break;
                            default:
                                break;
                        }
                        dialog.dismiss();
                    }
                }).show();

    }

    /**
     * 调用照相机拍照
     */
    private void photograph() {
        BaseActivity.requestRuntimePermission(new String[]{Manifest
                        .permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        //拿到sdcard是否可用的状态码
                        String state = Environment.getExternalStorageState();
                        if (state.equals(Environment.MEDIA_MOUNTED)) {
                            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
                            startActivityForResult(intent, REQUEST_PHOTOGRAPH);
                        } else {
                            T.showLong(PersonalInfoActivity.this, R
                                    .string.sdcard_disabled);
                        }
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {

                    }
                });
    }


    /**
     * 从本地选取照片
     */
    private void selectFromAlbum() {
        BaseActivity.requestRuntimePermission(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");//相片类型
                        startActivityForResult(intent, REQUEST_SELECT_FROM_ALBUM);
                    }

                    @Override
                    public void onDenied(List<String> deniedPermissions) {

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        Uri uri = data.getData();
        switch (requestCode) {
            case REQUEST_PHOTOGRAPH:
                // 避免有时候获取到的uri是空的情况
                if (uri == null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        try {
                            // 从Bundle中获取图片
                            mAvatarBitmap = (Bitmap) bundle.get("data");

                            File externalFilesDir = getExternalFilesDir(Environment
                                    .DIRECTORY_PICTURES);
                            File file = new File(externalFilesDir, System
                                    .currentTimeMillis() + ".jpg");
                            L.i("文件路径：" + file.getPath());
                            // 打开文件输出流
                            FileOutputStream fileOutputStream = new
                                    FileOutputStream(file);
                            // 生成图片文件
                            mAvatarBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            // 压缩图片
                            mAvatarBitmap = PictureUtil.getScaleBitmap
                                    (file.getPath(), this);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        T.showLong(PersonalInfoActivity.this,
                                R.string.photograph_failed);
                        return;
                    }
                } else {
                    // 压缩图片
                    mAvatarBitmap = PictureUtil.getScaleBitmap
                            (FileUtil.getRealFilePath(this, uri), this);
                }
                break;
            case REQUEST_SELECT_FROM_ALBUM:
                // 压缩图片
                mAvatarBitmap = PictureUtil.getScaleBitmap
                        (FileUtil.getRealFilePath(this, uri), this);

                break;
            default:
                break;
        }

        mAvatarView.setImageBitmap(mAvatarBitmap);
    }

    /**
     * 修改昵称
     *
     * @param view
     */
    @OnClick(R.id.rl_nickname)
    void onNickname(View view) {
        Intent intent = NicknameActivity.getIntent(this);
        startActivity(intent);
    }

    /**
     * 修改性别
     *
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
