package com.jerry.nurse.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Jerry on 2016/4/26.
 */
public class MyViewPager extends ViewPager {

    private View mLeft;
    private View mRight;
    private Map<Integer, View> mViewMap = new HashMap<>();

    // 缩放的最小值
    private static final float MIN_SCALE = 0.7f;
    // 透明的最小值
    private static final float MIN_ALPHA = 0.5f;

    public MyViewPager(Context context) {
        this(context, null);
    }

    public MyViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        mLeft = mViewMap.get(position);
        mRight = mViewMap.get(position + 1);
        addAnimation(mLeft, mRight, offset, offsetPixels);

        super.onPageScrolled(position, offset, offsetPixels);
    }

    /**
     * 给左右两个View添加动画
     *
     * @param left
     * @param right
     * @param offset
     * @param offsetPixels
     */
    private void addAnimation(View left, View right, float offset, int offsetPixels) {
        if (right != null) {
            float scale = (1 - MIN_SCALE) * offset + MIN_SCALE;
            float alpha = (1 - MIN_ALPHA) * offset + MIN_ALPHA;
            right.setScaleX(scale);
            right.setScaleY(scale);
            right.setAlpha(alpha);
            right.setTranslationX(-getWidth() + offsetPixels);
        }
        if (left != null) {
            left.bringToFront();
        }
    }

    /**
     * 根据位置将view添加进map中
     *
     * @param position
     * @param view
     */
    public void addViewByPosition(Integer position, View view) {
        mViewMap.put(position, view);
    }


    /**
     * 根据位置从map中移除view
     *
     * @param position
     */
    public void removeViewByPosition(Integer position) {
        mViewMap.remove(position);
    }
}
