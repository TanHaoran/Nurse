package com.jerry.nurse.model;

import java.util.List;

/**
 * Created by Jerry on 2017/8/5.
 */

public class BannersResult {

    /**
     * body : [{"BannerId":"0000000001","BannerTime":"/Date(1501835044000+0800)/","BannerToUrl":"www.baidu.com","BannerUrl":"1.jpg","DepartmentId":"000000001","DisplayOrder":1,"HospitalId":"0000000000","IsFlag":1,"Issuer":"客服01","Type":1}]
     * code : 0
     * msg :
     */

    private int code;
    private String msg;
    private List<Banner> body;

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

    public List<Banner> getBody() {
        return body;
    }

    public void setBody(List<Banner> body) {
        this.body = body;
    }


}
