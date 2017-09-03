package com.jerry.nurse.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jerry.nurse.R;

/**
 * Created by Jerry on 2017/9/3.
 */

public class EventLineEditView extends RelativeLayout {

    private TextView mTextView;

    private EditText mEditView;

    private String mName;
    private String mHint;

    public EventLineEditView(Context context) {
        this(context, null);
    }

    public EventLineEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventLineEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray ta = context.obtainStyledAttributes(attrs, R
                .styleable.EventLineEditView);

        mName = ta.getString(R.styleable.EventLineEditView_event_edit_name);
        mHint = ta.getString(R.styleable.EventLineEditView_event_edit_hint);

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
                R.layout.view_line_edit, this);
        mTextView = (TextView) view.findViewById(R.id.tv_name);
        mEditView = (EditText) view.findViewById(R.id.et_value);

        mTextView.setText(mName);
        mEditView.setHint(mHint);
    }

    /**
     * 获取当前填写的值
     *
     * @return
     */
    public String getValue() {
        return mEditView.getText().toString();
    }
}
