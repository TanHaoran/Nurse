package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/7/16.
 */

public class User extends DataSupport {


    /**
     * Avatar :
     * Name :
     * NickName :
     * Password :
     * Phone : 13002909620
     * RegisterId : 0000000002
     */

    private String Avatar;
    private String Name;
    private String NickName;
    private String Password;
    private String Phone;
    private String RegisterId;

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String Avatar) {
        this.Avatar = Avatar;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String RegisterId) {
        this.RegisterId = RegisterId;
    }
}
