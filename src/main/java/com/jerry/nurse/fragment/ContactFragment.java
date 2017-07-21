package com.jerry.nurse.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jerry.nurse.R;
import com.jerry.nurse.adapter.ContactAdapter;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.util.L;
import com.jerry.nurse.view.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by Jerry on 2017/7/15.
 */

public class ContactFragment extends Fragment {

    @Bind(R.id.rv_contact)
    RecyclerView mRecyclerView;

    private ContactAdapter mAdapter;

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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mContacts = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            Contact c = new Contact();
            c.setNickname(i + "号");
            mContacts.add(c);
        }

        // 设置间隔线
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(),
                LinearLayoutManager.HORIZONTAL, 2, getResources().getColor(R.color.iron)));

        mAdapter = new ContactAdapter(getActivity(), R.layout.item_contact, mContacts);
        mRecyclerView.setAdapter(mAdapter);
    }


}
