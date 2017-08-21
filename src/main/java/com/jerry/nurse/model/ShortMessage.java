package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/21.
 * <p>
 * 用于查看是否请求成功的服务返回类
 */

public class ShortMessage {

    private String RegisterId;
    private String Phone;
    private String Code;
    private int Type;
    private String DeviceRegId;

    public ShortMessage(String registerId, String phone, String code, int type) {
        RegisterId = registerId;
        Phone = phone;
        Code = code;
        Type = type;
    }

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

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }

    public String getDeviceRegId() {
        return DeviceRegId;
    }

    public void setDeviceRegId(String deviceRegId) {
        DeviceRegId = deviceRegId;
    }
}
