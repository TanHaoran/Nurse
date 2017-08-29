package com.jerry.nurse.model;

import java.util.List;

/**
 * Created by Jerry on 2017/8/29.
 */

public class WeChatUserInfoResult {

    /**
     * openid : oA1jk0u4mO35BZUqnD7TckWp5IOk
     * nickname : 林山夕风
     * sex : 1
     * language : zh_CN
     * city :
     * province : Tokyo
     * country : JP
     * headimgurl : http://wx.qlogo.cn/mmopen/Q3auHgzwzM76VHKwsw7NIdVfZlvAYXbVYTyibnm3asiblkKnE3yiahmdl1jwgzWke052PUarLVu7ibBSjtVBjbWe8w/0
     * privilege : []
     * unionid : oGNPF0q13hCQyV43lCmirbcBnSaI
     */

    private String openid;
    private String nickname;
    private int sex;
    private String language;
    private String city;
    private String province;
    private String country;
    private String headimgurl;
    private String unionid;
    private List<?> privilege;

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public List<?> getPrivilege() {
        return privilege;
    }

    public void setPrivilege(List<?> privilege) {
        this.privilege = privilege;
    }
}
