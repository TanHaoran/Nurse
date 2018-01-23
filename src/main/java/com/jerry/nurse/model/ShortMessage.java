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
    private int temp;

    /**
     * 创建短信验证类
     *
     * @param registerId 用户注册Id
     * @param phone      用户手机号
     * @param code       验证码
     * @param type       验证类型
     */
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

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }
}
