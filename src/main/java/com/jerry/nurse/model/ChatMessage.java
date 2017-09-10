package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class ChatMessage extends DataSupport implements Serializable {

    private String mId;
    private long mTime;
    private String mFrom;
    private String mTo;
    private boolean mIsSend;
    private boolean mIsGroup;
    /**
     * 消息类型：0，文字消息；1，图片消息；2，语音消息
     */
    private int mType;
    /**
     * 如果是文字消息，这个属性才有值
     */
    private String mContent;
    /**
     * 语音消息时长
     */
    private float mSecond;
    /**
     * 语音消息路径
     */
    private String path;

    /**
     * 图片消息本地路径
     */
    private String localUrl;

    /**
     * 图片消息远程路径
     */
    private String remoteUrl;

    /**
     * 这条消息是否已读
     */
    private boolean mRead = false;

    public final static int MSG_SEND = 0;
    public final static int MSG_RECEIVE = 1;

    public static final int TYPE_TXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_VOICE = 2;

    public ChatMessage() {
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getFrom() {
        return mFrom;
    }

    public void setFrom(String from) {
        mFrom = from;
    }

    public String getTo() {
        return mTo;
    }

    public void setTo(String to) {
        mTo = to;
    }

    public boolean isSend() {
        return mIsSend;
    }

    public void setSend(boolean send) {
        mIsSend = send;
    }

    public float getSecond() {
        return mSecond;
    }

    public void setSecond(float second) {
        mSecond = second;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        mContent = content;
    }

    public boolean isGroup() {
        return mIsGroup;
    }

    public void setGroup(boolean group) {
        mIsGroup = group;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        this.localUrl = localUrl;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public boolean isRead() {
        return mRead;
    }

    public void setRead(boolean read) {
        mRead = read;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }
}
