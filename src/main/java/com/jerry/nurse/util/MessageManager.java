package com.jerry.nurse.util;

import android.support.annotation.NonNull;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.jerry.nurse.R;
import com.jerry.nurse.model.AddFriendApply;
import com.jerry.nurse.model.ChatMessage;
import com.jerry.nurse.model.ContactInfo;
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
        Message message = null;
        ChatMessage chatMessage = null;
        // 单聊
        if (emMessage.getChatType() == EMMessage.ChatType.Chat) {

            // 保存首页消息
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
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(false);
            chatMessage.setGroup(false);
            chatMessage.setContent(msg);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("读取到一条消息，并存入数据库");
        }
        // 群聊
        else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
            // 保存首页消息
            try {
                message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                        emMessage.getTo(), emMessage.getFrom(), "2").findFirst(Message.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (message == null) {
                message = new Message();
            }
            message.setType(Message.TYPE_CHAT_GROUP);
            message.setTime(emMessage.getMsgTime());
            message.setRegisterId(EMClient.getInstance().getCurrentUser());
            message.setContactId(emMessage.getTo());
            message.setImageResource(R.drawable.icon_qlt);
            message.save();

            // 保存聊天消息
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(false);
            chatMessage.setGroup(true);
            chatMessage.setContent(msg);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("读取到一条消息，并存入数据库");
        }
        return chatMessage;
    }

    /**
     * 收到消息：
     * 将消息数据保存在本地数据库
     *
     * @param emMessage
     * @param second
     * @param path
     * @return
     */
    @NonNull
    public static ChatMessage saveReceiveChatMessageLocalData(EMMessage emMessage, float second, String path) {
        Message message = null;
        ChatMessage chatMessage = null;
        // 单聊
        if (emMessage.getChatType() == EMMessage.ChatType.Chat) {

            // 保存首页消息
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
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setPath(path);
            chatMessage.setSecond(second);
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(false);
            chatMessage.setGroup(false);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("读取到一条消息，并存入数据库");
        }
        // 群聊
        else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
            // 保存首页消息
            try {
                message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                        emMessage.getTo(), emMessage.getFrom(), "2").findFirst(Message.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (message == null) {
                message = new Message();
            }
            message.setType(Message.TYPE_CHAT_GROUP);
            message.setTime(emMessage.getMsgTime());
            message.setRegisterId(EMClient.getInstance().getCurrentUser());
            message.setContactId(emMessage.getTo());
            message.setImageResource(R.drawable.icon_qlt);
            message.save();

            // 保存聊天消息
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setPath(path);
            chatMessage.setSecond(second);
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(false);
            chatMessage.setGroup(true);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("读取到一条消息，并存入数据库");
        }
        return chatMessage;
    }

    /**
     * 收到消息：
     * 将消息数据保存在本地数据库
     *
     * @param emMessage
     * @param path
     * @return
     */
    @NonNull
    public static ChatMessage saveImageReceiveChatMessageLocalData(EMMessage emMessage, String path) {
        Message message = null;
        ChatMessage chatMessage = null;
        // 单聊
        if (emMessage.getChatType() == EMMessage.ChatType.Chat) {

            // 保存首页消息
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
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setPath(path);
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(false);
            chatMessage.setGroup(false);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("读取到一条消息，并存入数据库");
        }
        // 群聊
        else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
            // 保存首页消息
            try {
                message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                        emMessage.getTo(), emMessage.getFrom(), "2").findFirst(Message.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (message == null) {
                message = new Message();
            }
            message.setType(Message.TYPE_CHAT_GROUP);
            message.setTime(emMessage.getMsgTime());
            message.setRegisterId(EMClient.getInstance().getCurrentUser());
            message.setContactId(emMessage.getTo());
            message.setImageResource(R.drawable.icon_qlt);
            message.save();

            // 保存聊天消息
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setPath(path);
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(false);
            chatMessage.setGroup(true);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("读取到一条消息，并存入数据库");
        }
        return chatMessage;
    }

    /**
     * 发送消息：
     * 将消息数据保存在本地数据库
     *
     * @param msg
     */
    public static ChatMessage saveSendChatMessageLocalData(EMMessage emMessage, String msg) {
        Message message = null;
        ChatMessage chatMessage = null;
        // 保存首页消息
        // 单聊
        if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
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
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(true);
            chatMessage.setGroup(false);
            chatMessage.setContent(msg);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("发送出一条消息，并存入数据库");

        }
        // 群聊
        else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
            try {
                message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                        emMessage.getFrom(), emMessage.getTo(), "2").findFirst(Message.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (message == null) {
                message = new Message();
            }
            message.setType(Message.TYPE_CHAT_GROUP);
            message.setTime(emMessage.getMsgTime());
            message.setRegisterId(emMessage.getFrom());
            message.setContactId(emMessage.getTo());
            message.setImageResource(R.drawable.icon_qlt);
            message.save();

            // 保存聊天消息
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(true);
            chatMessage.setGroup(true);
            chatMessage.setContent(msg);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("发送出一条消息，并存入数据库");
        }

        return chatMessage;
    }

    /**
     * 发送消息：
     * 将消息数据保存在本地数据库
     *
     * @param emMessage
     * @param second
     * @param path
     * @return
     */
    public static ChatMessage saveSendChatMessageLocalData(EMMessage emMessage, int second, String path) {
        // 保存首页消息
        Message message = null;
        ChatMessage chatMessage = null;
        // 单聊
        if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
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
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setSecond(second);
            chatMessage.setPath(path);
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(true);
            chatMessage.setGroup(false);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("发送出一条消息，并存入数据库");
        }
        // 群聊
        else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
            try {
                message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                        emMessage.getFrom(), emMessage.getTo(), "2").findFirst(Message.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (message == null) {
                message = new Message();
            }
            message.setType(Message.TYPE_CHAT_GROUP);
            message.setTime(emMessage.getMsgTime());
            message.setRegisterId(emMessage.getFrom());
            message.setContactId(emMessage.getTo());
            message.setImageResource(R.drawable.icon_qlt);
            message.save();

            // 保存聊天消息
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setSecond(second);
            chatMessage.setPath(path);
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(true);
            chatMessage.setGroup(true);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("发送出一条消息，并存入数据库");
        }

        return chatMessage;
    }

    /**
     * 发送消息：
     * 将消息数据保存在本地数据库
     *
     * @param emMessage
     * @param path
     * @return
     */
    public static ChatMessage saveSendImageChatMessageLocalData(EMMessage emMessage, String path) {
        // 保存首页消息
        Message message = null;
        ChatMessage chatMessage = null;
        // 单聊
        if (emMessage.getChatType() == EMMessage.ChatType.Chat) {
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
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setPath(path);
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(true);
            chatMessage.setGroup(false);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("发送出一条消息，并存入数据库");
        }
        // 群聊
        else if (emMessage.getChatType() == EMMessage.ChatType.GroupChat) {
            try {
                message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                        emMessage.getFrom(), emMessage.getTo(), "2").findFirst(Message.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (message == null) {
                message = new Message();
            }
            message.setType(Message.TYPE_CHAT_GROUP);
            message.setTime(emMessage.getMsgTime());
            message.setRegisterId(emMessage.getFrom());
            message.setContactId(emMessage.getTo());
            message.setImageResource(R.drawable.icon_qlt);
            message.save();

            // 保存聊天消息
            chatMessage = new ChatMessage();
            if (emMessage.getType() == EMMessage.Type.TXT) {
                chatMessage.setType(ChatMessage.TYPE_TXT);
            } else if (emMessage.getType() == EMMessage.Type.VOICE) {
                chatMessage.setType(ChatMessage.TYPE_VOICE);
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                chatMessage.setType(ChatMessage.TYPE_IMAGE);
            }
            chatMessage.setFrom(emMessage.getFrom());
            chatMessage.setPath(path);
            chatMessage.setTo(emMessage.getTo());
            chatMessage.setSend(true);
            chatMessage.setGroup(true);
            chatMessage.setTime(emMessage.getMsgTime());
            chatMessage.save();
            L.i("发送出一条消息，并存入数据库");
        }

        return chatMessage;
    }

    /**
     * 收到好友申请：
     * 保存好友申请本地数据
     *
     * @param ci
     * @param reason
     */
    public static AddFriendApply saveReceiveAddFriendApplyLocalData(ContactInfo ci, String reason) {
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
        message.setContent(ci.getDisplayName() + "请求添加您为好友");
        message.setTime(new Date().getTime());
        message.setRegisterId(EMClient.getInstance().getCurrentUser());
        message.setContactId(ci.getRegisterId());
        message.save();

        // 构建添加好友消息
        AddFriendApply apply = null;
        try {
            apply = DataSupport.where("mRegisterId=? and mContactId=?",
                    EMClient.getInstance().getCurrentUser(), ci.getRegisterId()).findFirst(AddFriendApply.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (apply == null) {
            apply = new AddFriendApply();
        }
        apply.setAvatar(ci.getAvatar());
        apply.setStatus(AddFriendApply.STATUS_RECEIVE_ING);
        apply.setNickname(ci.getNickName());
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
    public static AddFriendApply updateReceiveAddFriendApplyLocalData(ContactInfo ci, boolean agree) {
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
        apply.setNickname(ci.getNickName());
        apply.setContactId(ci.getRegisterId());
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
     * @param ci
     * @param reason
     */
    public static void saveSendAddFriendApplyLocalData(ContactInfo ci, String reason) {
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
        message.setContent("您已申请添加" + ci.getDisplayName() + "为好友");
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
        apply.setNickname(ci.getNickName());
        apply.setAvatar(ci.getAvatar());
        apply.setStatus(AddFriendApply.STATUS_SEND_ING);
        apply.setContactId(ci.getRegisterId());
        apply.setRegisterId(EMClient.getInstance().getCurrentUser());
        apply.setTime(new Date().getTime());
        apply.setReason(reason);
        apply.save();
    }

    /**
     * 创建群：
     * 保存本地群数据
     *
     * @param groupId
     * @param nickname
     */
    public static void saveCreateGroupLocalData(String groupId, String nickname) {
        // 构建首页消息
        Message message = null;
        try {
            message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                    EMClient.getInstance().getCurrentUser(), groupId, "2").findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }

        message.setType(Message.TYPE_CHAT_GROUP);
        message.setImageResource(R.drawable.icon_nurse_class);
        message.setTitle(nickname);
        message.setTime(new Date().getTime());
        message.setRegisterId(EMClient.getInstance().getCurrentUser());
        message.setContactId(groupId);
        message.save();
    }

    /**
     * 收到消息：群
     * 将消息数据保存在本地数据库
     *
     * @param emMessage
     * @param msg
     * @return
     */
    @NonNull
    public static ChatMessage saveReceiveChatGroupMessageLocalData(EMMessage emMessage, String msg) {
        // 保存首页消息
        Message message = null;
        try {
            message = DataSupport.where("mRegisterId=? and mContactId=? and mType=?",
                    EMClient.getInstance().getCurrentUser(), emMessage.getTo(), "2").findFirst(Message.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (message == null) {
            message = new Message();
        }
        message.setType(Message.TYPE_CHAT_GROUP);
        message.setTime(emMessage.getMsgTime());
        message.setRegisterId(EMClient.getInstance().getCurrentUser());
        message.setContactId(emMessage.getTo());
        message.save();

        // 保存聊天消息
        ChatMessage chatMessage = new ChatMessage();
        if (emMessage.getType() == EMMessage.Type.TXT) {
            chatMessage.setType(ChatMessage.TYPE_TXT);
        } else if (emMessage.getType() == EMMessage.Type.VOICE) {
            chatMessage.setType(ChatMessage.TYPE_VOICE);
        } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
            chatMessage.setType(ChatMessage.TYPE_IMAGE);
        }
        chatMessage.setFrom(emMessage.getFrom());
        chatMessage.setTo(emMessage.getTo());
        chatMessage.setSend(false);
        chatMessage.setGroup(true);
        chatMessage.setContent(msg);
        chatMessage.setTime(emMessage.getMsgTime());
        chatMessage.save();
        L.i("读取到一群条消息，并存入数据库");
        return chatMessage;
    }
}
