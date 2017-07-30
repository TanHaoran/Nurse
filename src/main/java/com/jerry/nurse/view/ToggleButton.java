package com.jerry.nurse.view;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.DensityUtil;

/**
 * Created by Jerry on 2017/7/30.
 */

public class ToggleButton extends RelativeLayout {


    private TextView mMaleTextView;
    private TextView mFemaleTextView;

    private ImageView mFemaleImageView;

    private boolean mIsFemale = true;

    public ToggleButton(Context context) {
        this(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_sax_toggle, this);

        mMaleTextView = (TextView) findViewById(R.id.tv_male);
        mFemaleTextView = (TextView) findViewById(R.id.tv_female);

        mFemaleImageView = (ImageView) findViewById(R.id.iv_female);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                toggle();
        }
        return true;
    }


    /**
     * 获取当前性别，0表示男，1表示女
     *
     * @return
     */
    public int getSex() {
        return mIsFemale ? 1 : 0;
    }

    /**
     * 切换显示状态
     */
    private void toggle() {
        if (mIsFemale) {
            ObjectAnimator animator = ObjectAnimator
                    .ofFloat(mFemaleImageView, "x", 0,
                            DensityUtil.dp2px(ActivityCollector.getTopActivity(), 35))
                    .setDuration(100);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        } else {
            ObjectAnimator animator = ObjectAnimator
                    .ofFloat(mFemaleImageView, "x",
                            DensityUtil.dp2px(ActivityCollector.getTopActivity(), 35), 0)
                    .setDuration(100);
            animator.setInterpolator(new LinearInterpolator());
            animator.start();
        }
        mIsFemale = !mIsFemale;
    }
}
