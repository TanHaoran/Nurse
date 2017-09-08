package com.jerry.nurse.transform;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Jerry on 2016/4/15.
 */
public class RotatePageTransformer implements ViewPager.PageTransformer {


    /**
     * 页面切换时旋转的角度
     */
    private static final float ROTATE_VALUE = 20;

    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        int pageHeight = view.getHeight();

        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);

        } else if (position <= 0) { // [-1,0]
            // 从0度到-20度
            view.setPivotX(pageWidth / 2);
            view.setPivotY(pageHeight);
            float rotation = position * ROTATE_VALUE;
            view.setRotation(rotation);
        } else if (position <= 1) { // (0,1]
            // 从20度到0度
            view.setPivotX(pageWidth / 2);
            view.setPivotY(pageHeight);
            float rotation = position * ROTATE_VALUE;
            view.setRotation(rotation);
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }
}
