package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/16.
 */

public class GroupInfoResult {


    /**
     * body : {"GroupId":"","GroupUserCount":3,"HXGroupId":"","HXNickName":"群聊","RegisterId":"","groupMemberList":[{"Avatar":"-1749197750time20170810113416.jpg","DepartmentName":"","FriendId":"0000000430","HospitalName":"","IsFriend":false,"MyId":"0000000430","Name":"浩然","NickName":"jerrytan","Phone":"13002909620","Remark":"","Role":0,"Sex":"男"},{"Avatar":"526358_龙猫9time20170810112147.jpg","DepartmentName":null,"FriendId":"0000000431","HospitalName":null,"IsFriend":false,"MyId":"0000000430","Name":null,"NickName":"Aa_8@-王","Phone":"177492946","Remark":null,"Role":0,"Sex":"女"},{"Avatar":"01bca757ef140ca84a0d304fca0d15time20170811102624.gif","DepartmentName":null,"FriendId":"0000000799","HospitalName":null,"IsFriend":false,"MyId":"0000000430","Name":null,"NickName":"白衣天使","Phone":"1872958","Remark":null,"Role":0,"Sex":"女"}]}
     * code : 0
     * msg : null
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
