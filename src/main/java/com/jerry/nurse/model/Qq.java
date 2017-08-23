package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/2.
 */

public class Qq {

    private String RegisterId;
    private String OpenId;
    private String FigureUrl;
    private String NickName;
    private String Province;
    private String City;
    private String Gender;
    private String AccessToken;
    private String Expires;
    private String DeviceId;
    private String DeviceRegId;

    public String getOpenId() {
        return OpenId;
    }

    public void setOpenId(String openId) {
        OpenId = openId;
    }

    public String getFigureUrl() {
        return FigureUrl;
    }

    public void setFigureUrl(String figureUrl) {
        FigureUrl = figureUrl;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String nickName) {
        NickName = nickName;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String province) {
        Province = province;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }

    public String getAccessToken() {
        return AccessToken;
    }

    public void setAccessToken(String accessToken) {
        AccessToken = accessToken;
    }

    public String getExpires() {
        return Expires;
    }

    public void setExpires(String expires) {
        Expires = expires;
    }

    public String getDeviceId() {
        return DeviceId;
    }

    public void setDeviceId(String deviceId) {
        DeviceId = deviceId;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String registerId) {
        RegisterId = registerId;
    }

    public String getDeviceRegId() {
        return DeviceRegId;
    }

    public void setDeviceRegId(String deviceRegId) {
        DeviceRegId = deviceRegId;
    }
}
