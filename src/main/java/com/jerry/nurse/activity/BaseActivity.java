package com.jerry.nurse.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;

import com.jerry.nurse.R;
import com.jerry.nurse.listener.OnDateSelectListener;
import com.jerry.nurse.listener.PermissionListener;
import com.jerry.nurse.listener.PhotoSelectListener;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.FileUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.PictureUtil;
import com.jerry.nurse.util.ScreenUtil;
import com.jerry.nurse.util.T;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;

/**
 * Created by Jerry on 2017/7/15.
 */

public abstract class BaseActivity extends AppCompatActivity {

    @BindString(R.string.photograph)
    String mStringPhotograph;

    @BindString(R.string.select_from_album)
    String mStringSelectFromAlbum;

    private static final int REQUEST_PERMISSION_RESULT = 0x00001001;

    private static final int REQUEST_PHOTOGRAPH = 0x00001002;
    private static final int REQUEST_SELECT_FROM_ALBUM = 0x00001003;

    private static PermissionListener mPermissionListener;
    private static PhotoSelectListener mPhotoSelectListener;

    public static ProgressDialog mProgressDialog;

    /**
     * 获取当前页面的布局
     *
     * @return
     */
    public abstract int getContentViewResId();

    /**
     * 初始化页面
     *
     * @param savedInstanceState
     */
    public abstract void init(Bundle savedInstanceState);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        setContentView(getContentViewResId());
        ScreenUtil.setWindowStatusBarColor(this, R.color.primary);
        ButterKnife.bind(this);

        // 初始化等待框
        mProgressDialog = new ProgressDialog(this,
                R.style.AppTheme_Dark_Dialog);
        // 设置不定时等待
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("请稍后...");

        init(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册友盟统计分析
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        ActivityCollector.removeActivity(this);
    }

    /**
     * 申请权限
     *
     * @param permissions 所需要的权限数组
     * @param listener    申请权限结果回调
     */
    public static void requestRuntimePermission(String[] permissions, PermissionListener listener) {
        // 获取应用程序栈顶的Activity，用来申请权限
        Activity topActivity = ActivityCollector.getTopActivity();
        if (topActivity == null) {
            return;
        }
        mPermissionListener = listener;
        List<String> permissionList = new ArrayList<>();
        for (String permission : permissions) {
            // 如果没有这个权限，就添加到集合中
            if (ContextCompat.checkSelfPermission(topActivity, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission);
            }
        }
        // 如果拒绝权限列表不空，就开始申请权限
        if (!permissionList.isEmpty()) {
            ActivityCompat.requestPermissions(topActivity,
                    permissionList.toArray(new String[permissionList.size()]), REQUEST_PERMISSION_RESULT);
        } else {
            mPermissionListener.onGranted();
        }
    }

    /**
     * 请求权限的回调
     *
     * @param requestCode  请求码
     * @param permissions  请求权限列表数组
     * @param grantResults 请求结果数组
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_RESULT:
                if (grantResults.length > 0) {
                    List<String> deniedPermissions = new ArrayList<>();
                    // 遍历所有请求结果，如果有被拒绝的，添加到拒绝集合中
                    for (int i = 0; i < grantResults.length; i++) {
                        int grantResult = grantResults[i];
                        String permission = permissions[i];
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            deniedPermissions.add(permission);
                        }
                    }
                    if (deniedPermissions.isEmpty()) {
                        mPermissionListener.onGranted();
                    } else {
                        mPermissionListener.onDenied(deniedPermissions);
                    }
                }
                break;
            default:
                break;
        }
    }

    /**
     * 给一个View设置日期选择控件
     *
     * @param view
     * @param origin
     * @param onDateSelectListener
     */
    public void setDateSelectListener(View view, final Date origin, final OnDateSelectListener onDateSelectListener) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if (origin != null) {
                    calendar.setTime(origin);
                }

                final DatePickerDialog datePickerDialog =
                        new DatePickerDialog(ActivityCollector.getTopActivity(), null,
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
                                String date = year + "-" + (month + 1) + "-" + day;
                                L.i("设置的日期是：" + date);
                                if (onDateSelectListener != null) {
                                    onDateSelectListener.onDateSelected(date);
                                }
                                datePickerDialog.dismiss();
                            }
                        });

                //取消按钮，如果不需要直接不设置即可
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                datePickerDialog.show();
            }
        });

    }

    /**
     * 设置一个View进行拍照或者相册选择图片的监听器
     *
     * @param view
     * @param photoSelectListener
     */
    public void setPhotoSelectListener(View view, PhotoSelectListener photoSelectListener) {

        mPhotoSelectListener = photoSelectListener;
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items = {mStringPhotograph, mStringSelectFromAlbum};

                new AlertDialog
                        .Builder(ActivityCollector.getTopActivity())
                        .setTitle(R.string.upload_phone)
                        .setNegativeButton(R.string.cancel, null)
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
        });
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
                            T.showLong(ActivityCollector.getTopActivity(),
                                    R.string.sdcard_disabled);
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
        Bitmap photoBitmap = null;
        Uri uri = null;
        if (data != null) {
            uri = data.getData();
        }
        switch (requestCode) {
            case REQUEST_PHOTOGRAPH:
                // 避免有时候获取到的uri是空的情况
                if (uri == null) {
                    Bundle bundle = data.getExtras();
                    if (bundle != null) {
                        try {
                            // 从Bundle中获取图片
                            photoBitmap = (Bitmap) bundle.get("data");

                            File externalFilesDir = getExternalFilesDir(Environment
                                    .DIRECTORY_PICTURES);
                            File file = new File(externalFilesDir, System
                                    .currentTimeMillis() + ".jpg");
                            L.i("文件路径：" + file.getPath());
                            // 打开文件输出流
                            FileOutputStream fileOutputStream = new
                                    FileOutputStream(file);
                            // 生成图片文件
                            photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            // 压缩图片
                            photoBitmap = PictureUtil.getScaleBitmap
                                    (file.getPath(), this);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    } else {
                        T.showLong(ActivityCollector.getTopActivity(),
                                R.string.photograph_failed);
                        return;
                    }
                } else {
                    // 压缩图片
                    photoBitmap = PictureUtil.getScaleBitmap
                            (FileUtil.getRealFilePath(this, uri), this);
                }
                break;
            case REQUEST_SELECT_FROM_ALBUM:
                // 压缩图片
                photoBitmap = PictureUtil.getScaleBitmap
                        (FileUtil.getRealFilePath(this, uri), this);

                break;
            default:
                break;
        }
        if (mPhotoSelectListener != null) {
            mPhotoSelectListener.onPhotoSelected(photoBitmap);
        }
    }
}
