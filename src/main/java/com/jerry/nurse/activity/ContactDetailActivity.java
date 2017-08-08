package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jerry.nurse.R;

import butterknife.OnClick;

public class ContactDetailActivity extends BaseActivity {

    public static final String EXTRA_CONTACT_ID = "extra_contact_id";

    public static Intent getIntent(Context context, String contactId) {
        Intent intent = new Intent(context, ContactDetailActivity.class);
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

    @OnClick(R.id.acb_call)
    void onCall(View view) {

    }

    @OnClick(R.id.acb_send_message)
    void onSendMessage() {
        Intent intent = ChatActivity.getIntent(this);
        startActivity(intent);
    }
}
