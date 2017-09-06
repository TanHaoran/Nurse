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
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.LocalContactCache;
import com.jerry.nurse.util.LocalGroupCache;
import com.jerry.nurse.util.SPUtil;
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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mAdapter = new MessageAdapter(getActivity(), R.layout.item_message, mMessages);
        mRecyclerView.setAdapter(mAdapter);
    }

    /**
     * 加载本地数据库中的消息
     */
    private void loadLocalMessage() {
        try {
            mMessages = DataSupport.where("mRegisterId=?",
                    mRegisterId).order("mTime desc").find(Message.class);
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
        protected void convert(final ViewHolder holder, final Message message, final int position) {
            final int type = message.getType();
            holder.setText(R.id.tv_time, DateUtil.parseDateToChatDate(new Date(message.getTime())));
            final ImageView imageView = holder.getView(R.id.iv_avatar_arrow);
            switch (type) {
                // 好友申请消息
                case Message.TYPE_ADD_FRIEND_APPLY:
                    imageView.setImageResource(message.getImageResource());
                    holder.setText(R.id.tv_title, message.getTitle());
                    holder.setText(R.id.tv_content, message.getContent());
                    holder.setVisible(R.id.ll_status, false);
                    break;
                // 单聊聊天消息
                case Message.TYPE_CHAT:
                    new LocalContactCache() {
                        @Override
                        protected void onLoadContactInfoSuccess(ContactInfo info) {
                            ChatMessage chatMessage = null;
                            if (info != null) {
                                try {
                                    // 读取聊天记录中最后一条记录
                                    chatMessage = DataSupport.where("(mFrom=? and mTo=?) or (mFrom=? and mTo=?)",
                                            mRegisterId, message.getContactId(),
                                            message.getContactId(), mRegisterId).findLast(ChatMessage.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                // 加载头像
                                Glide.with(getActivity()).load(info.getAvatar())
                                        .error(R.drawable.icon_avatar_default).into(imageView);
                                holder.setText(R.id.tv_title, info.getDisplayName());
                                holder.setVisible(R.id.ll_status, true);
                                if (chatMessage != null) {
                                    if (chatMessage.getType() == ChatMessage.TYPE_TXT) {
                                        // 读取并设置已读未读的状态
                                        boolean read = chatMessage.isRead();
                                        if (read) {
                                            holder.setVisible(R.id.tv_read, true);
                                            holder.setVisible(R.id.tv_unread, false);
                                        } else {
                                            holder.setVisible(R.id.tv_unread, true);
                                            holder.setVisible(R.id.tv_read, false);
                                        }
                                        holder.setText(R.id.tv_content, chatMessage.getContent());
                                    } else if (chatMessage.getType() == ChatMessage.TYPE_VOICE) {
                                        holder.setText(R.id.tv_content, "语音消息");
                                    } else if (chatMessage.getType() == ChatMessage.TYPE_IMAGE) {
                                        holder.setText(R.id.tv_content, "图片消息");
                                    }
                                } else {
                                    setEmptyContent(holder);
                                }
                            } else {
                                setEmptyContent(holder);

                            }
                        }
                    }.getContactInfo(mRegisterId, message.getContactId());
                    break;
                // 群聊聊天消息
                case Message.TYPE_CHAT_GROUP:
                    new LocalGroupCache() {
                        @Override
                        protected void onLoadGroupInfoSuccess(GroupInfo info) {
                            // 设置群聊头像
                            imageView.setImageResource(message.getImageResource());
                            ChatMessage groupMessage = null;
                            if (info != null) {
                                holder.setText(R.id.tv_title, info.getHXNickName());
                                try {
                                    // 获取最后一条消息记录
                                    groupMessage = DataSupport.where("mFrom=? or mTo=?",
                                            message.getContactId(), message.getContactId()).findLast(ChatMessage.class);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (groupMessage != null) {
                                    holder.setText(R.id.tv_content, groupMessage.getContent());
                                    holder.setVisible(R.id.ll_status, false);
                                } else {
                                    setEmptyContent(holder);
                                }
                            } else {
                                setEmptyContent(holder);
                            }
                        }
                    }.getGroupInfo(mRegisterId,
                            message.getContactId());
                    break;
                case Message.TYPE_WELCOME:
                    holder.setText(R.id.tv_title, message.getTitle());
                    holder.setText(R.id.tv_content, message.getContent());
                    imageView.setImageResource(message.getImageResource());
                    holder.setVisible(R.id.ll_status, false);
                    break;

            }

            // 设置点击事件
            holder.getView(R.id.rl_message).
                    setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent;
                            switch (type) {
                                case Message.TYPE_ADD_FRIEND_APPLY:
                                    intent = AddContactApplyActivity.getIntent(getActivity());
                                    startActivity(intent);
                                    break;
                                case Message.TYPE_CHAT:
                                    intent = ChatActivity.getIntent(getActivity(),
                                            message.getContactId(), false);
                                    startActivity(intent);
                                    break;
                                case Message.TYPE_CHAT_GROUP:
                                    intent = ChatActivity.getIntent(getActivity(),
                                            message.getContactId(), true);
                                    startActivity(intent);
                                    break;
                                case Message.TYPE_WELCOME:
                                    break;
                            }
                        }
                    });

            // 删除这条记录
            holder.getView(R.id.btn_delete).

                    setOnClickListener(new View.OnClickListener() {
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

    private void setEmptyContent(ViewHolder holder) {
        holder.setText(R.id.tv_content, "");
    }
}
