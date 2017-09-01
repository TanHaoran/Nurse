package com.jerry.nurse.model;

import java.io.Serializable;

public class BindInfo implements Serializable {
    /**
     * BindCount : 0
     * Phone : 13227726101
     * QQNickName :
     * QQOpenId :
     * RegisterId :
     */

    private int BindCount;
    private String Phone;
    private String QQNickName;
    private String QQOpenId;
    private String RegisterId;
    private String WeixinNickName;
    private String WeixinOpenId;
    private String WeiboOpenId;
    private String WeiboNickName;

    public int getBindCount() {
        return BindCount;
    }

    public void setBindCount(int BindCount) {
        this.BindCount = BindCount;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getQQNickName() {
        return QQNickName;
    }

    public void setQQNickName(String QQNickName) {
        this.QQNickName = QQNickName;
    }

    public String getQQOpenId() {
        return QQOpenId;
    }

    public void setQQOpenId(String QQOpenId) {
        this.QQOpenId = QQOpenId;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String RegisterId) {
        this.RegisterId = RegisterId;
    }

    public String getWeixinNickName() {
        return WeixinNickName;
    }

    public void setWeixinNickName(String weixinNickName) {
        WeixinNickName = weixinNickName;
    }

    public String getWeixinOpenId() {
        return WeixinOpenId;
    }

    public void setWeixinOpenId(String weixinOpenId) {
        WeixinOpenId = weixinOpenId;
    }

    public String getWeiboOpenId() {
        return WeiboOpenId;
    }

    public void setWeiboOpenId(String weiboOpenId) {
        WeiboOpenId = weiboOpenId;
    }

    public String getWeiboNickName() {
        return WeiboNickName;
    }

    public void setWeiboNickName(String weiboNickName) {
        WeiboNickName = weiboNickName;
    }
}