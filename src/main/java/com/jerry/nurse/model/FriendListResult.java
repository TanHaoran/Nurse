package com.jerry.nurse.model;

import java.util.List;

/**
 * Created by Jerry on 2017/8/10.
 */

public class FriendListResult {

    /**
     * body : [{"Avatar":"","DepartmentName":"","HospitalName":"","IsFriend":false,"Name":"","NickName":"jerrytan","Phone":"13002909620","RegisterId":"0000000430","Remark":"","Role":0,"Sex":""}]
     * code : 0
     * msg :
     */

    private int code;
    private String msg;
    private List<Contact> body;

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

    public List<Contact> getBody() {
        return body;
    }

    public void setBody(List<Contact> body) {
        this.body = body;
    }
}
