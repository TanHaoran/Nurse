package com.jerry.nurse.listener;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Jerry on 2017/7/18.
 * 选择完照片后的接口
 */
public interface OnPhotoSelectListener {

    /**
     * 选择完照片后
     *
     * @param bitmap
     */
    void onPhotoSelected(Bitmap bitmap, File file);
}
