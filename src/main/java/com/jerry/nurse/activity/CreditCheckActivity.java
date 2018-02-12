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
import com.jerry.nurse.model.CreditDetail;
import com.jerry.nurse.model.CreditResult;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.BottomDialogManager;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.TitleBar;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import org.litepal.crud.DataSupport;

import java.text.DecimalFormat;
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

    private List<CreditDetail> mCredits;
    private List<CreditDetail> mOriginCredits;

    private LoginInfo mLoginInfo;

    private int mYear;

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

        mLoginInfo = DataSupport.findFirst(LoginInfo.class);
        mOriginCredits = new ArrayList<>();
        mCredits = new ArrayList<>();

        Calendar calendar = Calendar.getInstance();
        mYear= calendar.get(Calendar.YEAR);
        final List<String> years = new ArrayList<>();
        for (int i = 2014; i <= mYear; i++) {
            years.add(i + "");
        }

        mTitleBar.setRightText(mYear + "");

        mTitleBar.setOnRightClickListener(new TitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {
                // 创建并显示底布弹出选择框
                BottomDialogManager dialog = new BottomDialogManager(CreditCheckActivity.this);
                dialog.setOnItemSelectedListener(years, new BottomDialogManager.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int position, String item) {
                        mTitleBar.setRightText(item);
                        getCredit(mLoginInfo.getXFId(), Integer.parseInt(item));
                    }
                });
                dialog.showSelectDialog(mTitleBar.getRightText());
                dialog.setTitle("请选择年份");

            }
        });

        getCredit(mLoginInfo.getXFId(), mYear);
    }

    /**
     * 获取学分数据
     *
     * @param xfId
     */
    private void getCredit(String xfId, int year) {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_CREDIT_SCORE)
                .addParams("staffId", xfId)
                .addParams("year", year + "")
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mOriginCredits = new ArrayList<>();
                        mCredits = new ArrayList<>();
                        CreditResult result = new Gson().fromJson(response, CreditResult.class);
                        if (result.getCode() == RESPONSE_SUCCESS) {
                            mOriginCredits = result.getBody();
                            calculationCredit(result.getBody());
                            setCreditData();
                        } else {
                            T.showShort(CreditCheckActivity.this, result.getMsg());
                        }
                    }
                });
    }

    /**
     * 根据分类统计学分
     *
     * @param cds
     */
    private void calculationCredit(List<CreditDetail> cds) {
        for (int i = 0; i < CREDIT_ITEM.length; i++) {
            CreditDetail cd = new CreditDetail();
            cd.setType(CREDIT_ITEM[i]);
            mCredits.add(cd);
        }
        for (CreditDetail cd : cds) {
            switch (cd.getType()) {
                case "国家级学分":
                    addCredit(cd, 0);
                    break;
                case "省级学分":
                    addCredit(cd, 1);
                    break;
                case "一类学术会学分":
                    addCredit(cd, 2);
                    break;
                case "核心期刊论文":
                    addCredit(cd, 3);
                    break;
                case "院级培训班":
                    addCredit(cd, 4);
                    break;
                case "院级专题讲座":
                    addCredit(cd, 5);
                    break;
                case "院级操作培训":
                    addCredit(cd, 6);
                    break;
                case "院级护理查房":
                    addCredit(cd, 7);
                    break;
                case "科室学分":
                    addCredit(cd, 8);
                    break;
                case "考核学分":
                    addCredit(cd, 9);
                    break;
                case "非核心期刊论文学分":
                    addCredit(cd, 10);
                    break;
                case "自学培训学分":
                    addCredit(cd, 11);
                    break;
                case "院外培训学分":
                    addCredit(cd, 12);
                    break;
                case "科研项目学分":
                    addCredit(cd, 13);
                    break;
                case "二类学术会学分":
                    addCredit(cd, 14);
                    break;
                default:
                    break;
            }
        }
    }

    private void addCredit(CreditDetail cd, int position) {
        float score = mCredits.get(position).getScore();
        mCredits.get(position).setScore(score + cd.getScore());
    }


    /**
     * 设置学分列表数据
     */
    private void setCreditData() {
        mAdapter = new CreditAdapter(this, R.layout.item_credit, mCredits);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    class CreditAdapter extends CommonAdapter<CreditDetail> {

        public CreditAdapter(Context context, int layoutId, List<CreditDetail> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, final CreditDetail credit, int position) {
            holder.setText(R.id.tv_name, credit.getType());
            DecimalFormat decimalFormat = new DecimalFormat("0.0");
            holder.setText(R.id.tv_credit, decimalFormat.format(credit.getScore()));
            TextView scoreView = holder.getView(R.id.tv_credit);
            if (credit.getScore() != 0.0f) {
                scoreView.setTextColor(getResources().getColor(R.color.credit_dark));
            } else {
                scoreView.setTextColor(getResources().getColor(R.color.gray_textColor));
            }
            holder.setOnClickListener(R.id.rl_credit, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    List<CreditDetail> details = new ArrayList<>();
                    for(CreditDetail cd : mOriginCredits) {
                        if (cd.getType().equals(credit.getType())) {
                            details.add(cd);
                        }
                    }
                    if (credit.getScore() != 0.0f) {
                        Intent intent = CreditDetailActivity.getIntent(CreditCheckActivity.this, details, mYear);
                        startActivity(intent);
                    }
                }
            });
        }
    }
}
