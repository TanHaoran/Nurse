package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/21.
 *
 * 用于查看是否请求成功的服务返回类
 */

public class ServiceResult {


    /**
     * code : 1
     * msg : statusCode=112310;statusMsg=【短信】应用未上线，模板短信接收号码外呼受限;data=null;
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
