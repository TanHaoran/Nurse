package com.jerry.nurse.adapter;

import android.content.Context;

import com.jerry.nurse.R;
import com.jerry.nurse.model.MeiTuanBean;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.ViewHolder;

import java.util.List;


/**
 * Created by zhangxutong .
 * Date: 16/08/28
 */

public class MeituanAdapter extends CommonAdapter<MeiTuanBean> {
    public MeituanAdapter(Context context, int layoutId, List<MeiTuanBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, final MeiTuanBean cityBean) {
        holder.setText(R.id.tvCity, cityBean.getCity());
    }
}