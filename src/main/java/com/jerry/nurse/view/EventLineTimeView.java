package com.jerry.nurse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.util.DateUtil;

import java.util.Date;

/**
 * Created by Jerry on 2017/9/3.
 */

public class EventLineTimeView extends RelativeLayout {

    private TextView mNameTextView;
    private TextView mValueTextView;

    private String mName;
    private String mValue;

    public EventLineTimeView(Context context) {
        this(context, null);
    }

    public EventLineTimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventLineTimeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R
                .styleable.EventLineTimeView);

        mName = ta.getString(R.styleable.EventLineTimeView_event_time_name);
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
                R.layout.view_line_time, this);
        mNameTextView = (TextView) view.findViewById(R.id.tv_name);
        mValueTextView = (TextView) view.findViewById(R.id.tv_time);

        mNameTextView.setText(mName);
        mValueTextView.setText(DateUtil.parseDateToStringDetail(new Date()));
    }

    /**
     * 获取当前填写的值
     *
     * @return
     */
    public String getValue() {
        return mValueTextView.getText().toString();
    }

    /**
     * 设置时间值
     *
     * @param value
     */
    public void setValue(String value) {
        mValueTextView.setText(value);
    }
}
