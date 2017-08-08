package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/7.
 */

public class VersionResult {

    /**
     * body : {"ReleaseTime":"/Date(1501344000000+0800)/","ReleaseUrl":"测试地址","Version":"0.5","VersionId":"0000000001"}
     * code : 0
     * msg :
     */

    private Version body;
    private int code;
    private String msg;

    public Version getBody() {
        return body;
    }

    public void setBody(Version body) {
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

    public static class Version {
        /**
         * ReleaseTime : /Date(1501344000000+0800)/
         * ReleaseUrl : 测试地址
         * Version : 0.5
         * VersionId : 0000000001
         */

        private String ReleaseTime;
        private String ReleaseUrl;
        private String Version;
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

        public String getVersion() {
            return Version;
        }

        public void setVersion(String Version) {
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
