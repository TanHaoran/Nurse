package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jerry.nurse.R;

public class ContactDetailActivity extends BaseActivity {

    public static final String EXTRA_CONTACT_ID = "extra_contact_id";

    public static Intent getIntent(Context context, String contactId) {
        Intent intent = new Intent(context, InputActivity.class);
        intent.putExtra(EXTRA_CONTACT_ID, contactId);
        return intent;
    }


    @Override
    public int getContentViewResId() {
        return R.layout.activity_contact_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        String contactId = getIntent().getStringExtra(EXTRA_CONTACT_ID);
    }
}
