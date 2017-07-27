package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/27.
 */

public class Feedback {

    private String RegisterId;
    private String Feedback;

    public Feedback(String registerId, String feedback) {
        RegisterId = registerId;
        Feedback = feedback;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String registerId) {
        RegisterId = registerId;
    }

    public String getFeedback() {
        return Feedback;
    }

    public void setFeedback(String feedback) {
        Feedback = feedback;
    }
}
