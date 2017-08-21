package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/25.
 */

public class Register {

    private String Phone;

    private String Password;

    private String CountryCode;

    private String DeviceId;

    public Register(String phone, String password) {
        Phone = phone;
        Password = password;
    }

    public Register(String phone, String password, String countryCode) {
        Phone = phone;
        Password = password;
        CountryCode = countryCode;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String countryCode) {
        CountryCode = countryCode;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }
}
