package com.jerry.nurse.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jerry.nurse.R;
import com.jerry.nurse.util.L;

/**
 * Created by Jerry on 2017/7/15.
 */

public class MineFragment extends Fragment {

    public static MineFragment newInstance() {

        MineFragment fragment = new MineFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        L.i("初始化我的页面");
        return inflater.inflate(R.layout.fragment_mine, container, false);
    }
}
