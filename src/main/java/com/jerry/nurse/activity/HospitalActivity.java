package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.model.Hospital;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.view.RecycleViewDivider;
import com.jerry.nurse.view.TitleBar;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.BindString;


public class HospitalActivity extends BaseActivity {

    @Bind(R.id.tb_list)
    TitleBar mTitleBar;

    @Bind(R.id.rv_list)
    RecyclerView mRecyclerView;

    @BindString(R.string.hospital)
    String mTitle;

    private List<Hospital> mHospitals;

    private HospitalAdapter mAdapter;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, HospitalActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_list;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mTitleBar.setTitle(mTitle);

        mHospitals = new ArrayList();
        for (int i = 0; i < 20; i++) {
            Hospital hospital = new Hospital("医院" + i);
            mHospitals.add(hospital);
        }

        mAdapter = new HospitalAdapter(this, R.layout.item_string, mHospitals);

        // 设置间隔线
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f),
                getResources().getColor(R.color.divider_line)));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    class HospitalAdapter extends CommonAdapter<Hospital> {


        public HospitalAdapter(Context context, int layoutId, List<Hospital> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Hospital hospital, int position) {
            ((TextView) holder.getView(R.id.tv_string)).setText(hospital.getName());
        }
    }
}
