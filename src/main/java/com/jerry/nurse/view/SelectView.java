package com.jerry.nurse.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jerry.nurse.R;


/**
 * Created by Jerry on 2017/8/12.
 */

public class SelectView extends android.support.v7.widget.AppCompatImageView {

    private static final int DEFAULT_PADDING = 9;

    private boolean mIsSelected;

    public SelectView(Context context) {
        this(context, null);
    }

    public SelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setBackgroundResource(R.drawable.select_view_normal);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });

        setPadding(DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING, DEFAULT_PADDING);
    }

    @Override
    public boolean isSelected() {
        return mIsSelected;
    }

    public void toggle() {
        if (!mIsSelected) {
            setBackgroundResource(R.drawable.select_view_selected);
            setImageResource(R.drawable.check_white);
        } else {
            setBackgroundResource(R.drawable.select_view_normal);
        }
        mIsSelected = !mIsSelected;
    }

    public void setSelected(boolean select) {
        if (select) {
            setBackgroundResource(R.drawable.select_view_selected);
        } else {
            setBackgroundResource(R.drawable.select_view_normal);
        }
    }
}
