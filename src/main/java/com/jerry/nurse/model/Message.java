package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * Created by Jerry on 2017/8/10.
 */

public class Message extends DataSupport implements Serializable {

    public static final int TYPE_ADD_FRIEND_APPLY = 0;
    public static final int TYPE_CHAT = 1;

    private String mRegisterId;
    private String mContactId;
    private int mImageResource;
    private String mImageUrl;
    private String mTitle;
    private String mContent;
    private long mTime;

    /**
     * 消息类型：0，好友申请；1，聊天
     */
    private int mType;

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
}
