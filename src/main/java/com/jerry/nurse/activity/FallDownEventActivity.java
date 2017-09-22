package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.jerry.nurse.R;
import com.jerry.nurse.listener.OnDateSelectListener;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.view.EventLineEditView;
import com.jerry.nurse.view.EventLineSelectView;
import com.jerry.nurse.view.EventLineTimeView;
import com.jerry.nurse.view.EventLineToggleView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

public class FallDownEventActivity extends BaseActivity {

    @Bind(R.id.eltv_happened_time)
    EventLineTimeView mHappenedTimeView;

    @Bind(R.id.elev_patient_number)
    EventLineEditView mPatientNumberView;

    @Bind(R.id.eltv_sex)
    EventLineToggleView mSexView;

    @Bind(R.id.elsv_age)
    EventLineSelectView mAgeView;

    @Bind(R.id.elsv_nursing_level)
    EventLineSelectView mNursingLevelView;


    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, FallDownEventActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_fall_down_event;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        // 发生时间
        setDateSelectListener(mHappenedTimeView, new Date(), new OnDateSelectListener() {
            @Override
            public void onDateSelected(Date date) {
                mHappenedTimeView.setValue(DateUtil
                        .parseDateToStringDetail(date));
            }
        });

        // 患者年龄
        List<String> ages = new ArrayList<>();
        ages.add("岁");
        ages.add("月");
        ages.add("天");
        mAgeView.setItems(ages);

        // 护理等级
        List<String> nursingLevels = new ArrayList<>();
        nursingLevels.add("I级");
        nursingLevels.add("II级");
        nursingLevels.add("III级");
        nursingLevels.add("特级");
        mNursingLevelView.setItems(nursingLevels);
    }
}
