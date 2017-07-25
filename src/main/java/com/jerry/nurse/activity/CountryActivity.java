package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.jerry.nurse.R;
import com.jerry.nurse.adapter.CountryAdapter;

import butterknife.Bind;

public class CountryActivity extends BaseActivity {

    @Bind(R.id.rv_country)
    RecyclerView mRecyclerView;

    CountryAdapter mAdapter;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, CountryActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_country;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mAdapter = new CountryAdapter(this, R.layout.item_string, null);
        mRecyclerView.setAdapter(mAdapter);
    }
}
