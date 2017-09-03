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
import com.jerry.nurse.util.BottomDialogManager;

import java.util.List;

/**
 * Created by Jerry on 2017/9/3.
 */

public class EventLineSelectView extends RelativeLayout {

    private static final String OTHER = "其它";

    private TextView mNameTextView;

    private EditText mNumberEditText;
    private TextView mValueTextView;

    private EditText mOtherEditText;

    private boolean mShowEdit;
    private String mName;
    private String mHint;
    private String mDefaultValue;

    private List<String> mItems;

    private Context mContext;

    public EventLineSelectView(Context context) {
        this(context, null);
    }

    public EventLineSelectView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EventLineSelectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        TypedArray ta = context.obtainStyledAttributes(attrs, R
                .styleable.EventLineSelectView);

        mShowEdit = ta.getBoolean(R.styleable
                .EventLineSelectView_select_show_edit, false);
        mName = ta.getString(R.styleable.EventLineSelectView_select_name);
        mHint = ta.getString(R.styleable.EventLineSelectView_select_hint);
        mDefaultValue = ta.getString(R.styleable.EventLineSelectView_select_default);

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
                R.layout.view_line_select, this);
        mNameTextView = (TextView) view.findViewById(R.id.tv_name);
        mValueTextView = (TextView) view.findViewById(R.id.tv_value);
        mNumberEditText = (EditText) view.findViewById(R.id.tv_number);
        mOtherEditText = (EditText) view.findViewById(R.id.et_other);

        mNameTextView.setText(mName);
        mValueTextView.setText(mDefaultValue);
        mNumberEditText.setHint(mHint);
        if (mShowEdit) {
            mNumberEditText.setVisibility(VISIBLE);
        } else {
            mNumberEditText.setVisibility(GONE);
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mNumberEditText.getVisibility() == GONE) {
                    showDialog();
                }
            }
        });

        mValueTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void showDialog() {
        BottomDialogManager manager = new BottomDialogManager
                (mContext);
        manager.setOnItemSelectedListener(mItems, new BottomDialogManager.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position, String item) {
                mValueTextView.setText(item);
                if (item.equals(OTHER)) {
                    mOtherEditText.setVisibility(VISIBLE);
                } else {
                    mOtherEditText.setVisibility(GONE);
                }
            }
        });
        manager.showSelectDialog();
    }

    /**
     * 设置候选项
     *
     * @param items
     */
    public void setItems(List<String> items) {
        mItems = items;
    }
}
