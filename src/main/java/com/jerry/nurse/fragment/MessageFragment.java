package com.jerry.nurse.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.jerry.nurse.R;
import com.jerry.nurse.adapter.BannerAdapter;
import com.jerry.nurse.view.ViewPagerScroller;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by Jerry on 2017/7/15.
 */

public class MessageFragment extends BaseFragment {

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

    /**
     * 实例化方法
     *
     * @return
     */
    public static MessageFragment newInstance() {

        MessageFragment fragment = new MessageFragment();
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_message;
    }

    @Override
    public void init(Bundle savedInstanceState) {
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
