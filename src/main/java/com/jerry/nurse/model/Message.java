package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Jerry on 2017/8/10.
 */

public class Message extends DataSupport implements Serializable {

    public static final int TYPE_ADD_FRIEND_APPLY = 0;
    public static final int TYPE_CHAT = 1;
    public static final int TYPE_CHAT_GROUP = 2;
    public static final int TYPE_WELCOME = 3;
//    public static final int TYPE_EVENT_REPORT = 2;

    private String mRegisterId;
    /**
     * 只有当是聊天消息的时候，这个属性才有值
     */
    private String mContactId;
    /**
     * 使用系统图标，这个属性才有值
     */
    private int mImageResource;
    /**
     * 使用非系统图标，这个属性才有值
     */
    private String mImageUrl;

    private String mTitle;
    private String mContent;
    private long mTime;

    /**
     * 消息类型：0，好友申请；1，聊天；2， 不良事件
     */
    private int mType;

    public Message() {
    }

    public Message(String registerId, String contactId, int imageResource, String imageUrl, String title, String content, long time, int type) {
        mRegisterId = registerId;
        mContactId = contactId;
        mImageResource = imageResource;
        mImageUrl = imageUrl;
        mTitle = title;
        mContent = content;
        mTime = time;
        mType = type;
    }

    public String getRegisterId() {
        return mRegisterId;
    }

    public void setRegisterId(String registerId) {
        mRegisterId = registerId;
    }

    public String getContactId() {
        return mContactId;
    }

    public void setContactId(String contactId) {
        mContactId = contactId;
    }

    public int getImageResource() {
        return mImageResource;
    }

    public void setImageResource(int imageResource) {
        mImageResource = imageResource;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }
}
