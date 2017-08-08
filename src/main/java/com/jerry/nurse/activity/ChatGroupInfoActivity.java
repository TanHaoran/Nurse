package com.jerry.nurse.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jerry.nurse.R;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.util.CommonAdapter;
import com.jerry.nurse.util.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class ChatGroupInfoActivity extends BaseActivity {


    @Bind(R.id.rv_group)
    RecyclerView mRecyclerView;

    private ContactAdapter mAdapter;

    private List<Contact> mContacts;

    public static Intent getIntent(Context context) {
        Intent intent = new Intent(context, ChatGroupInfoActivity.class);
        return intent;
    }

    @Override
    public int getContentViewResId() {
        return R.layout.activity_chat_group_info;
    }

    @Override
    public void init(Bundle savedInstanceState) {

        mContacts = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            Contact c = new Contact();
            mContacts.add(c);
        }

        mAdapter = new ContactAdapter(this, R.layout.item_group_contact, mContacts);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    class ContactAdapter extends CommonAdapter<Contact> {
        public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        public void convert(ViewHolder holder, final Contact cityBean) {
        }
    }
}
