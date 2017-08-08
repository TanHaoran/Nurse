package com.jerry.nurse.model;

import java.io.Serializable;

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

    public static class BindInfo implements Serializable {
        /**
         * BindCount : 0
         * Phone : 13227726101
         * QQNickName :
         * QQOpenId :
         * RegisterId :
         */

        private int BindCount;
        private String Phone;
        private String QQNickName;
        private String QQOpenId;
        private String RegisterId;

        public int getBindCount() {
            return BindCount;
        }

        public void setBindCount(int BindCount) {
            this.BindCount = BindCount;
        }

        public String getPhone() {
            return Phone;
        }

        public void setPhone(String Phone) {
            this.Phone = Phone;
        }

        public String getQQNickName() {
            return QQNickName;
        }

        public void setQQNickName(String QQNickName) {
            this.QQNickName = QQNickName;
        }

        public String getQQOpenId() {
            return QQOpenId;
        }

        public void setQQOpenId(String QQOpenId) {
            this.QQOpenId = QQOpenId;
        }

        public String getRegisterId() {
            return RegisterId;
        }

        public void setRegisterId(String RegisterId) {
            this.RegisterId = RegisterId;
        }

        @Override
        public String toString() {
            return "BindInfo{" +
                    "BindCount=" + BindCount +
                    ", Phone='" + Phone + '\'' +
                    ", QQNickName='" + QQNickName + '\'' +
                    ", QQOpenId='" + QQOpenId + '\'' +
                    ", RegisterId='" + RegisterId + '\'' +
                    '}';
        }
    }
}
