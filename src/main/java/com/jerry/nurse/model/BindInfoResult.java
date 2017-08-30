package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/6.
 */

public class BindInfoResult {


    /**
     * body : {"BindCount":0,"Phone":"13227726101","QQNickName":"","QQOpenId":"","RegisterId":""}
     * code : 0
     * msg :
     */

    private BindInfo body;
    private int code;
    private String msg;

    public BindInfo getBody() {
        return body;
    }

    public void setBody(BindInfo body) {
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
