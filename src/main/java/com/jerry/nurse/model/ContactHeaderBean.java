package com.jerry.nurse.model;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import java.util.List;


public class ContactHeaderBean extends BaseIndexPinyinBean {
    private List<Contact> cityList;
    //悬停ItemDecoration显示的Tag
    private String suspensionTag;

    public ContactHeaderBean() {
    }

    public ContactHeaderBean(List<Contact> cityList, String suspensionTag, String indexBarTag) {
        this.cityList = cityList;
        this.suspensionTag = suspensionTag;
        this.setBaseIndexTag(indexBarTag);
    }

    public List<Contact> getCityList() {
        return cityList;
    }

    public ContactHeaderBean setCityList(List<Contact> cityList) {
        this.cityList = cityList;
        return this;
    }

    public ContactHeaderBean setSuspensionTag(String suspensionTag) {
        this.suspensionTag = suspensionTag;
        return this;
    }

    @Override
    public String getTarget() {
        return null;
    }

    @Override
    public boolean isNeedToPinyin() {
        return false;
    }

    @Override
    public String getSuspensionTag() {
        return suspensionTag;
    }


}
