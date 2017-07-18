package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.jerry.nurse.R;

import butterknife.Bind;

public class ChangePasswordActivity extends BaseActivity {

    @Bind(R.id.et_origin_password)
    EditText mOriginPasswordEditText;

    @Bind(R.id.et_new_password)
    EditText mNewPasswordEditText;


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ChangePasswordActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_change_password;
    }

    @Override
    public void init(Bundle savedInstanceState) {
    }
}
