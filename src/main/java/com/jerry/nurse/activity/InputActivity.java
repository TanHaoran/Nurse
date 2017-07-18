package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.view.TitleBar;

import butterknife.Bind;

public class InputActivity extends BaseActivity {

    @Bind(R.id.tb_input)
    TitleBar mTitleBar;

    public static Intent getIntent(Context context, String title) {
        Intent intent = new Intent(context, InputActivity.class);
        intent.putExtra(HtmlActivity.EXTRA_TITLE, title);
        return intent;
    }

    public static Intent getIntent(Context context, int titleRes) {
        String title = context.getResources().getString(titleRes);
        return getIntent(context, title);
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_input;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        String title = getIntent().getStringExtra(HtmlActivity.EXTRA_TITLE);

        mTitleBar.setTitle(title);

        mTitleBar.setRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                finish();
            }
        });
    }
}
