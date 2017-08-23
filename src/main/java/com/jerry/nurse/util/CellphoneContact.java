package com.jerry.nurse.util;

import android.text.TextUtils;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

/**
 * 手机通讯录联系人
 */
public class CellphoneContact extends BaseIndexPinyinBean {

    public static final int TYPE_NOT_USAGE = 0;
    public static final int TYPE_NOT_FRIEND = 1;
    public static final int TYPE_IS_FRIEND = 2;

    private String Avatar;
    private String Name;
    private String NickName;
    private String Phone;
    private String RegisterId;
    /**
     * 0：没有使用软件；1：使用软件，但不是好友；2：使用软件，而且是好友
     */
    private int status;

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String Avatar) {
        this.Avatar = Avatar;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String RegisterId) {
        this.RegisterId = RegisterId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Override
    public String getTarget() {
        if (!TextUtils.isEmpty(NickName)) {
            return NickName;
        } else if (!TextUtils.isEmpty(Name)) {
            return Name;
        } else if (!TextUtils.isEmpty(Phone)) {
            return Phone;
        }
        return Name;
    }
}