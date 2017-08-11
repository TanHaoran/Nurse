package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/8/11.
 */

public class AddFriendApply extends DataSupport {

    public static final int STATUS_SEND_ING = 0;
    public static final int STATUS_RECEIVE_ING = 1;
    public static final int STATUS_REFUSE = 2;
    public static final int STATUS_AGREE = 3;

    private String mRegisterId;
    private String mContactId;
    private String mAvatar;
    private String mNickname;
    private int mStatus;
    private long mTime;
    private String mReason;

    public String getAvatar() {
        return mAvatar;
    }

    public void setAvatar(String avatar) {
        mAvatar = avatar;
    }

    public String getNickname() {
        return mNickname;
    }

    public void setNickname(String nickname) {
        mNickname = nickname;
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

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getStatus() {
        return mStatus;
    }

    public void setStatus(int status) {
        mStatus = status;
    }

    public String getReason() {
        return mReason;
    }

    public void setReason(String reason) {
        mReason = reason;
    }
}
