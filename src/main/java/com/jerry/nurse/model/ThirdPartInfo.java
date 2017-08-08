package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/4.
 */

public class ThirdPartInfo {

    private String RegisterId;
    private String Phone;
    private Qq QQData;

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
}
