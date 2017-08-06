package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/6.
 */

public class UserInfoResult {

    /**
     * body : {"Avatar":"","Birthday":"/Date(-2209017600000+0800)/","DepartmentId":"","DepartmentName":"","EmployeeId":"","FirstJobTime":"/Date(-2209017600000+0800)/","HospitalId":"","HospitalName":"","Name":"","NickName":"","PCertificateId":"","QCertificateId":"","RegisterId":"","Sex":""}
     * code : 1
     * msg :
     */

    private UserInfo body;
    private int code;
    private String msg;

    public UserInfo getBody() {
        return body;
    }

    public void setBody(UserInfo body) {
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
