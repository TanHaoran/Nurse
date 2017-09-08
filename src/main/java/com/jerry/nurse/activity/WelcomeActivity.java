package com.jerry.nurse.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.jerry.nurse.R;

import butterknife.Bind;

public class WelcomeActivity extends BaseActivity {

    /**
     * 动画持续时间
     */
    private static final int ANIMATION_DURATION = 800;

    /**
     * 首页停留时间
     */
    private static final int STAY_DURATION = 200;

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

        Window window = this.getWindow();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setNavigationBarColor(this.getResources().getColor(R.color.primary));
        }

        int startAlpha = 0;
        int endAlpha = 1;

        // 创建两个图标渐显的动画
        ObjectAnimator wingAnimator = ObjectAnimator
                .ofFloat(mWingImageView, "alpha", startAlpha, endAlpha)
                .setDuration(ANIMATION_DURATION);
        wingAnimator.setInterpolator(new LinearInterpolator());
        mWingImageView.setVisibility(View.VISIBLE);

        ObjectAnimator textAnimator = ObjectAnimator
                .ofFloat(mTextImageView, "alpha", startAlpha, endAlpha)
                .setDuration(ANIMATION_DURATION);
        textAnimator.setInterpolator(new LinearInterpolator());
        mTextImageView.setVisibility(View.VISIBLE);

        AnimatorSet set = new AnimatorSet();
        set.play(wingAnimator).with(textAnimator);
        set.start();

        // 动画播放完毕后跳转到登录页面
        set.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
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

        });
    }
}
