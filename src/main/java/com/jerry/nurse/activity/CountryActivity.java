package com.jerry.nurse.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.jerry.nurse.R;
import com.jerry.nurse.constant.ServiceConstant;
import com.jerry.nurse.model.CountriesResult;
import com.jerry.nurse.model.Country;
import com.jerry.nurse.net.FilterStringCallback;
import com.jerry.nurse.util.ProgressDialogManager;
import com.jerry.nurse.util.RecyclerViewDecorationUtil;
import com.jerry.nurse.util.T;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import com.zhy.http.okhttp.OkHttpUtils;

import java.util.List;

import butterknife.Bind;
import okhttp3.Call;

/**
 * 选择国家编码界面
 */
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
        mProgressDialogManager = new ProgressDialogManager(this);

        mCountryCode = getIntent().getStringExtra(EXTRA_COUNTRY_CODE);
        getCountries();
    }

    /**
     * 获取国家信息
     */
    private void getCountries() {
        mProgressDialogManager.show();
        OkHttpUtils.get().url(ServiceConstant.GET_COUNTRIES)
                .build()
                .execute(new FilterStringCallback(mProgressDialogManager) {

                    @Override
                    public void onFilterError(Call call, Exception e, int id) {
                    }

                    @Override
                    public void onFilterResponse(String response, int id) {
                        CountriesResult countriesResult = new Gson().fromJson(response, CountriesResult.class);
                        if (countriesResult.getCode() == 0) {
                            mCountries = countriesResult.getBody();
                            if (mCountries.size() > 0) {
                                setCountriesData();
                            } else {
                                T.showShort(CountryActivity.this, "国家列表为空");
                            }
                        } else {
                            T.showShort(CountryActivity.this, "读取国家列表失败");
                        }
                    }
                });
    }

    /**
     * 设置国家代码数据
     */
    private void setCountriesData() {
        // 设置间隔线
        RecyclerViewDecorationUtil.addItemDecoration(this, mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CountryAdapter(this, R.layout.item_string, mCountries);
        mRecyclerView.setAdapter(mAdapter);
    }

    class CountryAdapter extends CommonAdapter<Country> {

        public CountryAdapter(Context context, int layoutId, List countries) {
            super(context, layoutId, countries);
        }

        @Override
        protected void convert(final ViewHolder holder, final Country country, final int position) {
            // 将国家名称和号码区拼接到一起
            holder.setText(R.id.tv_string, country.getCountryName() + " " + country.getCountryCode());
            // 对当前选择的国家进行打钩
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
                    // 将国家名称和号码回传回去
                    bundle.putString(EXTRA_COUNTRY_NAME, country.getCountryName());
                    bundle.putString(EXTRA_COUNTRY_CODE, country.getCountryCode());
                    intent.putExtras(bundle);

                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }
            });
        }
    }
}
