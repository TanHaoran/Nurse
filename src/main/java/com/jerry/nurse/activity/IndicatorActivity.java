package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.jerry.nurse.R;
import com.jerry.nurse.transform.RotatePageTransformer;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.view.MyViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;

public class IndicatorActivity extends BaseActivity {

    @Bind(R.id.viewpager)
    MyViewPager mViewPager;

    @Bind(R.id.btn_use)
    Button mUseButton;

    private List<ImageView> mImageList;
    private int[] mImageIds = new int[]{R.drawable.guide_1, R.drawable.guide_2, R.drawable.guide_3};


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, IndicatorActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_indicator;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        boolean isFirstOpen = (boolean) SPUtil.get(this, SPUtil.IS_FIRST_OPEN, true);
        if (!isFirstOpen) {
            gnToLogin();
            finish();
        }

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mImageList = new ArrayList<>();
        mViewPager.setAdapter(new PagerAdapter() {

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                ImageView imageView = new ImageView(IndicatorActivity.this);
                imageView.setImageResource(mImageIds[position]);
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                container.addView(imageView);
                mImageList.add(imageView);
                mViewPager.addViewByPosition(position, imageView);
                return imageView;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImageList.get(position));
                mViewPager.removeViewByPosition(position);
            }

            @Override
            public int getCount() {
                return mImageIds.length;
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (mViewPager.getCurrentItem() == 2) {
                    mUseButton.setVisibility(View.VISIBLE);
                } else {
                    mUseButton.setVisibility(View.GONE);
                }

            }
        });
        mViewPager.setPageTransformer(true, new RotatePageTransformer());
    }

    @OnClick(R.id.btn_use)
    void onUse(View view) {
        gnToLogin();
    }

    private void gnToLogin() {
        Intent intent = LoginActivity.getIntent(this);
        startActivity(intent);
        SPUtil.put(this, SPUtil.IS_FIRST_OPEN, false);
        finish();
    }
}
