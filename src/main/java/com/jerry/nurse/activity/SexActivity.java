package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jerry.nurse.R;

public class SexActivity extends BaseActivity {

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
}
