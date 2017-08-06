package com.jerry.nurse.model;

import java.util.List;

/**
 * Created by Jerry on 2017/8/5.
 */

public class AnnouncementsResult {

    /**
     * body : [{"Content":"注意休息","DepartmentId":"0000000001","DisplayOrder":2,"HospitalId":"0000000001","IsFlag":1,"IsVital":1,"Issuer":"护士01","NoticeId":"0000000002","NoticeTime":"/Date(1501909538000+0800)/","Title":"眼部保养","Type":2}]
     * code : 0
     * msg :
     */

    private int code;
    private String msg;
    private List<Announcement> body;

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

    public List<Announcement> getBody() {
        return body;
    }

    public void setBody(List<Announcement> body) {
        this.body = body;
    }


}
