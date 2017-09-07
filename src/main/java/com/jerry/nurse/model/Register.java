package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/25.
 * 登录/注册post传值类
 */

public class Register {

    private String Phone;

    private String Password;

    private String CountryCode;

    private String DeviceId;

    private String DeviceRegId;

    /**
     * 0不良事件，4学分，5考试，6排班
     */
    private int LoginType;

    /**
     * 学分、考试需要此字段
     */
    private String HospitalId;

    public Register(String phone, String password) {
        Phone = phone;
        Password = password;
    }

    public Register(String phone, String password, String countryCode) {
        Phone = phone;
        Password = password;
        CountryCode = countryCode;
    }

    public Register(String phone, String password, String countryCode, String deviceRegId) {
        Phone = phone;
        Password = password;
        CountryCode = countryCode;
        DeviceRegId = deviceRegId;
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

    public String getDeviceRegId() {
        return DeviceRegId;
    }

    public void setDeviceRegId(String deviceRegId) {
        DeviceRegId = deviceRegId;
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
