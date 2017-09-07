package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/4.
 */

public class ThirdPartInfo {


    public static final int TYPE_CELLPHONE = 0;
    public static final int TYPE_QQ = 1;
    public static final int TYPE_WE_CHAT = 2;
    public static final int TYPE_MICRO_BLOG = 3;
    public static final int TYPE_EVENT_REPORT = 4;
    public static final int TYPE_CREDIT = 5;
    public static final int TYPE_SCHEDULE = 6;

    private String RegisterId;
    private String Phone;
    private Qq QQData;
    private WeChat WXData;
    private MicroBlog WBData;
    private String LoginName;
    private String Password;
    private String HospitalId;

    /**
     * 0手机号、1qq、2微信、3微博、4不良事件、5学分、6排班
     */
    private int LoginType;

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String registerId) {
        RegisterId = registerId;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public Qq getQQData() {
        return QQData;
    }

    public void setQQData(Qq QQData) {
        this.QQData = QQData;
    }

    public WeChat getWXData() {
        return WXData;
    }

    public void setWXData(WeChat WXData) {
        this.WXData = WXData;
    }

    public MicroBlog getWBData() {
        return WBData;
    }

    public void setWBData(MicroBlog WBData) {
        this.WBData = WBData;
    }

    public String getLoginName() {
        return LoginName;
    }

    public void setLoginName(String loginName) {
        LoginName = loginName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getHospitalId() {
        return HospitalId;
    }

    public void setHospitalId(String hospitalId) {
        HospitalId = hospitalId;
    }

    public int getLoginType() {
        return LoginType;
    }

    public void setLoginType(int loginType) {
        LoginType = loginType;
    }
}
