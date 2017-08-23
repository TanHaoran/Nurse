package com.jerry.nurse.model;

import android.text.TextUtils;

import org.litepal.crud.DataSupport;

import static com.jerry.nurse.constant.ServiceConstant.AVATAR_ADDRESS;

/**
 * Created by Jerry on 2017/8/11.
 */

public class ContactInfo extends DataSupport {

    private String mAvatar;
    private String mName;
    private String mNickName;
    private String mCellphone;
    private String mRemark;
    private String mRegisterId;
    private boolean mIsFriend;

    public String getAvatar() {
        if (!TextUtils.isEmpty(mAvatar)) {
            if (mAvatar.startsWith("http")) {
                return mAvatar;
            } else {
                return AVATAR_ADDRESS + mAvatar;
            }
        }
        return "";
    }

    /**
     * 获取屏幕上显示的内容
     *
     * @return
     */
    public String getDisplayName() {
        if (!TextUtils.isEmpty(mRemark)) {
            return mRemark;
        } else if (!TextUtils.isEmpty(mNickName)) {
            return mNickName;
        } else if (!TextUtils.isEmpty(mName)) {
            return mName;
        }else if (!TextUtils.isEmpty(mCellphone)) {
            return mCellphone;
        } else {
            return "";
        }
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

    public String getNickName() {
        return mNickName;
    }

    public void setNickName(String nickName) {
        mNickName = nickName;
    }

    public String getCellphone() {
        return mCellphone;
    }

    public void setCellphone(String cellphone) {
        mCellphone = cellphone;
    }

    public String getRemark() {
        return mRemark;
    }

    public void setRemark(String remark) {
        mRemark = remark;
    }

    public String getRegisterId() {
        return mRegisterId;
    }

    public void setRegisterId(String registerId) {
        mRegisterId = registerId;
    }

    public boolean isFriend() {
        return mIsFriend;
    }

    public void setFriend(boolean friend) {
        mIsFriend = friend;
    }
}
