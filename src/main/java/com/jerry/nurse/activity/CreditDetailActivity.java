package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.jerry.nurse.R;
import com.jerry.nurse.model.CreditDetail;
import com.jerry.nurse.view.TitleBar;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import butterknife.Bind;

public class CreditDetailActivity extends BaseActivity {

    @Bind(R.id.tb_credit)
    TitleBar mTitleBar;

    @Bind(R.id.tv_credit)
    TextView mCreditTextView;

    @Bind(R.id.rl_credit)
    RecyclerView mRecyclerView;

    private CreditDetailAdapter mAdapter;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CreditDetailActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_credit_detail;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mCreditTextView.setText("2017年获得国家级学分共0.3分");
    }


    class CreditDetailAdapter extends CommonAdapter<CreditDetail> {

        public CreditDetailAdapter(Context context, int layoutId, List<CreditDetail> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, CreditDetail credit, int position) {
            TextView textView = holder.getView(R.id.tv_credit);
            textView.setText(credit.getScore() + "");
            if (credit.getScore() == 0.0f) {
                textView.setTextColor(mContext.getResources().getColor(R.color.gray_textColor));
            } else {
                textView.setTextColor(mContext.getResources().getColor(R.color.credit_dark));
            }
            holder.setText(R.id.tv_name, credit.getName());
            holder.setOnClickListener(R.id.rl_credit, new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
    }
}
