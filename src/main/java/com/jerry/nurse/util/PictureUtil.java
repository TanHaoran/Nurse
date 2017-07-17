package com.jerry.nurse.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

public class PictureUtil {

    /**
     * 根据目标区域获取合适大小的Bitmap
     *
     * @param path
     * @param destWidth
     * @param destHeight
     * @return
     */
    public static Bitmap getScaleBitmap(String path, int destWidth,
                                        int destHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        // 设置true表示只是解析bitmap，并不加载到内存
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // 获取图片的原始尺寸
        int srcWidth = options.outWidth;
        int srcHeight = options.outHeight;

        // 进行比较后缩放
        int inSampleSize = 1;
        if (srcWidth > destWidth || srcHeight > destHeight) {
            // 如果原始宽大于原始高
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(destHeight / srcHeight);
            }
        }

        // 重新设置解析图片的参数
        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(path, options);
    }

    /**
     * 根据屏幕大小缩放至合适的Bitmap
     *
     * @param path
     * @param activity
     * @return
     */
    public static Bitmap getScaleBitmap(String path, Activity activity) {
        Point point = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(point);

        return getScaleBitmap(path, point.x, point.y);
    }
}