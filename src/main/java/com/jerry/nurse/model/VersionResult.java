package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/7.
 */

public class VersionResult {


    /**
     * body : {"ReleaseTime":"/Date(1503386626000+0800)/","ReleaseUrl":"Nurse_v0.51.apk","UpdateContent":"1注册好了\\n2登陆好了","Version":0.51,"VersionId":"0000000002"}
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
         * ReleaseTime : /Date(1503386626000+0800)/
         * ReleaseUrl : Nurse_v0.51.apk
         * UpdateContent : 1注册好了\n2登陆好了
         * Version : 0.51
         * VersionId : 0000000002
         */

        private String ReleaseTime;
        private String ReleaseUrl;
        private String UpdateContent;
        private double Version;
        private String VersionId;

        public String getReleaseTime() {
            return ReleaseTime;
        }

        public void setReleaseTime(String ReleaseTime) {
            this.ReleaseTime = ReleaseTime;
        }

        public String getReleaseUrl() {
            return ReleaseUrl;
        }

        public void setReleaseUrl(String ReleaseUrl) {
            this.ReleaseUrl = ReleaseUrl;
        }

        public String getUpdateContent() {
            return UpdateContent;
        }

        public void setUpdateContent(String UpdateContent) {
            this.UpdateContent = UpdateContent;
        }

        public double getVersion() {
            return Version;
        }

        public void setVersion(double Version) {
            this.Version = Version;
        }

        public String getVersionId() {
            return VersionId;
        }

        public void setVersionId(String VersionId) {
            this.VersionId = VersionId;
        }
    }
}
