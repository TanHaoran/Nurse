package com.jerry.nurse.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.jerry.nurse.R;
import com.jerry.nurse.adapter.ContactAdapter;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.view.RecycleViewDivider;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Jerry on 2017/7/15.
 */

public class MessageFragment extends BaseFragment {


    @Bind(R.id.rv_message)
    RecyclerView mRecyclerView;

    private ContactAdapter mAdapter;

    private List<Contact> mContacts;

    private String[] mNames = {"RyanGosling", "RussellCrowe", "EvaGreen",
            "JenniferLawrence", "KateWinslet", "JuliaRoberts", "BlakeLively",
            "DianeKruger", "Kate", "JayChou", "Catherine", "Jolin", "Jay", "Haha",
            "TanHaoran", "YanMing", "Jerry", "doomthr",};

    /**
     * 实例化方法
     *
     * @return
     */
    public static MessageFragment newInstance() {

        MessageFragment fragment = new MessageFragment();
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_message;
    }

    @Override
    public void init(Bundle savedInstanceState) {
        mContacts = new ArrayList<>();
        for (int i = 0; i < mNames.length; i++) {
            Contact c = new Contact();
            c.setNickname(mNames[i]);
            mContacts.add(c);
        }

        // 设置间隔线
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(),
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(getActivity(), 0.5f), getResources().getColor(R.color.divider_line)));

        mAdapter = new ContactAdapter(getActivity(), R.layout.item_contact, mContacts);
        mRecyclerView.setAdapter(mAdapter);
    }


}
