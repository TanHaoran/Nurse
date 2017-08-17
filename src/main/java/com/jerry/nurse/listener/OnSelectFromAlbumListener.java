package com.jerry.nurse.listener;

import android.graphics.Bitmap;

import java.io.File;

/**
 * Created by Jerry on 2017/7/18.
 * 选择完照片后的接口
 */
public interface OnSelectFromAlbumListener {

    /**
     * 选择完照片后
     *
     * @param bitmap
     */
    void onPhotoFinished(Bitmap bitmap, File file);
}
