package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/22.
 */

public class Result {

    /**
     * code : 1
     * msg : 失败
     * body : null
     */

    private int code;
    private String msg;
    private Object body;

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

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
