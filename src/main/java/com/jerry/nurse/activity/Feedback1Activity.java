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

public class Feedback1Activity extends BaseActivity {

    @Bind(R.id.tb_feedback)
    TitleBar mTitleBar;

    @Bind(R.id.rg_feedback)
    RadioGroup mRadioGroup;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, Feedback1Activity.class);
        return intent;
    }


    @Override
    public int getContentViewResId() {
        return R.layout.activity_feedback1;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                String choice = "";
                switch (mRadioGroup.getCheckedRadioButtonId()) {
                    case -1:
                        T.showShort(Feedback1Activity.this, "选一个选项嘛!");
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
                    case R.id.rb_d:
                        choice = "D";
                        break;
                    case R.id.rb_e:
                        choice = "E";
                        break;
                }
                Intent intent = Feedback2Activity.getIntent(Feedback1Activity.this, choice);
                startActivity(intent);
            }
        });
    }
}

