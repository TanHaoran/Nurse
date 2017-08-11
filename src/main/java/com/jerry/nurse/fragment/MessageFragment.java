package com.jerry.nurse.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.jerry.nurse.R;
import com.jerry.nurse.activity.AddContactApplyActivity;
import com.jerry.nurse.model.Message;
import com.jerry.nurse.util.DateUtil;
import com.jerry.nurse.util.DensityUtil;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.SPUtil;
import com.jerry.nurse.view.RecycleViewDivider;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;

/**
 * Created by Jerry on 2017/7/15.
 */

public class MessageFragment extends BaseFragment {

    @Bind(R.id.rv_message)
    RecyclerView mRecyclerView;

    private MessageAdapter mAdapter;

    private List<Message> mMessages;

    private String mRegisterId;


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
    }

    @Override
    public void onStart() {
        super.onStart();
        // 刷新界面
        refresh();
    }

    /**
     * 刷新界面
     */
    public void refresh() {
        mRegisterId = (String) SPUtil.get(getActivity(), SPUtil.REGISTER_ID, "");

        // 加载本地数据库中的消息
        loadLocalMessage();

        // 设置间隔线
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(),
                LinearLayoutManager.HORIZONTAL, DensityUtil.dp2px(getActivity(), 0.5f),
                getResources().getColor(R.color.divider_line)));

        mAdapter = new MessageAdapter(getActivity(), R.layout.item_message, mMessages);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 加载本地数据库中的消息
     */
    private void loadLocalMessage() {
        try {
            mMessages = DataSupport.where("mRegisterId=?", mRegisterId).order("mTime desc").find(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mMessages == null) {
            mMessages = new ArrayList<>();
        }
    }


    class MessageAdapter extends CommonAdapter<Message> {

        public MessageAdapter(Context context, int layoutId, List<Message> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, final Message message, final int position) {
            final int type = message.getType();
            if (type == Message.TYPE_ADD_FRIEND_APPLY) {
                holder.setImageResource(R.id.iv_image, message.getImageResource());
            }
            holder.setText(R.id.tv_title, message.getTitle());
            holder.setText(R.id.tv_content, message.getContent());
            holder.setText(R.id.tv_time, DateUtil.parseDateToString(new Date(message.getTime())));

            holder.getView(R.id.rl_message).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (type == Message.TYPE_ADD_FRIEND_APPLY) {
                        Intent intent = AddContactApplyActivity.getIntent(getActivity());
                        startActivity(intent);
                    }
                }
            });

            // 删除这条记录
            holder.getView(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    L.i("执行删除");
                    mMessages.remove(message);
                    message.delete();
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
