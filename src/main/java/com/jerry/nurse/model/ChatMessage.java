package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class ChatMessage extends DataSupport implements Serializable {
    private String mAvatar;
    private String mName;
    private String mContent;
    private long mTime;
    private String from;
    private String to;
    private boolean mIsSend;

    public final static int MSG_SEND = 0;
    public final static int MSG_RECEIVE = 1;

    public ChatMessage() {
    }

    public ChatMessage(String avatar, String name, String content, long time, boolean isSend) {
        mAvatar = avatar;
        mName = name;
        mContent = content;
        mTime = time;
        mIsSend = isSend;
    }

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
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

    public boolean isSend() {
        return mIsSend;
    }

    public void setSend(boolean send) {
        mIsSend = send;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "mAvatar='" + mAvatar + '\'' +
                ", mName='" + mName + '\'' +
                ", mContent='" + mContent + '\'' +
                ", mTime=" + mTime +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", mIsSend=" + mIsSend +
                '}';
    }
}
