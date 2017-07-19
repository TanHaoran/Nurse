package com.jerry.nurse.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.activity.HtmlActivity;

import butterknife.OnClick;

/**
 * Created by Jerry on 2017/7/15.
 */

public class OfficeFragment extends BaseFragment {

    private static final String REPORT_EVENT_URL = "http://192.168.0.188:8082";

    private static final String NURSE_CLASS_URL = "http://192.168.0.100:8100";

    /**
     * 实例化方法
     *
     * @return
     */
    public static OfficeFragment newInstance() {
        OfficeFragment fragment = new OfficeFragment();
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_office;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    @OnClick(R.id.ll_event_report)
    void onEventReport(View view) {
        Intent intent = HtmlActivity.getIntent(getActivity(), REPORT_EVENT_URL);
        startActivity(intent);
    }

    @OnClick(R.id.ll_nurse_class)
    void onNurseClass(View view) {
        Intent intent = HtmlActivity.getIntent(getActivity(), NURSE_CLASS_URL);
        startActivity(intent);
    }

    @OnClick(R.id.ll_credit_check)
    void onCreditCheck(View view) {
    }

    @OnClick(R.id.ll_schedule_check)
    void onScheduleCheck(View view) {
    }
}
