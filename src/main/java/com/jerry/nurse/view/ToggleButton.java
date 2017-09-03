package com.jerry.nurse.view;

import android.content.Context;
import android.content.res.TypedArray;
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


    private TextView mLeftTextView;
    private TextView mRightTextView;

    private ImageView mLeftImageView;
    private ImageView mRightImageView;

    private boolean mOpen = false;

    public interface OnToggleListener {
        void onToggle(boolean open);
    }

    private OnToggleListener mOnToggleListener;


    public void setOnToggleListener(OnToggleListener onToggleListener) {
        mOnToggleListener = onToggleListener;
    }

    public ToggleButton(Context context) {
        this(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ToggleButton);
        String leftText = ta.getString(R.styleable.ToggleButton_toggle_left_text);
        String rightText = ta.getString(R.styleable.ToggleButton_toggle_right_text);


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_toggle_button, this);

        mLeftTextView = (TextView) findViewById(R.id.tv_left);
        mRightTextView = (TextView) findViewById(R.id.tv_right);

        mLeftImageView = (ImageView) findViewById(R.id.iv_left);
        mRightImageView = (ImageView) findViewById(R.id.iv_right);

        mLeftTextView.setText(leftText);
        mRightTextView.setText(rightText);

        ta.recycle();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                toggle();
                if (mOnToggleListener != null) {
                    mOnToggleListener.onToggle(mOpen);
                }
        }
        return true;
    }


    /**
     * 获取当前性别，0表示关闭，1表示打开
     *
     * @return
     */
    public int getOpen() {
        return mOpen ? 1 : 0;
    }

    public boolean isOpen() {
        return mOpen;
    }

    /**
     * 切换显示状态
     */
    private void toggle() {
        if (mOpen) {
            mLeftImageView.setVisibility(INVISIBLE);
            mRightImageView.setVisibility(VISIBLE);

            mLeftTextView.setTextColor(getResources().getColor(R.color.gray_textColor));
            mRightTextView.setTextColor(getResources().getColor(R.color.white));
        } else {
            mLeftImageView.setVisibility(VISIBLE);
            mRightImageView.setVisibility(INVISIBLE);

            mLeftTextView.setTextColor(getResources().getColor(R.color.white));
            mRightTextView.setTextColor(getResources().getColor(R.color.gray_textColor));
        }
        mOpen = !mOpen;
    }

    /**
     * 设置按钮的开关状态
     *
     * @param open
     */
    public void setOpen(boolean open) {
        mOpen = open;
        if (open) {
            mLeftImageView.setVisibility(VISIBLE);
            mRightImageView.setVisibility(INVISIBLE);

            mLeftTextView.setTextColor(getResources().getColor(R.color.white));
            mRightTextView.setTextColor(getResources().getColor(R.color.gray_textColor));
        } else {
            mLeftImageView.setVisibility(INVISIBLE);
            mRightImageView.setVisibility(VISIBLE);

            mLeftTextView.setTextColor(getResources().getColor(R.color.gray_textColor));
            mRightTextView.setTextColor(getResources().getColor(R.color.white));
        }
    }

    /**
     * 设置左侧(开状态)文字
     *
     * @param open
     */
    public void setOpenText(String open) {
        mLeftTextView.setText(open);
    }


    /**
     * 设置右侧(开状态)文字
     *
     * @param close
     */
    public void setCloseText(String close) {
        mRightTextView.setText(close);
    }

    /**
     * 获取当前状态值
     */
    public String getValue() {
        if (mOpen) {
            return mLeftTextView.getText().toString();
        } else {
            return mRightTextView.getText().toString();
        }
    }

}
