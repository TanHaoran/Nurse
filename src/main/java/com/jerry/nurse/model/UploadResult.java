package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/7.
 */

public class UploadResult {

    /**
     * body : {"Filename":"QQ截图20170731102911time20170807022240.png"}
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
         * Filename : QQ截图20170731102911time20170807022240.png
         */

        private String Filename;

        public String getFilename() {
            return Filename;
        }

        public void setFilename(String Filename) {
            this.Filename = Filename;
        }
    }
}
