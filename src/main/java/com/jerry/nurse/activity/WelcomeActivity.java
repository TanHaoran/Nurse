package com.jerry.nurse.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.jerry.nurse.R;

public class WelcomeActivity extends BaseActivity {

    /**
     * 首页停留时间
     */
    private static final int STAY_DURATION = 5000;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(this.getResources().getColor(R.color.primary));
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(STAY_DURATION);
                    Intent intent = IndicatorActivity.getIntent(WelcomeActivity.this);
                    startActivity(intent);
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
