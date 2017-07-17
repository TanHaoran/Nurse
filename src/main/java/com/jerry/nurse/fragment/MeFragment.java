package com.jerry.nurse.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.activity.PersonalInfoActivity;

import butterknife.OnClick;

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

    @OnClick(R.id.ll_personal_info)
    void onPersonalInfo(View view) {
        Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.rl_credit)
    void onCredit(View view) {

    }

    @OnClick(R.id.rl_collection)
    void onCollection(View view) {

    }

    @OnClick(R.id.rl_schedule)
    void onSchedule(View view) {

    }

    @OnClick(R.id.rl_exam)
    void onExam(View view) {

    }

    @OnClick(R.id.rl_setting)
    void onSetting(View view) {

    }


}
