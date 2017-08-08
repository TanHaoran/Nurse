package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jerry.nurse.R;

import butterknife.OnClick;

public class AddContactActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, AddContactActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_add_contact;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @OnClick(R.id.ll_scan)
    void onScan(View view) {

    }

    @OnClick(R.id.ll_cellphone_contact)
    void onCellphoneContact(View view) {
        Intent intent = CellphoneContactActivity.getIntent(this);
        startActivity(intent);
    }

}
