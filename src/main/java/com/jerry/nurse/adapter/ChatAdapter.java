package com.jerry.nurse.adapter;

import android.content.Context;

import com.jerry.nurse.model.ChatMessage;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import java.util.List;

/**
 * Created by zhy on 15/9/4.
 */
public class ChatAdapter extends MultiItemTypeAdapter<ChatMessage> {
    public ChatAdapter(Context context, List<ChatMessage> datas) {
        super(context, datas);

        addItemViewDelegate(new SendItemDelagate());
        addItemViewDelegate(new ReceiveItemDelagate());
    }
}
