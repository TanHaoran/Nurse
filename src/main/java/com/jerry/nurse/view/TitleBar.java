package com.jerry.nurse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.DensityUtil;

/**
 * Created by Buzzly on 2016/4/24.
 */
public class TitleBar extends RelativeLayout {

    private static final int DEFAULT_BUTTON_WIDTH = 50;
    private static final int DEFAULT_BACK_ARROW_WIDTH = 40;

    private static final int DEFAULT_BUTTON_TEXT_SIZE = 15;
    private static final int DEFAULT_TITLE_TEXT_SIZE = 18;

    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_BG_COLOR = 0xFF48A2F8;

    private ImageView mBackImageView;
    private TextView mLeftTextView;
    private TextView mRightTextView;
    private TextView mTitleTextView;

    private boolean mShowBack;

    private String mLeftText;
    private String mRightText;
    private String mTitleText;

    private float mButtonTextSize;
    private float mTitleTextSize;

    private int mTextColor;
    private int mBgColor;

    private OnLeftClickListener mOnLeftClickListener;
    private OnRightClickListener mOnRightClickListener;

    public interface OnLeftClickListener {
        void onLeftClick(View view);
    }

    public interface OnRightClickListener {
        void onRightClick(View view);
    }

    public TitleBar(Context context) {
        this(context, null);
    }

    public TitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);

        mShowBack = ta.getBoolean(R.styleable.TitleBar_show_back, false);

        mLeftText = ta.getString(R.styleable.TitleBar_left_text);
        mRightText = ta.getString(R.styleable.TitleBar_right_text);
        mTitleText = ta.getString(R.styleable.TitleBar_title_text);

        mButtonTextSize = ta.getDimension(R.styleable
                .TitleBar_button_textSize, DensityUtil.sp2px(context,
                DEFAULT_BUTTON_TEXT_SIZE));
        mTitleTextSize = ta.getDimension(R.styleable
                .TitleBar_title_textSize, DensityUtil.sp2px(context,
                DEFAULT_TITLE_TEXT_SIZE));

        mTextColor = ta.getColor(R.styleable.TitleBar_text_color,
                DEFAULT_TEXT_COLOR);
        mBgColor = ta.getColor(R.styleable.TitleBar_bg_color,
                DEFAULT_BG_COLOR);

        // 初始化界面
        initView(context);

        ta.recycle();
    }

    /**
     * 初始化界面
     *
     * @param context
     */
    private void initView(Context context) {
        // 创建View
        mBackImageView = new ImageView(context);
        mLeftTextView = new TextView(context);
        mRightTextView = new TextView(context);
        mTitleTextView = new TextView(context);

        // 设置文字内容
        mBackImageView.setImageResource(R.drawable.arrow_back);
        mLeftTextView.setText(mLeftText);
        mRightTextView.setText(mRightText);
        mTitleTextView.setText(mTitleText);

        // 设置文字大小
        mLeftTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mButtonTextSize);
        mRightTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mButtonTextSize);
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                mTitleTextSize);

        // 设置标题的几种格式
        mTitleTextView.setMaxWidth(DensityUtil.dp2px(context, 200));
        mTitleTextView.setSingleLine(true);
        mTitleTextView.setEllipsize(TextUtils.TruncateAt.END);

        // 设置文字颜色
        mLeftTextView.setTextColor(mTextColor);
        mRightTextView.setTextColor(mTextColor);
        mTitleTextView.setTextColor(mTextColor);
        setBackgroundColor(mBgColor);

        // 设置文字位置
        mLeftTextView.setGravity(Gravity.CENTER);
        mRightTextView.setGravity(Gravity.CENTER);

        // 设置布局摆放方式
        LayoutParams backLayoutParam = new LayoutParams(
                DensityUtil.dp2px(context, DEFAULT_BACK_ARROW_WIDTH),
                ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams leftTextLayoutParam = new LayoutParams(
                DensityUtil.dp2px(context, DEFAULT_BUTTON_WIDTH),
                ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams rightTextLayoutParam = new LayoutParams(
                DensityUtil.dp2px(context, DEFAULT_BUTTON_WIDTH),
                ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams titleTextLayoutParam = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        backLayoutParam.addRule(ALIGN_PARENT_LEFT, TRUE);

        mBackImageView.setPadding(DensityUtil.dp2px(context, 1),
                DensityUtil.dp2px(context, 13), DensityUtil.dp2px(context, 4),
                DensityUtil.dp2px(context, 13));

        leftTextLayoutParam.addRule(ALIGN_PARENT_LEFT, TRUE);
        rightTextLayoutParam.addRule(ALIGN_PARENT_RIGHT, TRUE);
        titleTextLayoutParam.addRule(CENTER_IN_PARENT, TRUE);

        // 添加布局
        addView(mBackImageView, backLayoutParam);
        // addView(mLeftTextView, leftTextLayoutParam);
        addView(mRightTextView, rightTextLayoutParam);
        addView(mTitleTextView, titleTextLayoutParam);

        if (mShowBack) {
            mBackImageView.setVisibility(VISIBLE);
        } else {
            mBackImageView.setVisibility(INVISIBLE);
        }

        mBackImageView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnLeftClickListener != null) {
                    mOnLeftClickListener.onLeftClick(v);
                } else {
                    ActivityCollector.getTopActivity().finish();
                }
            }
        });

        mRightTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mRightTextView.getText().toString())) {
                    return;
                }
                if (mOnRightClickListener != null) {
                    mOnRightClickListener.onRightClick(v);
                }
            }
        });
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public TitleBar setTitle(String title) {
        mTitleText = title;
        mTitleTextView.setText(mTitleText);
        return this;
    }

    /**
     * 是否显示返回按钮
     *
     * @param show
     */
    public void showBack(boolean show) {
        if (show) {
            mBackImageView.setVisibility(VISIBLE);
        } else {
            mBackImageView.setVisibility(INVISIBLE);
        }
    }

    /**
     * 设置右侧按钮文字
     *
     * @param text
     * @return
     */
    public TitleBar setRightText(String text) {
        mRightTextView.setText(text);
        mRightTextView.setVisibility(VISIBLE);
        return this;
    }

    /**
     * 设置左侧按钮的监听事件
     *
     * @param onLeftClickListener
     */
    public TitleBar setOnLeftClickListener(OnLeftClickListener onLeftClickListener) {
        mOnLeftClickListener = onLeftClickListener;
        return this;
    }

    /**
     * 设置右侧按钮的监听事件
     *
     * @param onRightClickListener
     */
    public TitleBar setOnRightClickListener(OnRightClickListener onRightClickListener) {
        mOnRightClickListener = onRightClickListener;
        return this;
    }

    /**
     * 设置右边按钮是否可见
     *
     * @param visible
     */
    public TitleBar setRightVisible(int visible) {
        mRightTextView.setVisibility(visible);
        return this;
    }
}