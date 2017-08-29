package com.jerry.nurse.util;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.chat.EMVoiceMessageBody;
import com.jerry.nurse.R;
import com.jerry.nurse.model.AddFriendApply;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.ContactInfo;
import com.jerry.nurse.model.GroupInfo;
import com.jerry.nurse.model.Message;

import org.litepal.crud.DataSupport;

import java.util.Date;
import java.util.List;

import static com.jerry.nurse.model.AddFriendApply.STATUS_AGREE;

/**
 * Created by Jerry on 2017/8/12.
 */

public class MessageManager {

    /**
     * 更新首页消息和好友申请两个数据库
     *
     * @param apply
     * @param agree 是否同意
     * @param info
     */
    public static void updateApplyData(AddFriendApply apply, boolean agree,
                                       ContactInfo info) {
        // 更新页消息
        Message message = null;
        try {
            message = DataSupport.where("mType=? and mRegisterId=?",
                    "0", apply.getRegisterId()).findFirst(Message.class);
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
        message.setRegisterId(apply.getRegisterId());
        message.setContactId(apply.getContactId());
        if (agree) {
            message.setContent("已同意" + info.getDisplayName() + "的申请");
        } else {
            message.setContent("已拒绝" + info.getDisplayName() + "的申请");
        }
        message.save();

        // 更新好友申请消息
        if (agree) {
            apply.setStatus(STATUS_AGREE);
        } else {
            apply.setStatus(AddFriendApply.STATUS_REFUSE);
        }
        apply.setTime(new Date().getTime());
        apply.save();
    }

    /**
     * 发送消息：
     * 将消息数据保存在本地数据库
     */
    public static ChatMessage saveChatMessageData(
            EMMessage emMessage, boolean isSend) {
        Message message = null;
        ChatMessage chatMessage;
        /**
         * 保存首页消息
         */
        // 单聊
        if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
            try {
                String registerId = EMClient.getInstance().getCurrentUser();
                String contactId;
                // 按照发送和接收区分
                if (isSend) {
                    contactId = emMessage.getTo();
                } else {
                    contactId = emMessage.getFrom();
                }
                message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                        registerId, contactId, "1")
                        .findFirst(Message.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (message == null) {
                message = new Message();
            }
            message.setType(Message.TYPE_CHAT);
            // 发送
            if (isSend) {
                message.setRegisterId(emMessage.getFrom());
                message.setContactId(emMessage.getTo());
            }
            // 接收
            else {
                message.setRegisterId(emMessage.getTo());
                message.setContactId(emMessage.getFrom());
            }
        }
        // 群聊
        else {
            List<Message> all = DataSupport.findAll(Message.class);
            for (Message mes : all) {
                L.i(mes.toString());
            }
            try {
                message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                        EMClient.getInstance().getCurrentUser(), emMessage.getTo(), "2")
                        .findFirst(Message.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (message == null) {
                message = new Message();
            }
            message.setType(Message.TYPE_CHAT_GROUP);
            message.setImageResource(R.drawable.icon_qlt);
            // 发送
            if (isSend) {
                message.setRegisterId(emMessage.getFrom());
                message.setContactId(emMessage.getTo());
            }
            // 接收
            else {
                message.setRegisterId(EMClient.getInstance().getCurrentUser());
                message.setContactId(emMessage.getTo());
                message.setImageResource(R.drawable.icon_qlt);
            }
        }
        message.setTime(emMessage.getMsgTime());
        message.save();

        /**
         * 保存聊天消息
         */
        chatMessage = new ChatMessage();
        // 按照消息类型分
        if (emMessage.getType() == EMMessage.Type.TXT) {
            EMTextMessageBody messageBody = (EMTextMessageBody) emMessage.getBody();
            chatMessage.setContent(messageBody.getMessage());
            chatMessage.setType(ChatMessage.TYPE_TXT);
        } else if (emMessage.getType() == EMMessage.Type.VOICE) {
            EMVoiceMessageBody messageBody = (EMVoiceMessageBody) emMessage.getBody();
            chatMessage.setSecond(messageBody.getLength());
            chatMessage.setPath(messageBody.getLocalUrl());
            chatMessage.setType(ChatMessage.TYPE_VOICE);
        } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
            EMImageMessageBody messageBody = (EMImageMessageBody) emMessage.getBody();
            chatMessage.setLocalUrl(messageBody.getLocalUrl());
            chatMessage.setRemoteUrl(messageBody.getRemoteUrl());
            chatMessage.setType(ChatMessage.TYPE_IMAGE);
        }
        // 按照发送接收分
        if (isSend) {
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(true);
        } else {
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(false);
        }
        // 按照单聊群聊分
        if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
            chatMessage.setGroup(false);
        } else {
            chatMessage.setGroup(true);
        }
        chatMessage.setTime(emMessage.getMsgTime());
        chatMessage.save();

        return chatMessage;
    }

    /**
     * 收到好友申请：
     * 保存好友申请本地数据
     *
     * @param ci
     * @param reason
     */
    public static AddFriendApply saveApplyLocalData(ContactInfo ci,
                                                    String reason, boolean isSend) {
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
        message.setImageResource(R.drawable.icon_xzhy);
        message.setTitle("好友申请");
        if (isSend) {
            message.setContent("您已申请添加" + ci.getDisplayName() + "为好友");
        } else {
            message.setContent(ci.getDisplayName() + "请求添加您为好友");
        }
        message.setTime(new Date().getTime());
        message.setRegisterId(EMClient.getInstance().getCurrentUser());
        message.setContactId(ci.getRegisterId());
        message.save();


        // 构建添加好友消息
        AddFriendApply apply = null;
        try {
            apply = DataSupport.where("mRegisterId=? and mContactId=?",
                    EMClient.getInstance().getCurrentUser(), ci.getRegisterId())
                    .findFirst(AddFriendApply.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apply == null) {
            apply = new AddFriendApply();
        }
        apply.setAvatar(ci.getAvatar());
        if (isSend) {
            apply.setStatus(AddFriendApply.STATUS_SEND_ING);
        } else {
            apply.setStatus(AddFriendApply.STATUS_RECEIVE_ING);
        }
        apply.setNickname(ci.getDisplayName());
        apply.setContactId(ci.getRegisterId());
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
     * @param ci
     * @param agree 是否同意
     */
    public static AddFriendApply updateApplyData(ContactInfo ci, boolean agree) {
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
        message.setImageResource(R.drawable.icon_xzhy);
        message.setTitle("好友申请");
        if (agree) {
            message.setContent("已同意");
        } else {
            message.setContent("已拒绝");
        }
        message.setTime(new Date().getTime());
        message.setRegisterId(EMClient.getInstance().getCurrentUser());
        message.setContactId(ci.getRegisterId());
        message.save();

        // 构建添加好友消息
        AddFriendApply apply = null;
        try {
            apply = DataSupport.where("mRegisterId=? and mContactId=?",
                    EMClient.getInstance().getCurrentUser(),
                    ci.getRegisterId()).findFirst(AddFriendApply.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apply == null) {
            apply = new AddFriendApply();
        }
        apply.setAvatar(ci.getAvatar());
        apply.setNickname(ci.getDisplayName());
        apply.setContactId(ci.getRegisterId());
        apply.setRegisterId(EMClient.getInstance().getCurrentUser());
        // 修改请求状态
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
     * 创建群：
     * 保存本地群数据
     *
     * @param groupInfo
     */
    public static void saveCreateGroupLocalData(GroupInfo groupInfo) {
        // 构建首页消息
        Message message = null;
        try {
            message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                    EMClient.getInstance().getCurrentUser(), groupInfo.getHXGroupId(), "2")
                    .findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setType(Message.TYPE_CHAT_GROUP);
        message.setImageResource(R.drawable.icon_qlt);
        message.setTitle(groupInfo.getHXNickName());
        message.setTime(new Date().getTime());
        message.setRegisterId(EMClient.getInstance().getCurrentUser());
        message.setContactId(groupInfo.getHXGroupId());
        message.save();
    }
}
