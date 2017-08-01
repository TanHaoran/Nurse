package com.jerry.nurse.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerry.nurse.R;

/**
 * Created by Jerry on 2017/7/30.
 */

public class ToggleButton extends RelativeLayout {


    private TextView mMaleTextView;
    private TextView mFemaleTextView;

    private ImageView mMaleImageView;
    private ImageView mFemaleImageView;

    private boolean mIsMale = false;

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

        mMaleTextView = (TextView) findViewById(R.id.tv_left);
        mFemaleTextView = (TextView) findViewById(R.id.tv_right);

        mMaleImageView = (ImageView) findViewById(R.id.iv_left);
        mFemaleImageView = (ImageView) findViewById(R.id.iv_right);
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
        return mIsMale ? 0 : 1;
    }

    /**
     * 切换显示状态
     */
    private void toggle() {
        if (mIsMale) {
            mFemaleImageView.setVisibility(VISIBLE);
            mFemaleTextView.setTextColor(getResources().getColor(R.color.white));

            mMaleTextView.setTextColor(getResources().getColor(R.color.gray_textColor));
            mMaleImageView.setVisibility(INVISIBLE);
        } else {
            mFemaleImageView.setVisibility(INVISIBLE);
            mFemaleTextView.setTextColor(getResources().getColor(R.color.gray_textColor));

            mMaleTextView.setTextColor(getResources().getColor(R.color.white));
            mMaleImageView.setVisibility(VISIBLE);
        }
        mIsMale = !mIsMale;
    }
}
