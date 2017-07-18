package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jerry.nurse.R;

public class ChatActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ChatActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_chat;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }
}
