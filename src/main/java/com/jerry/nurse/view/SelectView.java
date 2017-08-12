package com.jerry.nurse.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import com.jerry.nurse.R;


/**
 * Created by Jerry on 2017/8/12.
 */

public class SelectView extends android.support.v7.widget.AppCompatImageView {

    private boolean mIsSelected;

    public SelectView(Context context) {
        this(context, null);
    }

    public SelectView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        setBackgroundResource(R.drawable.select_view_normal);


        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean isSelected() {
        return mIsSelected;
    }
}
