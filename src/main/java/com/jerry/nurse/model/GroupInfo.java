package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

import java.util.List;

public class GroupInfo extends DataSupport{

    /**
     * HXGroupId : 24346151157761
     * HXNickName : 群聊
     * groupMemberList : [{"Avatar":"01bca757ef140ca84a0d304fca0d15time20170811102624.gif","DepartmentName":"","FriendId":"","HospitalName":"","IsFriend":true,"MyId":"0000000799","Name":"","NickName":"白衣天使","Phone":"","Remark":"","Role":0,"Sex":""},{"Avatar":"-1749197750time20170810113416.jpg","DepartmentName":"","FriendId":"","HospitalName":"","IsFriend":false,"MyId":"0000000430","Name":"","NickName":"jerrytan","Phone":"","Remark":"","Role":0,"Sex":""},{"Avatar":"IMG_20170430_103937time20170810113605.jpg","DepartmentName":"","FriendId":"","HospitalName":"","IsFriend":true,"MyId":"0000000429","Name":"","NickName":"灰大狼","Phone":"","Remark":"","Role":0,"Sex":""}]
     */

    private String RegisterId;
    private String HXGroupId;
    private String HXNickName;
    private List<Contact> groupMemberList;

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String registerId) {
        RegisterId = registerId;
    }

    public String getHXGroupId() {
        return HXGroupId;
    }

    public void setHXGroupId(String HXGroupId) {
        this.HXGroupId = HXGroupId;
    }

    public String getHXNickName() {
        return HXNickName;
    }

    public void setHXNickName(String HXNickName) {
        this.HXNickName = HXNickName;
    }

    public List<Contact> getGroupMemberList() {
        return groupMemberList;
    }

    public void setGroupMemberList(List<Contact> groupMemberList) {
        this.groupMemberList = groupMemberList;
    }

}