package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/9.
 */

public class ContactDetailResult {


    /**
     * body : {"Avatar":"Screen-20170622064742time20170809054355.png","DepartmentName":"","HospitalName":"","IsFriend":false,"Name":"刘备","NickName":"灰太狼","Phone":"18709269196","Remark":"","Role":0,"Sex":"男"}
     * code : 0
     * msg :
     */

    private Contact body;
    private int code;
    private String msg;

    public Contact getBody() {
        return body;
    }

    public void setBody(Contact body) {
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
