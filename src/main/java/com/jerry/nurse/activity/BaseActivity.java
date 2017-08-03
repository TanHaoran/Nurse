package com.jerry.nurse.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import com.jerry.nurse.listener.OnPhotoSelectListener;
import com.jerry.nurse.listener.PermissionListener;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.DateUtil;
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

import butterknife.ButterKnife;

/**
 * Created by Jerry on 2017/7/15.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_RESULT = 0x00001001;

    public static final int REQUEST_PHOTOGRAPH_1 = 0x00001002;
    public static final int REQUEST_SELECT_FROM_ALBUM_1 = 0x00001003;
    public static final int REQUEST_PHOTOGRAPH_2 = 0x00001004;
    public static final int REQUEST_SELECT_FROM_ALBUM_2 = 0x00001005;
    public static final int REQUEST_PHOTOGRAPH_3 = 0x00001006;
    public static final int REQUEST_SELECT_FROM_ALBUM_3 = 0x00001007;
    public static final int REQUEST_PHOTOGRAPH_4 = 0x00001008;
    public static final int REQUEST_SELECT_FROM_ALBUM_4 = 0x00001009;

    private static final int[] REQUEST_PHOTOGRAPHS = {REQUEST_PHOTOGRAPH_1, REQUEST_PHOTOGRAPH_2,
            REQUEST_PHOTOGRAPH_3, REQUEST_PHOTOGRAPH_4};

    private static final int[] REQUEST_SELECT_FROM_ALBUM = {REQUEST_SELECT_FROM_ALBUM_1,
            REQUEST_SELECT_FROM_ALBUM_2, REQUEST_SELECT_FROM_ALBUM_3,
            REQUEST_SELECT_FROM_ALBUM_4};

    private static PermissionListener sPermissionListener;

    private List<OnPhotoSelectListener> mOnPhotoSelectListeners;


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

        mOnPhotoSelectListeners = new ArrayList<>();

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
        sPermissionListener = listener;
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
            sPermissionListener.onGranted();
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
                        sPermissionListener.onGranted();
                    } else {
                        sPermissionListener.onDenied(deniedPermissions);
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
    public void setDateSelectListener(View view, final Date origin, final boolean isBirthday, final OnDateSelectListener onDateSelectListener) {

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int nowYear = calendar.get(Calendar.YEAR);
                //  首先如果原日期不为空就先设置成原日期
                if (origin != null) {
                    calendar.setTime(origin);
                }
                // 然后判断是否需要往前退20年
                if (nowYear - calendar.get(Calendar.YEAR) > 20) {

                    if (isBirthday) {
                        calendar.set(Calendar.YEAR, nowYear - 20);
                    }
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
                                Date nowDate = new Date();
                                if (DateUtil.parseStringToDate(date).getTime() > nowDate.getTime()) {
                                    T.showShort(ActivityCollector.getTopActivity(), "日期设置不能大于当天");
                                    return;
                                }
                                if (onDateSelectListener != null) {
                                    onDateSelectListener.onDateSelected(DateUtil.parseStringToDate(date));
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
     * @param onPhotoSelectListener
     */
    public void setPhotoSelectListener(View view, final int index, OnPhotoSelectListener onPhotoSelectListener) {

        mOnPhotoSelectListeners.add(index, onPhotoSelectListener);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] items = {"拍照", "从相册选择"};

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
                                        photograph(index);
                                        break;
                                    // 从相册选择
                                    case 1:
                                        selectFromAlbum(index);
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
     *
     * @param index
     */
    private void photograph(final int index) {
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
                            startActivityForResult(intent, REQUEST_PHOTOGRAPHS[index]);
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
     *
     * @param index
     */
    private void selectFromAlbum(final int index) {
        BaseActivity.requestRuntimePermission(new String[]{
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                new PermissionListener() {
                    @Override
                    public void onGranted() {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");//相片类型
                        startActivityForResult(intent, REQUEST_SELECT_FROM_ALBUM[index]);
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
        if (requestCode == REQUEST_PHOTOGRAPH_1) {
            dealPhotograph(data, 0);
        } else if (requestCode == REQUEST_SELECT_FROM_ALBUM_1) {
            dealAlbum(data, 0);
        } else if (requestCode == REQUEST_PHOTOGRAPH_2) {
            dealPhotograph(data, 1);
        } else if (requestCode == REQUEST_SELECT_FROM_ALBUM_2) {
            dealAlbum(data, 1);
        } else if (requestCode == REQUEST_PHOTOGRAPH_3) {
            dealPhotograph(data, 2);
        } else if (requestCode == REQUEST_SELECT_FROM_ALBUM_3) {
            dealAlbum(data, 2);
        } else if (requestCode == REQUEST_PHOTOGRAPH_4) {
            dealPhotograph(data, 3);
        } else if (requestCode == REQUEST_SELECT_FROM_ALBUM_4) {
            dealAlbum(data, 3);
        }
    }

    /**
     * 处理相册选择的图片
     *
     * @param data
     * @param index
     */
    private void dealAlbum(Intent data, int index) {
        Bitmap photoBitmap;
        File file;
        Uri uri = null;
        if (data != null) {
            uri = data.getData();
        }
        String realFilePath = FileUtil.getRealFilePath(this, uri);
        // 压缩图片
        photoBitmap = PictureUtil.getScaleBitmap
                (realFilePath, this);
        file = new File(realFilePath);
        if (mOnPhotoSelectListeners.size() >= index + 1) {
            mOnPhotoSelectListeners.get(index).onPhotoSelected(photoBitmap, file);
        }
    }

    /**
     * 处理拍照的图片
     *
     * @param data
     * @param index
     */
    private void dealPhotograph(Intent data, int index) {
        Bitmap photoBitmap = null;
        File file = null;
        Uri uri = null;
        if (data != null) {
            uri = data.getData();
        }
        // 避免有时候获取到的uri是空的情况
        if (uri == null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                try {
                    // 从Bundle中获取图片
                    photoBitmap = (Bitmap) bundle.get("data");

                    File externalFilesDir = getExternalFilesDir(Environment
                            .DIRECTORY_PICTURES);
                    file = new File(externalFilesDir, System
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
            String realFilePath = FileUtil.getRealFilePath(this, uri);
            // 压缩图片
            photoBitmap = PictureUtil.getScaleBitmap
                    (realFilePath, this);
            file = new File(realFilePath);
        }
        if (mOnPhotoSelectListeners.size() >= index + 1) {
            mOnPhotoSelectListeners.get(index).onPhotoSelected(photoBitmap, file);
        }
    }
}
