package com.jerry.nurse.adapter;

import com.jerry.nurse.R;
import com.jerry.nurse.model.ChatMessage;
import com.zhy.adapter.recyclerview.base.ItemViewDelegate;
import com.zhy.adapter.recyclerview.base.ViewHolder;

/**
 * Created by zhy on 16/6/22.
 */
public class SendItemDelagate implements ItemViewDelegate<ChatMessage> {

    @Override
    public int getItemViewLayoutId() {
        return R.layout.item_chat_send;
    }

    @Override
    public boolean isForViewType(ChatMessage item, int position) {
        return item.isSend();
    }

    @Override
    public void convert(ViewHolder holder, ChatMessage chatMessage, int position) {
        holder.setText(R.id.tv_message, chatMessage.getContent());
        holder.setImageResource(R.id.iv_avatar, chatMessage.getAvatar());
    }
}
