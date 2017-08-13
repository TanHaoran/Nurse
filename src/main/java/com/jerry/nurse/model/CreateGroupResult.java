package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/13.
 */

public class CreateGroupResult {


    /**
     * body : {"CreateTime":"/Date(1502610131677+0800)/","CreaterId":"0000000430","Descg":"0000000430在2017/8/13 15:42:11创建的群","GroupId":"0000000013","GroupNickName":"群聊","IsFlag":1,"MaxGroupCount":100,"UserCount":2}
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
         * CreateTime : /Date(1502610131677+0800)/
         * CreaterId : 0000000430
         * Descg : 0000000430在2017/8/13 15:42:11创建的群
         * GroupId : 0000000013
         * GroupNickName : 群聊
         * IsFlag : 1
         * MaxGroupCount : 100
         * UserCount : 2
         */

        private String CreateTime;
        private String CreaterId;
        private String Descg;
        private String GroupId;
        private String GroupNickName;
        private int IsFlag;
        private int MaxGroupCount;
        private int UserCount;

        public String getCreateTime() {
            return CreateTime;
        }

        public void setCreateTime(String CreateTime) {
            this.CreateTime = CreateTime;
        }

        public String getCreaterId() {
            return CreaterId;
        }

        public void setCreaterId(String CreaterId) {
            this.CreaterId = CreaterId;
        }

        public String getDescg() {
            return Descg;
        }

        public void setDescg(String Descg) {
            this.Descg = Descg;
        }

        public String getGroupId() {
            return GroupId;
        }

        public void setGroupId(String GroupId) {
            this.GroupId = GroupId;
        }

        public String getGroupNickName() {
            return GroupNickName;
        }

        public void setGroupNickName(String GroupNickName) {
            this.GroupNickName = GroupNickName;
        }

        public int getIsFlag() {
            return IsFlag;
        }

        public void setIsFlag(int IsFlag) {
            this.IsFlag = IsFlag;
        }

        public int getMaxGroupCount() {
            return MaxGroupCount;
        }

        public void setMaxGroupCount(int MaxGroupCount) {
            this.MaxGroupCount = MaxGroupCount;
        }

        public int getUserCount() {
            return UserCount;
        }

        public void setUserCount(int UserCount) {
            this.UserCount = UserCount;
        }
    }
}
