package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.jerry.nurse.R;

import butterknife.Bind;
import butterknife.OnClick;

public class SexActivity extends BaseActivity {

    @Bind(R.id.iv_male)
    ImageView mMaleChooseImageView;

    @Bind(R.id.iv_female)
    ImageView mFemaleChooseImageView;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, SexActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_sex;
    }

    @Override
    public void init(Bundle savedInstanceState) {
    }

    @OnClick(R.id.ll_male)
    void onMaleChoose(View view) {
        mMaleChooseImageView.setVisibility(View.VISIBLE);
        mFemaleChooseImageView.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.ll_female)
    void onFemaleChoose(View view) {
        mMaleChooseImageView.setVisibility(View.INVISIBLE);
        mFemaleChooseImageView.setVisibility(View.VISIBLE);
    }
}
