package com.jerry.nurse.model;

import com.jerry.nurse.util.CellphoneContact;

import java.util.List;

/**
 * Created by Jerry on 2017/8/12.
 */

public class CellphoneContactResult {

    /**
     * body : [{"Avatar":"","Name":"","NickName":"","Phone":"13002909621","RegisterId":"0000000426","status":1},{"Avatar":"01bca757ef140ca84a0d304fca0d15time20170811102624.gif","Name":"","NickName":"","Phone":"18729515058","RegisterId":"0000000799","status":1},{"Avatar":"","Name":"","NickName":"","Phone":"1300209620","RegisterId":"","status":0}]
     * code : 0
     * msg :
     */

    private int code;
    private String msg;
    private List<CellphoneContact> body;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<CellphoneContact> getBody() {
        return body;
    }

    public void setBody(List<CellphoneContact> body) {
        this.body = body;
    }


    private String Avatar;
    private String Name;
    private String NickName;
    private String Phone;
    private String RegisterId;
    /**
     *
     */
    private int status;

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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}

