package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.model.CreditDetail;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.view.TitleBar;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class CreditDetailActivity extends BaseActivity {

    @Bind(R.id.tb_credit)
    TitleBar mTitleBar;

    @Bind(R.id.tv_credit_start)
    TextView mCreditStartTextView;

    @Bind(R.id.tv_credit)
    TextView mCreditTextView;

    @Bind(R.id.tv_credit_end)
    TextView mCreditEndTextView;

    @Bind(R.id.rl_credit)
    RecyclerView mRecyclerView;

    private CreditDetailAdapter mAdapter;

    private List<CreditDetail> mDetails;

    private static final String EXTRA_DETAILS = "extra_details";
    private static final String EXTRA_YEAR = "extra_year";

    public static Intent getIntent(Context context, List<CreditDetail> details, int year) {
        Intent intent = new Intent(context, CreditDetailActivity.class);
        intent.putExtra(EXTRA_DETAILS, (Serializable) details);
        intent.putExtra(EXTRA_YEAR, year);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_credit_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mDetails = (ArrayList) getIntent().getSerializableExtra(EXTRA_DETAILS);
        int year = getIntent().getIntExtra(EXTRA_YEAR, 2017);
        float score = 0f;
        for (CreditDetail cd : mDetails) {
            score += cd.getScore();
        }
        mTitleBar.setTitle( mDetails.get(0).getType());
        mCreditStartTextView.setText(year + "年获得" + mDetails.get(0).getType() + "学分共");
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        mCreditTextView.setText(decimalFormat.format(score));
        mCreditEndTextView.setText("分");

        mAdapter = new CreditDetailAdapter(this, R.layout.item_credit_detail, mDetails);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }


    class CreditDetailAdapter extends CommonAdapter<CreditDetail> {

        public CreditDetailAdapter(Context context, int layoutId, List<CreditDetail> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, CreditDetail credit, int position) {
            holder.setText(R.id.tv_name, credit.getName());
            holder.setText(R.id.tv_time, "获取时间:" + DateUtil.parseMysqlDateToString(credit.getTime()));
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            holder.setText(R.id.tv_credit, decimalFormat.format(credit.getScore()));
        }
    }
}
