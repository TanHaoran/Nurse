package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/27.
 */

public class Password {

    private String RegisterId;
    private String PasswordOld;
    private String PasswordNew;
    /**
     * 0：院内账号，1：智护账号
     */
    private int Type;

    public Password(String registerId, String passwordOld, String passwordNew) {
        RegisterId = registerId;
        PasswordOld = passwordOld;
        PasswordNew = passwordNew;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String registerId) {
        RegisterId = registerId;
    }

    public String getPasswordOld() {
        return PasswordOld;
    }

    public void setPasswordOld(String passwordOld) {
        PasswordOld = passwordOld;
    }

    public String getPasswordNew() {
        return PasswordNew;
    }

    public void setPasswordNew(String passwordNew) {
        PasswordNew = passwordNew;
    }

    public int getType() {
        return Type;
    }

    public void setType(int type) {
        Type = type;
    }
}
