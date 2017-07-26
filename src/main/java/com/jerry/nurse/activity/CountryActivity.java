package com.jerry.nurse.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.Country;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.view.RecycleViewDivider;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

public class CountryActivity extends BaseActivity {

    public static final String EXTRA_COUNTRY_NAME = "extra_country_name";
    public static final String EXTRA_COUNTRY_CODE = "extra_country_code";

    @Bind(R.id.rv_country)
    RecyclerView mRecyclerView;

    private CountryAdapter mAdapter;

    private List<Country> mCountries;

    private String mCountryCode;

    public static Intent getIntent(Context context, String countryCode) {
        Intent intent = new Intent(context, CountryActivity.class);
        intent.putExtra(EXTRA_COUNTRY_CODE, countryCode);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_country;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mCountryCode = getIntent().getStringExtra(EXTRA_COUNTRY_CODE);
        getCountries();
    }

    /**
     * 获取国家信息
     */
    private void getCountries() {
        mProgressDialog.show();
        OkHttpUtils.get().url(ServiceConstant.GET_COUNTRIES)
                .build()
                .execute(new FilterStringCallback() {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        mProgressDialog.dismiss();
                        try {
                            mCountries = new Gson().fromJson(response,
                                    new TypeToken<List<Country>>() {
                                    }.getType());
                            setData();
                        } catch (JsonSyntaxException e) {
                            e.printStackTrace();
                            L.i("获取国家信息失败");
                        }
                    }
                });
    }

    private void setData() {
        mAdapter = new CountryAdapter(this, R.layout.item_string, mCountries);
        // 设置间隔线
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this,
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(this, 0.5f),
                getResources().getColor(R.color.divider_line)));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    class CountryAdapter extends CommonAdapter<Country> {

        public CountryAdapter(Context context, int layoutId, List countries) {
            super(context, layoutId, countries);
        }

        @Override
        protected void convert(final ViewHolder holder, final Country country, final int position) {

            ((TextView) holder.getView(R.id.tv_string)).setText(country.getCountryName()
                    + " " + country.getCountryCode());
            if (country.getCountryCode().equals(mCountryCode)) {
                holder.getView(R.id.iv_choose).setVisibility(View.VISIBLE);
            } else {
                holder.getView(R.id.iv_choose).setVisibility(View.INVISIBLE);
            }

            holder.getView(R.id.ll_country).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString(EXTRA_COUNTRY_NAME, country.getCountryName());
                    bundle.putString(EXTRA_COUNTRY_CODE, country.getCountryCode());
                    intent.putExtras(bundle);

                    ActivityCollector.getTopActivity().setResult(Activity.RESULT_OK, intent);
                    ActivityCollector.getTopActivity().finish();
                }
            });
        }
    }
}
