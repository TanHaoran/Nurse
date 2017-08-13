package com.jerry.nurse.model;

import java.util.List;

/**
 * Created by Jerry on 2017/8/13.
 */

public class GroupListResult {


    /**
     * body : [{"HXGroupId":"24346151157761","HXNickName":"群聊","groupList":[{"Avatar":"01bca757ef140ca84a0d304fca0d15time20170811102624.gif","Name":"","NickName":"白衣天使","Phone":"","RegisterId":"0000000799","status":0},{"Avatar":"-1749197750time20170810113416.jpg","Name":"","NickName":"jerrytan","Phone":"","RegisterId":"0000000430","status":0},{"Avatar":"IMG_20170430_103937time20170810113605.jpg","Name":"","NickName":"灰大狼","Phone":"","RegisterId":"0000000429","status":0}]},{"HXGroupId":"24350954684418","HXNickName":"群聊","groupList":[{"Avatar":"01bca757ef140ca84a0d304fca0d15time20170811102624.gif","Name":"","NickName":"白衣天使","Phone":"","RegisterId":"0000000799","status":0},{"Avatar":"526358_龙猫9time20170810112147.jpg","Name":"","NickName":"Aa_8@-王","Phone":"","RegisterId":"0000000431","status":0},{"Avatar":"-1749197750time20170810113416.jpg","Name":"","NickName":"jerrytan","Phone":"","RegisterId":"0000000430","status":0}]}]
     * code : 0
     * msg :
     */

    private int code;
    private String msg;
    private List<GroupInfo> body;

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

    public List<GroupInfo> getBody() {
        return body;
    }

    public void setBody(List<GroupInfo> body) {
        this.body = body;
    }


}
