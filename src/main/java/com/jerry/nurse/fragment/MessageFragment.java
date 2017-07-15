package com.jerry.nurse.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.jerry.nurse.R;
import com.jerry.nurse.adapter.BannerAdapter;
import com.jerry.nurse.util.L;
import com.jerry.nurse.view.ViewPagerScroller;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Jerry on 2017/7/15.
 */

public class MessageFragment extends Fragment {

    // Banner停留时间
    private static final int BANNER_DELAYED = 5000;
    // Banner滚动持续时间
    private static final int SCROLLER_DURATION = 1000;

    @Bind(R.id.vp_banner)
    ViewPager mViewPager;

    @Bind(R.id.ci_banner)
    CircleIndicator mIndicator;

    List<View> mBanners;

    private Runnable mBannerRunnable;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentItem = mViewPager.getCurrentItem();
            if (currentItem != mBanners.size() - 1) {
                mViewPager.setCurrentItem(currentItem + 1);
            } else {
                mViewPager.setCurrentItem(0);
            }
            postDelayed(mBannerRunnable, BANNER_DELAYED);
        }
    };

    public static MessageFragment newInstance() {

        MessageFragment fragment = new MessageFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        L.i("初始化消息页面");
        View view = inflater.inflate(R.layout.fragment_message,
                container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        mBanners = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ImageView banner = new ImageView(getActivity());
            banner.setImageResource(R.drawable.banner);
            banner.setScaleType(ImageView.ScaleType.FIT_XY);
            mBanners.add(banner);
        }

        BannerAdapter bannerAdapter = new BannerAdapter(mBanners);
        mViewPager.setAdapter(bannerAdapter);

        // 设置Banner导航点
        mIndicator.setViewPager(mViewPager);
        // 设置Banner滚动速度
        ViewPagerScroller scroller = new ViewPagerScroller(getActivity());
        scroller.setScrollDuration(SCROLLER_DURATION);
        scroller.initViewPagerScroll(mViewPager);

        // 每5秒切换一条Banner
        mBannerRunnable = new Runnable() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(0);
            }
        };
        mHandler.postDelayed(mBannerRunnable, BANNER_DELAYED);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(mBannerRunnable);
    }
}
