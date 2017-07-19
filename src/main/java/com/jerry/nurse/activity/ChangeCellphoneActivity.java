package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jerry.nurse.R;

import butterknife.OnClick;

public class ChangeCellphoneActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ChangeCellphoneActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_change_cellphone;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @OnClick(R.id.btn_next)
    void onNext(View view) {
        Intent intent = NewCellphoneActivity.getIntent(this);
        startActivity(intent);
    }
}
