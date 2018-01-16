package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;

import com.jerry.nurse.R;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;

import butterknife.Bind;

import static com.jerry.nurse.activity.Feedback3Activity.EXTRA_CONTENT;

public class Feedback2Activity extends BaseActivity {

    @Bind(R.id.tb_feedback)
    TitleBar mTitleBar;

    @Bind(R.id.rg_feedback)
    RadioGroup mRadioGroup;

    private String lastChoice;

    public static Intent getIntent(Context context, String content) {
        Intent intent = new Intent(context, Feedback2Activity.class);
        intent.putExtra(EXTRA_CONTENT, content);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_feedback2;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        lastChoice = getIntent().getStringExtra(EXTRA_CONTENT);
        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                String choice = "";
                switch (mRadioGroup.getCheckedRadioButtonId()) {
                    case -1:
                        T.showShort(Feedback2Activity.this, "选一个选项嘛!");
                        return;
                    case R.id.rb_a:
                        choice = "A";
                        break;
                    case R.id.rb_b:
                        choice = "B";
                        break;
                    case R.id.rb_c:
                        choice = "C";
                        break;
                }
                Intent intent =Feedback3Activity.getIntent(Feedback2Activity.this, lastChoice + choice);
                startActivity(intent);
            }
        });
    }
}

