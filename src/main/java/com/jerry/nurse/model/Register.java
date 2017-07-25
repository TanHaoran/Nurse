package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/25.
 */

public class Register {

    private String Phone;

    private String password;

    public Register(String phone, String password) {
        Phone = phone;
        this.password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
