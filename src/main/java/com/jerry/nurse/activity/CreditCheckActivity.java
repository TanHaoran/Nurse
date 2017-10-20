package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Credit;
import com.jerry.nurse.model.HospitalResult;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.BottomDialogManager;
import com.jerry.nurse.util.L;
import com.jerry.nurse.view.TitleBar;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.Bind;

import static com.jerry.nurse.constant.ServiceConstant.RESPONSE_SUCCESS;

public class CreditCheckActivity extends BaseActivity {

    @Bind(R.id.tb_credit)
    TitleBar mTitleBar;

    @Bind(R.id.rl_credit)
    RecyclerView mRecyclerView;

    private CreditAdapter mAdapter;

    private List<Credit> mCredits;

    private static final String[] CREDIT_ITEM = {"国家级学分", "省级学分", "一类学术会学分",
            "核心期刊论文", "院级培训班", "院级专题讲座", "院级操作培训", "院级护理查房", "科室学分",
            "考核学分", "非核心期刊论文学分", "自学培训学分", "院外培训学分", "科研项目学分", "二类学术会学分"};

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CreditCheckActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_credit;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        Calendar calendar = Calendar.getInstance();
        int end = calendar.get(Calendar.YEAR);
        final List<String> years = new ArrayList<>();
        for (int i = 2014; i <= end; i++) {
            years.add(i + "");
        }

        mTitleBar.setRightText(end + "");

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                // 创建并显示底布弹出选择框
                BottomDialogManager dialog = new BottomDialogManager(CreditCheckActivity.this);
                dialog.setOnItemSelectedListener(years, new BottomDialogManager.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int position, String item) {
                    }
                });
                dialog.showSelectDialog(mTitleBar.getRightText());
                dialog.setTitle("请选择年份");

            }
        });

        mCredits = new ArrayList<>();
        for (int i = 0; i < CREDIT_ITEM.length; i++) {
            Credit credit = new Credit();
            credit.setName(CREDIT_ITEM[i]);
            credit.setScore(i);
            mCredits.add(credit);
        }
        setCreditData();
    }

    /**
     * 获取学分数据
     *
     * @param lat
     * @param lng
     */
    private void getCredit(String lat, String lng) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_NEARBY_HOSPITAL_LIST)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        HospitalResult hospitalResult = new Gson().fromJson(response, HospitalResult.class);
                        if (hospitalResult.getCode() == RESPONSE_SUCCESS) {

                        } else {
                            L.i("获取附近医院失败");
                        }
                    }
                });
    }

    /**
     * 设置学分列表数据
     */
    private void setCreditData() {
        mAdapter = new CreditAdapter(this, R.layout.item_credit, mCredits);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    class CreditAdapter extends CommonAdapter<Credit> {

        public CreditAdapter(Context context, int layoutId, List<Credit> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Credit credit, int position) {
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
                    Intent intent = CreditDetailActivity.getIntent(CreditCheckActivity.this);
                    startActivity(intent);
                }
            });
        }
    }
}
