package com.jerry.nurse.adapter;

import android.content.Context;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.model.Country;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by Jerry on 2017/7/25.
 */

public class CountryAdapter extends CommonAdapter<Country> {

    private List<Country> mCountries;

    public CountryAdapter(Context context, int layoutId, List countries) {
        super(context, layoutId, countries);
    }

    @Override
    protected void convert(final ViewHolder holder, Country country, final int position) {

        holder.getView(R.id.ll_country).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < mCountries.size(); i++) {
                    if (i == position) {
                    } else {

                    }
                }
                notifyDataSetChanged();
            }
        });
    }
}
