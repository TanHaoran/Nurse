package com.jerry.nurse.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jerry.nurse.R;
import com.jerry.nurse.activity.ChatActivity;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.RecycleViewDivider;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Jerry on 2017/7/15.
 */

public class ContactFragment extends Fragment {

    @Bind(R.id.rv_collection)
    RecyclerView mCollectionRecyclerView;

    @Bind(R.id.rv_contact)
    RecyclerView mContactRecyclerView;

    private ContactAdapter mCollectionAdapter;
    private ContactAdapter mContactAdapter;

    private List<Contact> mCollections;
    private List<Contact> mContacts;

    public static ContactFragment newInstance() {

        ContactFragment fragment = new ContactFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        L.i("初始化联系人页面");
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {

        mCollections = new ArrayList<>();
        mContacts = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Contact c = new Contact();
            c.setNickname(i + "号");
            mCollections.add(c);
            mContacts.add(c);
        }

        // 设置间隔线
        mCollectionRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCollectionRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(),
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(getActivity(), 0.5f), getResources().getColor(R.color.divider_line)));

        mContactRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mContactRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(),
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(getActivity(), 0.5f), getResources().getColor(R.color.divider_line)));

        mCollectionAdapter = new ContactAdapter(getActivity(), R.layout.item_contact, mCollections);
        mContactAdapter = new ContactAdapter(getActivity(), R.layout.item_contact, mContacts);

        mCollectionRecyclerView.setAdapter(mCollectionAdapter);
        mContactRecyclerView.setAdapter(mContactAdapter);
    }

    class ContactAdapter extends CommonAdapter<Contact> {


        public ContactAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
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


}
