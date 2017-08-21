package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/13.
 */

public class CreateGroupResult {


    /**
     * body : {"CreateTime":"/Date(1503324961662+0800)/","GroupId":"0000000027","GroupUserCount":4,"HXGroupId":"25088821886977","HXNickName":"群聊","RegisterId":"","groupMemberList":""}
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
