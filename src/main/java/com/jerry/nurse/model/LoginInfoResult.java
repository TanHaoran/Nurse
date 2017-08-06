package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/6.
 */

public class LoginInfoResult {


    /**
     * body : {"Avatar":"","DepartmentId":"","DepartmentName":"","HospitalId":"","HospitalName":"","Name":"","NickName":"","PStatus":0,"QStatus":0,"RegisterId":""}
     * code : 0
     * msg :
     */

    private LoginInfo body;
    private int code;
    private String msg;

    public LoginInfo getBody() {
        return body;
    }

    public void setBody(LoginInfo body) {
        this.body = body;
    }

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

}
