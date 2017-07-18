package com.jerry.nurse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
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

    private static final int DEFAULT_BUTTON_TEXT_SIZE = 14;
    private static final int DEFAULT_TITLE_TEXT_SIZE = 20;

    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_BG_COLOR = 0xFF488AFF;

    private Context mContext;

    private TextView mLeftTextView;
    private TextView mRightTextView;
    private TextView mTitleTextView;

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

        mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);

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
        mLeftTextView = new TextView(context);
        mRightTextView = new TextView(context);
        mTitleTextView = new TextView(context);

        // 设置文字内容
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

        // 设置文字颜色
        mLeftTextView.setTextColor(mTextColor);
        mRightTextView.setTextColor(mTextColor);
        mTitleTextView.setTextColor(mTextColor);
        setBackgroundColor(mBgColor);

        // 设置文字位置
        mLeftTextView.setGravity(Gravity.CENTER);
        mRightTextView.setGravity(Gravity.CENTER);

        // 设置布局摆放方式
        LayoutParams leftTextLayoutParam = new LayoutParams(
                DensityUtil.dp2px(context, DEFAULT_BUTTON_WIDTH),
                ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams rightTextLayoutParam = new LayoutParams(
                DensityUtil.dp2px(context, DEFAULT_BUTTON_WIDTH),
                ViewGroup.LayoutParams.MATCH_PARENT);
        LayoutParams titleTextLayoutParam = new LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        leftTextLayoutParam.addRule(ALIGN_PARENT_LEFT, TRUE);
        rightTextLayoutParam.addRule(ALIGN_PARENT_RIGHT, TRUE);
        titleTextLayoutParam.addRule(CENTER_IN_PARENT, TRUE);

        // 添加布局
        addView(mLeftTextView, leftTextLayoutParam);
        addView(mRightTextView, rightTextLayoutParam);
        addView(mTitleTextView, titleTextLayoutParam);

        mLeftTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLeftText.isEmpty()) {
                    if (mOnLeftClickListener != null) {
                        mOnLeftClickListener.onLeftClick(v);
                    } else {
                        ActivityCollector.getTopActivity().finish();
                    }
                }
            }
        });

        mRightTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnRightClickListener != null) {
                    mOnRightClickListener.onRightClick(v);
                }
            }
        });
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title) {
        mTitleText = title;
        mTitleTextView.setText(mTitleText);
    }

    /**
     * 设置左侧按钮的监听事件
     *
     * @param onLeftClickListener
     */
    public void setLeftClickListener(OnLeftClickListener onLeftClickListener) {
        mOnLeftClickListener = onLeftClickListener;
    }

    /**
     * 设置右侧按钮的监听事件
     *
     * @param onRightClickListener
     */
    public void setRightClickListener(OnRightClickListener onRightClickListener) {
        mOnRightClickListener = onRightClickListener;
    }
}