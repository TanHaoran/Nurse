package com.jerry.nurse.model;

import java.util.List;

/**
 * Created by Jerry on 2017/8/7.
 */

public class HospitalResult {


    /**
     * body : [{"HospitalId":"0000000201","Name":"陕西省中医医院"},{"HospitalId":"0000000152","Name":"西安市第一医院"},{"HospitalId":"0000000158","Name":"西安市中心医院"},{"HospitalId":"0000000151","Name":"西安市第五医院"}]
     * code : 0
     * msg :
     */

    private int code;
    private String msg;
    private List<Hospital> body;

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

    public List<Hospital> getBody() {
        return body;
    }

    public void setBody(List<Hospital> body) {
        this.body = body;
    }

    public static class Hospital {
        /**
         * HospitalId : 0000000201
         * Name : 陕西省中医医院
         */

        private String HospitalId;
        private String Name;

        public String getHospitalId() {
            return HospitalId;
        }

        public void setHospitalId(String HospitalId) {
            this.HospitalId = HospitalId;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }
    }
}
