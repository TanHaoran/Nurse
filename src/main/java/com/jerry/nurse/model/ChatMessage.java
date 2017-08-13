package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class ChatMessage extends DataSupport implements Serializable {
    private long mTime;
    private String mFrom;
    private String mTo;
    private boolean mIsSend;
    private boolean mIsGroup;
    /**
     * 如果是文字消息，这个属性才有值
     */
    private String mContent;
    /**
     * 如果是语音消息，这个属性才有值
     */
    private float mSecond;
    /**
     * 如果是语音消息或者图片消息，这个属性才有值
     */
    private String path;

    public final static int MSG_SEND = 0;
    public final static int MSG_RECEIVE = 1;

    public ChatMessage() {
    }

    public ChatMessage(long time, String from, String to, boolean isSend, String content, float second, String path) {
        mTime = time;
        mFrom = from;
        mTo = to;
        mIsSend = isSend;
        mContent = content;
        mSecond = second;
        this.path = path;
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
}
