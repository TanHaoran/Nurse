package com.jerry.nurse.model;

public class ChatMessage {
    private int mAvatar;
    private String mName;
    private String mContent;
    private String mTime;
    private boolean mIsSend;

    public final static int MSG_SEND = 0;
    public final static int MSG_RECEIVE = 1;

    public ChatMessage() {
    }

    public ChatMessage(int avatar, String name, String content, String time, boolean isSend) {
        mAvatar = avatar;
        mName = name;
        mContent = content;
        mTime = time;
        mIsSend = isSend;
    }

    public int getAvatar() {
        return mAvatar;
    }

    public void setAvatar(int avatar) {
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

    public String getTime() {
        return mTime;
    }

    public void setTime(String time) {
        mTime = time;
    }

    public boolean isSend() {
        return mIsSend;
    }

    public void setSend(boolean send) {
        mIsSend = send;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "mAvatar=" + mAvatar +
                ", mName='" + mName + '\'' +
                ", mContent='" + mContent + '\'' +
                ", mTime='" + mTime + '\'' +
                ", mIsSend=" + mIsSend +
                '}';
    }
}
