package com.jerry.nurse.model;

public class Country {
    /**
     * CountryCode : +86
     * CountryId : 0000000001
     * CountryName : 中国
     * DisplayOrder : 1
     * IsFlag : 1
     */

    private String CountryCode;
    private String CountryId;
    private String CountryName;
    private int DisplayOrder;
    private String IsFlag;

    public String getCountryCode() {
        return CountryCode;
    }

    public void setCountryCode(String CountryCode) {
        this.CountryCode = CountryCode;
    }

    public String getCountryId() {
        return CountryId;
    }

    public void setCountryId(String CountryId) {
        this.CountryId = CountryId;
    }

    public String getCountryName() {
        return CountryName;
    }

    public void setCountryName(String CountryName) {
        this.CountryName = CountryName;
    }

    public int getDisplayOrder() {
        return DisplayOrder;
    }

    public void setDisplayOrder(int DisplayOrder) {
        this.DisplayOrder = DisplayOrder;
    }

    public String getIsFlag() {
        return IsFlag;
    }

    public void setIsFlag(String IsFlag) {
        this.IsFlag = IsFlag;
    }
}