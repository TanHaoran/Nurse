package com.jerry.nurse.adapter;

import android.content.Context;

import com.jerry.nurse.R;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.ViewHolder;

import java.util.List;


public class ContactAdapter extends CommonAdapter<Contact> {
    public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
        super(context, layoutId, datas);
    }

    @Override
    public void convert(ViewHolder holder, final Contact cityBean) {
        holder.setText(R.id.tvCity, cityBean.getNickName());
    }
}