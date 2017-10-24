package com.jerry.nurse.model;

import java.util.List;

/**
 * Created by Jerry on 2017/10/23.
 */

public class CreditResult {


    /**
     * body : [{"Name":"理论","Time":"/Date(1469980800000+0800)/","Type":"考核学分","score":1.5},{"Name":"理论","Time":"/Date(1480262400000+0800)/","Type":"考核学分","score":1.5}]
     * code : 0
     * msg :
     */

    private int code;
    private String msg;
    private List<CreditDetail> body;

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

    public List<CreditDetail> getBody() {
        return body;
    }

    public void setBody(List<CreditDetail> body) {
        this.body = body;
    }

}
