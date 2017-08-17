package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class Banner extends DataSupport implements Serializable {
    /**
     * BannerId : 0000000001
     * BannerTime : /Date(1501835044000+0800)/
     * BannerToUrl : www.baidu.com
     * BannerUrl : 1.jpg
     * DepartmentId : 000000001
     * DisplayOrder : 1
     * HospitalId : 0000000000
     * IsFlag : 1
     * Issuer : 客服01
     * Type : 1
     */

    private int id;
    private String BannerId;
    private String BannerTime;
    private String BannerToUrl;
    private String BannerUrl;
    private String DepartmentId;
    private int DisplayOrder;
    private String HospitalId;
    private int IsFlag;
    private String Issuer;
    private int Type;
    private String Title;

    public String getBannerId() {
        return BannerId;
    }

    public void setBannerId(String BannerId) {
        this.BannerId = BannerId;
    }

    public String getBannerTime() {
        return BannerTime;
    }

    public void setBannerTime(String BannerTime) {
        this.BannerTime = BannerTime;
    }

    public String getBannerToUrl() {
        return BannerToUrl;
    }

    public void setBannerToUrl(String BannerToUrl) {
        this.BannerToUrl = BannerToUrl;
    }

    public String getBannerUrl() {
        return BannerUrl;
    }

    public void setBannerUrl(String BannerUrl) {
        this.BannerUrl = BannerUrl;
    }

    public String getDepartmentId() {
        return DepartmentId;
    }

    public void setDepartmentId(String DepartmentId) {
        this.DepartmentId = DepartmentId;
    }

    public int getDisplayOrder() {
        return DisplayOrder;
    }

    public void setDisplayOrder(int DisplayOrder) {
        this.DisplayOrder = DisplayOrder;
    }

    public String getHospitalId() {
        return HospitalId;
    }

    public void setHospitalId(String HospitalId) {
        this.HospitalId = HospitalId;
    }

    public int getIsFlag() {
        return IsFlag;
    }

    public void setIsFlag(int IsFlag) {
        this.IsFlag = IsFlag;
    }

    public String getIssuer() {
        return Issuer;
    }

    public void setIssuer(String Issuer) {
        this.Issuer = Issuer;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }
}