package com.jerry.nurse.model;

import android.text.TextUtils;

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

    // 护理不良事件
    private String BLSJId;
    private String BLSJOpenId;

    // 学分
    private String XFId;
    private String XFOpenId;

    // 排班
    private String PBId;
    private String PBOpenId;

    public int getBindCount() {
        int count = 0;
        if (!TextUtils.isEmpty(getPhone())) {
            count++;
        }
        if (!TextUtils.isEmpty(getWeixinOpenId())) {
            count++;
        }
        if (!TextUtils.isEmpty(getQQOpenId())) {
            count++;
        }
        if (!TextUtils.isEmpty(getWeiboOpenId())) {
            count++;
        }
        if (!TextUtils.isEmpty(getBLSJOpenId())) {
            count++;
        }
        if (!TextUtils.isEmpty(getXFOpenId())) {
            count++;
        }
        if (!TextUtils.isEmpty(getPBOpenId())) {
            count++;
        }
        return count;
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

    public String getBLSJId() {
        return BLSJId;
    }

    public void setBLSJId(String BLSJId) {
        this.BLSJId = BLSJId;
    }

    public String getBLSJOpenId() {
        return BLSJOpenId;
    }

    public void setBLSJOpenId(String BLSJOpenId) {
        this.BLSJOpenId = BLSJOpenId;
    }

    public String getXFId() {
        return XFId;
    }

    public void setXFId(String XFId) {
        this.XFId = XFId;
    }

    public String getXFOpenId() {
        return XFOpenId;
    }

    public void setXFOpenId(String XFOpenId) {
        this.XFOpenId = XFOpenId;
    }

    public String getPBId() {
        return PBId;
    }

    public void setPBId(String PBId) {
        this.PBId = PBId;
    }

    public String getPBOpenId() {
        return PBOpenId;
    }

    public void setPBOpenId(String PBOpenId) {
        this.PBOpenId = PBOpenId;
    }
}