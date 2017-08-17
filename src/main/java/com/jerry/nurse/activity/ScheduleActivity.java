package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jerry.nurse.R;

public class ScheduleActivity extends BaseActivity {


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ScheduleActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_schedule;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }
}
