package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jerry.nurse.R;

import butterknife.OnClick;

public class EventReportActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, EventReportActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_event_report;
    }

    @Override
    public void init(Bundle savedInstanceState) {

    }

    /**
     * 跌倒事件
     *
     * @param view
     */
    @OnClick(R.id.tv_fall_down)
    void onFallDown(View view) {

    }

    /**
     * 坠床事件
     *
     * @param view
     */
    @OnClick(R.id.tv_fall_bed)
    void onFallBed(View view) {

    }

    /**
     * 管路事件
     *
     * @param view
     */
    @OnClick(R.id.tv_pipe_line)
    void onPipeLine(View view) {

    }

    /**
     * 给药事件
     *
     * @param view
     */
    @OnClick(R.id.tv_medicine)
    void onMedicine(View view) {

    }

    /**
     * 压疮事件
     *
     * @param view
     */
    @OnClick(R.id.tv_pressure_sore)
    void onPressureSore(View view) {

    }

    /**
     * 职业暴露
     *
     * @param view
     */
    @OnClick(R.id.tv_profession)
    void onProjession(View view) {

    }

    /**
     * 其他事件
     *
     * @param view
     */
    @OnClick(R.id.tv_other)
    void onOther(View view) {

    }

    /**
     * 零事件
     *
     * @param view
     */
    @OnClick(R.id.tv_zero)
    void onZero(View view) {

    }
}
