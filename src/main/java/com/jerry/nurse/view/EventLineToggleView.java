package com.jerry.nurse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerry.nurse.R;

/**
 * Created by Jerry on 2017/9/3.
 */

public class EventLineToggleView extends RelativeLayout {

    private TextView mNameTextView;
    private ToggleButton mToggleButton;

    private String mNameText;
    private String mOpenText;
    private String mCloseText;

    public EventLineToggleView(Context context) {
        this(context, null);
    }

    public EventLineToggleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventLineToggleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R
                .styleable.EventLineToggleView);

        mNameText = ta.getString(R.styleable.EventLineToggleView_toggle_name);
        mOpenText = ta.getString(R.styleable.EventLineToggleView_toggle_open);
        mCloseText = ta.getString(R.styleable
                .EventLineToggleView_toggle_close);
        init(context);
        ta.recycle();
    }

    /**
     * 初始化
     *
     * @param context
     */
    private void init(Context context) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.view_line_toggle, this);
        mNameTextView = (TextView) view.findViewById(R.id.tv_name);
        mToggleButton = (ToggleButton) view.findViewById(R.id.tb_toggle);

        mNameTextView.setText(mNameText);
        mToggleButton.setOpenText(mOpenText);
        mToggleButton.setCloseText(mCloseText);
    }

    /**
     * 获取当前填写的值
     *
     * @return
     */
    public String getValue() {
        return mToggleButton.getValue();
    }

    public boolean isOpen() {
        return mToggleButton.isOpen();
    }
}
