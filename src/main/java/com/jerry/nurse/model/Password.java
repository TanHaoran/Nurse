package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/27.
 */

public class Password {

    private String RegisterId;
    private String OriginPassword;
    private String NewPassword;

    public Password(String registerId, String originPassword, String newPassword) {
        RegisterId = registerId;
        OriginPassword = originPassword;
        NewPassword = newPassword;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String registerId) {
        RegisterId = registerId;
    }

    public String getOriginPassword() {
        return OriginPassword;
    }

    public void setOriginPassword(String originPassword) {
        OriginPassword = originPassword;
    }

    public String getNewPassword() {
        return NewPassword;
    }

    public void setNewPassword(String newPassword) {
        NewPassword = newPassword;
    }
}
