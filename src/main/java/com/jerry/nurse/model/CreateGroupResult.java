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

    private GroupInfo body;
    private int code;
    private String msg;

    public GroupInfo getBody() {
        return body;
    }

    public void setBody(GroupInfo body) {
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

}
