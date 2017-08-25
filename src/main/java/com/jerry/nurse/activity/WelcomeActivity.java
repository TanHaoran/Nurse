package com.jerry.nurse.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.jerry.nurse.R;

import butterknife.Bind;

public class WelcomeActivity extends BaseActivity {

    @Bind(R.id.iv_bg)
    ImageView mBgImageView;

    @Bind(R.id.iv_logo_wing)
    ImageView mWingImageView;

    @Bind(R.id.iv_logo_text)
    ImageView mTextImageView;

    @Override
    public int getContentViewResId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        int wingStartX = 0;
//        int wingEndX = ScreenUtil.getScreenWidth(this) / 2 - 150;
//        ObjectAnimator wingAnimator = ObjectAnimator.ofFloat(mWingImageView, "x", wingStartX, wingEndX).setDuration(300);
//        wingAnimator.setInterpolator(new LinearInterpolator());
//        wingAnimator.start();

//        int textStartX = ScreenUtil.getScreenWidth(this);
//        int textEndX = ScreenUtil.getScreenWidth(this) / 2;
//        final ObjectAnimator textAnimator = ObjectAnimator.ofFloat(mTextImageView, "x", textStartX, textEndX).setDuration(300);
//        textAnimator.setInterpolator(new LinearInterpolator());

        int wingStartAlpha = 0;
        int wingEndAlpha = 1;
        ObjectAnimator wingAnimator = ObjectAnimator.ofFloat(mWingImageView, "alpha", wingStartAlpha, wingEndAlpha).setDuration(1300);
        wingAnimator.setInterpolator(new LinearInterpolator());
        wingAnimator.start();
        mWingImageView.setVisibility(View.VISIBLE);

        int textStartAlpha = 0;
        int textEndAlpha = 1;
        final ObjectAnimator textAnimator = ObjectAnimator.ofFloat(mTextImageView, "alpha", textStartAlpha, textEndAlpha).setDuration(1300);
        textAnimator.setInterpolator(new LinearInterpolator());
        textAnimator.start();
        mTextImageView.setVisibility(View.VISIBLE);

        wingAnimator.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

        });

        textAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            Intent intent = LoginActivity.getIntent(WelcomeActivity.this);
                            startActivity(intent);
                            finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

    }
}
