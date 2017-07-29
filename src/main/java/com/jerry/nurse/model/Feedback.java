package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/27.
 */

public class Feedback {

    private String RegisterId;
    private String Content;

    public Feedback(String registerId, String content) {
        RegisterId = registerId;
        Content = content;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String registerId) {
        RegisterId = registerId;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
