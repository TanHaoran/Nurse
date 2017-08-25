package com.jerry.nurse.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jerry.nurse.R;
import com.jerry.nurse.activity.AddContactApplyActivity;
import com.jerry.nurse.activity.ChatActivity;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.model.GroupInfo;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    class MessageAdapter extends CommonAdapter<Message> {

        public MessageAdapter(Context context, int layoutId, List<Message> datas) {
            super(context, layoutId, datas);
        }

        @Override
        protected void convert(ViewHolder holder, final Message message, final int position) {
            final int type = message.getType();
            holder.setText(R.id.tv_time, DateUtil.parseDateToChatDate(new Date(message.getTime())));
            switch (type) {
                case Message.TYPE_ADD_FRIEND_APPLY:
                    holder.setImageResource(R.id.iv_avatar, message.getImageResource());
                    holder.setText(R.id.tv_title, message.getTitle());
                    holder.setText(R.id.tv_content, message.getContent());
                    break;
                case Message.TYPE_CHAT:
                    ContactInfo info = DataSupport.where("mRegisterId=?",
                            message.getContactId()).findFirst(ContactInfo.class);
                    ChatMessage chatMessage = null;
                    if (info != null) {
                        try {
                            chatMessage = DataSupport.where("(mFrom=? and mTo=?) or (mFrom=? and mTo=?)",
                                    mRegisterId, message.getContactId(),
                                    message.getContactId(), mRegisterId).findLast(ChatMessage.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        ImageView imageView = holder.getView(R.id.iv_avatar);
                        Glide.with(getActivity()).load(info.getAvatar()).error(R.drawable.icon_avatar_default).into(imageView);
                        holder.setText(R.id.tv_title, info.getDisplayName());
                        if (chatMessage.getType() == ChatMessage.TYPE_TXT) {
                            if (chatMessage != null) {
                                holder.setText(R.id.tv_content, chatMessage.getContent());
                            }
                        } else if (chatMessage.getType() == ChatMessage.TYPE_VOICE) {
                            if (chatMessage != null) {
                                holder.setText(R.id.tv_content, "语音消息");
                            }
                        } else if (chatMessage.getType() == ChatMessage.TYPE_IMAGE) {
                            if (chatMessage != null) {
                                holder.setText(R.id.tv_content, "图片消息");
                            }
                        }
                    }
                    break;
                case Message.TYPE_CHAT_GROUP:
                    holder.setImageResource(R.id.iv_avatar, R.drawable.icon_qlt);
                    GroupInfo groupInfo = DataSupport.where("HXGroupId=?",
                            message.getContactId()).findFirst(GroupInfo.class);
                    ChatMessage groupMessage = null;
                    if (groupInfo != null) {
                        try {
                            groupMessage = DataSupport.where("mFrom=? or mTo=?",
                                    message.getContactId(), message.getContactId()).findLast(ChatMessage.class);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        holder.setText(R.id.tv_title, groupInfo.getHXNickName());
                        if (groupMessage != null) {
                            holder.setText(R.id.tv_content, groupMessage.getContent());
                        }
                    }
                    break;
                case Message.TYPE_WELCOME:
                    holder.setText(R.id.tv_title, message.getTitle());
                    holder.setText(R.id.tv_content, message.getContent());
                    holder.setImageResource(R.id.iv_avatar, message.getImageResource());
                    break;

            }

            holder.getView(R.id.rl_message).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (type) {
                        case Message.TYPE_ADD_FRIEND_APPLY:
                            Intent applyIntent = AddContactApplyActivity.getIntent(getActivity());
                            startActivity(applyIntent);
                            break;
                        case Message.TYPE_CHAT:
                            Intent chatIntent = ChatActivity.getIntent(getActivity(),
                                    message.getContactId(), false);
                            startActivity(chatIntent);
                            break;
                        case Message.TYPE_CHAT_GROUP:
                            Intent chatGroupIntent = ChatActivity.getIntent(getActivity(),
                                    message.getContactId(), true);
                            startActivity(chatGroupIntent);
                            break;
                        case Message.TYPE_WELCOME:
                            break;
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
