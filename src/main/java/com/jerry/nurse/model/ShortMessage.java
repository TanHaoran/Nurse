package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/21.
 *
 * 用于查看是否请求成功的服务返回类
 */

public class ShortMessage {

    private String Phone;
    private String Code;

    public ShortMessage(String phone, String code) {
        Phone = phone;
        Code = code;
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
}
