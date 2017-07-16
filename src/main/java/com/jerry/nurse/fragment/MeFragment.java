package com.jerry.nurse.fragment;

import android.os.Bundle;

import com.jerry.nurse.R;

/**
 * Created by Jerry on 2017/7/15.
 */

public class MeFragment extends BaseFragment {

    /**
     * 实例化方法
     *
     * @return
     */
    public static MeFragment newInstance() {
        MeFragment fragment = new MeFragment();
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_me;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }
}
