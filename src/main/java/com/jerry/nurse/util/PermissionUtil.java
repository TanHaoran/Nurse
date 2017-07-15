package com.jerry.nurse.util;

import android.Manifest;

import com.jerry.nurse.activity.BaseActivity;

import java.util.List;

/**
 * Created by Jerry on 2016/12/29.
 */

public class PermissionUtil {

    /**
     * 测试在非Activity类中申请权限
     */
    public void test() {
        BaseActivity.requestRuntimePermission(new String[]{Manifest.permission.CALL_PHONE}, new PermissionListener() {

            @Override
            public void onGranted() {

            }

            @Override
            public void onDenied(List<String> deniedPermission) {

            }
        });
    }
}
