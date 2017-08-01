package com.jerry.nurse.model;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

/**
 * Created by Jerry on 2017/7/18.
 */

public class Contact extends BaseIndexPinyinBean {

    private String RegisterId;
    private String NickName;
    private String Name;
    private String Avatar;
    private String Phone;

    public Contact() {
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String registerId) {
        RegisterId = registerId;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String avatar) {
        Avatar = avatar;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    @Override
    public String getTarget() {
        if (NickName == null) {
            return "";
        }
        return NickName;
    }

}
