package com.jerry.nurse.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.activity.ChatActivity;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.T;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

public class ContactAdapter extends CommonAdapter<Contact> {

    private List<Contact> mContacts;

    public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
        super(context, layoutId, datas);
        mContacts = datas;
    }

    @Override
    protected void convert(ViewHolder holder, Contact contact, final int position) {
        holder.setText(R.id.tv_nickname, contact.getNickname());
        holder.getView(R.id.rl_contact).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                T.showShort(ActivityCollector.getTopActivity(), "点击了" + position);
                Intent intent = ChatActivity.getIntent(ActivityCollector.getTopActivity());
                ActivityCollector.getTopActivity().startActivity(intent);
            }
        });
    }
}