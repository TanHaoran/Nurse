package com.jerry.nurse.util;

import android.support.annotation.NonNull;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.jerry.nurse.R;
import com.jerry.nurse.model.AddFriendApply;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.Message;

import org.litepal.crud.DataSupport;

import java.util.Date;

/**
 * Created by Jerry on 2017/8/12.
 */

public class MessageManager {

    public MessageManager() {
    }


    /**
     * 收到消息：
     * 将消息数据保存在本地数据库
     *
     * @param emMessage
     * @param msg
     * @return
     */
    @NonNull
    public static ChatMessage saveReceiveChatMessageLocalData(EMMessage emMessage, String msg) {
        // 保存首页消息
        Message message = null;
        try {
            message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                    emMessage.getTo(), emMessage.getFrom(), "1").findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setType(Message.TYPE_CHAT);
        message.setTime(emMessage.getMsgTime());
        message.setRegisterId(emMessage.getTo());
        message.setContactId(emMessage.getFrom());
        message.save();

        // 保存聊天消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFrom(emMessage.getFrom());
        chatMessage.setTo(emMessage.getTo());
        chatMessage.setSend(false);
        chatMessage.setContent(msg);
        chatMessage.setTime(emMessage.getMsgTime());
        chatMessage.save();
        L.i("读取到一条消息，并存入数据库");
        return chatMessage;
    }

    /**
     * 发送消息：
     * 将消息数据保存在本地数据库
     *
     * @param msg
     */
    public static ChatMessage saveSendChatMessageLocalData(EMMessage emMessage, String msg) {
        // 保存首页消息
        Message message = null;
        try {
            message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                    emMessage.getFrom(), emMessage.getTo(), "1").findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setType(Message.TYPE_CHAT);
        message.setTime(emMessage.getMsgTime());
        message.setRegisterId(emMessage.getFrom());
        message.setContactId(emMessage.getTo());
        message.save();

        // 保存聊天消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setFrom(emMessage.getFrom());
        chatMessage.setTo(emMessage.getTo());
        chatMessage.setSend(true);
        chatMessage.setContent(msg);
        chatMessage.setTime(emMessage.getMsgTime());
        chatMessage.save();
        L.i("发送出一条消息，并存入数据库");

        return chatMessage;
    }

    /**
     * 收到好友申请：
     * 保存好友申请本地数据
     *
     * @param contactId
     * @param reason
     */
    public static AddFriendApply saveReceiveAddFriendApplyLocalData(String contactId, String reason) {
        // 构建首页消息
        Message message = null;
        try {
            message = DataSupport.where("mType=? and mRegisterId=?", "0",
                    EMClient.getInstance().getCurrentUser()).findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setType(Message.TYPE_ADD_FRIEND_APPLY);
        message.setImageResource(R.drawable.icon_pb);
        message.setTitle("好友申请");
        message.setTime(new Date().getTime());
        message.setRegisterId(EMClient.getInstance().getCurrentUser());
        message.setContactId(contactId);
        message.save();

        // 构建添加好友消息
        AddFriendApply apply = null;
        try {
            apply = DataSupport.where("mRegisterId=? and mContactId=?",
                    EMClient.getInstance().getCurrentUser(), contactId).findFirst(AddFriendApply.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apply == null) {
            apply = new AddFriendApply();
        }
        apply.setStatus(AddFriendApply.STATUS_RECEIVE_ING);
        apply.setContactId(contactId);
        apply.setRegisterId(EMClient.getInstance().getCurrentUser());
        apply.setTime(new Date().getTime());
        apply.setReason(reason);
        apply.save();
        return apply;
    }

    /**
     * 收到好友申请变化：
     * 根据对方同意或拒绝更新数据库
     *
     * @param contactId
     * @param agree     是否同意
     */
    public static AddFriendApply updateReceiveAddFriendApplyLocalData(String contactId, boolean agree) {
        // 构建首页消息
        Message message = null;
        try {
            message = DataSupport.where("mType=? and mRegisterId=?", "0",
                    EMClient.getInstance().getCurrentUser()).findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setType(Message.TYPE_ADD_FRIEND_APPLY);
        message.setImageResource(R.drawable.icon_pb);
        message.setTitle("好友申请");
        message.setTime(new Date().getTime());
        message.setRegisterId(EMClient.getInstance().getCurrentUser());
        message.setContactId(contactId);
        message.save();

        // 构建添加好友消息
        AddFriendApply apply = null;
        try {
            apply = DataSupport.where("mRegisterId=? and mContactId=?",
                    EMClient.getInstance().getCurrentUser(),
                    contactId).findFirst(AddFriendApply.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apply == null) {
            apply = new AddFriendApply();
        }
        apply.setContactId(contactId);
        apply.setRegisterId(EMClient.getInstance().getCurrentUser());
        if (agree) {
            apply.setStatus(AddFriendApply.STATUS_AGREE);
        } else {
            apply.setStatus(AddFriendApply.STATUS_REFUSE);
        }
        apply.setTime(new Date().getTime());
        apply.save();
        return apply;
    }

    /**
     * 发送好友申请：
     * 保存好友申请本地数据
     *
     * @param contactId
     * @param reason
     */
    public static void saveSendAddFriendApplyLocalData(String contactId, String reason) {
        // 构建首页消息
        Message message = null;
        try {
            message = DataSupport.where("mType=?", "0").findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }

        message.setType(Message.TYPE_ADD_FRIEND_APPLY);
        message.setImageResource(R.drawable.icon_xzhy);
        message.setTitle("好友申请");
        message.setTime(new Date().getTime());
        message.setRegisterId(EMClient.getInstance().getCurrentUser());
        message.setContactId(contactId);
        message.save();

        // 构建添加好友消息
        AddFriendApply apply = null;
        try {
            apply = DataSupport.where("mRegisterId=? and mContactId=?",
                    EMClient.getInstance().getCurrentUser(),
                    contactId).findFirst(AddFriendApply.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apply == null) {
            apply = new AddFriendApply();
        }
        apply.setStatus(AddFriendApply.STATUS_SEND_ING);
        apply.setContactId(contactId);
        apply.setRegisterId(EMClient.getInstance().getCurrentUser());
        apply.setTime(new Date().getTime());
        apply.setReason(reason);
        apply.save();
    }
}
