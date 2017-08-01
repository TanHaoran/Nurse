package com.jerry.nurse.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.activity.ChatActivity;
import com.jerry.nurse.model.Contact;
import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.T;
import com.jerry.nurse.view.RecycleViewDivider;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Jerry on 2017/7/15.
 */

public class MessageFragment extends BaseFragment {


    @Bind(R.id.rv_message)
    RecyclerView mRecyclerView;

    private MessageAdapter mAdapter;

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
            c.setNickName(mNames[i]);
            mContacts.add(c);
        }

        // 设置间隔线
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(),
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(getActivity(), 0.5f),
                getResources().getColor(R.color.divider_line)));

        mAdapter = new MessageAdapter(getActivity(), R.layout.item_contact, mContacts);
        mRecyclerView.setAdapter(mAdapter);
    }

    class MessageAdapter extends CommonAdapter<Contact> {


        public MessageAdapter(Context context, int layoutId, List<Contact> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, Contact contact, final int position) {
            holder.setText(R.id.tv_nickname, contact.getNickName());
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
