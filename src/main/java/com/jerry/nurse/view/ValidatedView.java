package com.jerry.nurse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.util.DensityUtil;


/**
 * Created by Jerry on 2017/7/20.
 */

public class ValidatedView extends LinearLayout {

    private static final int DEFAULT_TEXT_SIZE = 10;

    private static final int DEFAULT_BACKGROUND_COLOR = 0xFFF7B55F;

    private TextView mTextView;
    private ImageView mImageView;

    private String mText;
    private int mBackgroundColor;

    public ValidatedView(Context context) {
        this(context, null);
    }

    public ValidatedView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ValidatedView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ValidatedView);

        mText = ta.getString(R.styleable.ValidatedView_text);
        mBackgroundColor = ta.getColor(R.styleable.ValidatedView_validated_bg_color, DEFAULT_BACKGROUND_COLOR);

        initView(context);

        ta.recycle();
    }

    private void initView(Context context) {
        mTextView = new TextView(context);
        mImageView = new ImageView(context);

        mTextView.setText(mText);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, DEFAULT_TEXT_SIZE);
        mTextView.setTextColor(Color.WHITE);

        mImageView.setImageResource(R.drawable.certification);

        LayoutParams textLayoutParam = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayoutParam.setMargins(DensityUtil.dp2px(context, 4), 0, 0, 0);
        LayoutParams imageLayoutParam = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        mTextView.setLayoutParams(textLayoutParam);
        mImageView.setLayoutParams(imageLayoutParam);

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundResource(R.drawable.certification_bg);

        addView(mImageView);
        addView(mTextView);

        setPadding(DensityUtil.dp2px(context, 8),0, DensityUtil.dp2px(context, 8), 0);
    }
}
