package com.jerry.nurse.listener;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Jerry on 2017/7/18.
 * 拍完照片后回调接口
 */
public interface OnPhotographFinishListener {

    /**
     * 拍完照片后
     *
     * @param bitmap
     */
    void onPhotographFinished(Bitmap bitmap, File file);
}
