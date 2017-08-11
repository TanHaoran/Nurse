package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/11.
 */

public class QrCodeResult {

    /**
     * body : {"Name":""}
     * code : 0
     * msg :
     */

    private BodyBean body;
    private int code;
    private String msg;

    public BodyBean getBody() {
        return body;
    }

    public void setBody(BodyBean body) {
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

    public static class BodyBean {
        /**
         * Name :
         */

        private String Name;

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }
    }
}
