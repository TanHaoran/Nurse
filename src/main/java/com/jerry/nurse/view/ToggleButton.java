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


    private TextView mOpenTextView;
    private TextView mCloseTextView;

    private ImageView mOpenImageView;
    private ImageView mCloseImageView;

    private boolean mIsMale = false;

    public ToggleButton(Context context) {
        this(context, null);
    }

    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ToggleButton);
        String openText = ta.getString(R.styleable.ToggleButton_open_text);
        String closeText = ta.getString(R.styleable.ToggleButton_close_text);


        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_sax_toggle, this);

        mOpenTextView = (TextView) findViewById(R.id.tv_right);
        mCloseTextView = (TextView) findViewById(R.id.tv_left);

        mOpenImageView = (ImageView) findViewById(R.id.iv_left);
        mCloseImageView = (ImageView) findViewById(R.id.iv_right);

        mOpenTextView.setText(openText);
        mCloseTextView.setText(closeText);

        ta.recycle();
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
     * 获取当前性别，0表示关闭，1表示打开
     *
     * @return
     */
    public int getOpen() {
        return mIsMale ? 0 : 1;
    }

    /**
     * 切换显示状态
     */
    private void toggle() {
        if (mIsMale) {
            mCloseImageView.setVisibility(VISIBLE);
            mCloseTextView.setTextColor(getResources().getColor(R.color.white));

            mOpenTextView.setTextColor(getResources().getColor(R.color.gray_textColor));
            mOpenImageView.setVisibility(INVISIBLE);
        } else {
            mCloseImageView.setVisibility(INVISIBLE);
            mCloseTextView.setTextColor(getResources().getColor(R.color.gray_textColor));

            mOpenTextView.setTextColor(getResources().getColor(R.color.white));
            mOpenImageView.setVisibility(VISIBLE);
        }
        mIsMale = !mIsMale;
    }
}
