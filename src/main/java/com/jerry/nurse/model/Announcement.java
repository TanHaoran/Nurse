package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

import java.io.Serializable;

public class Announcement extends DataSupport implements Serializable {
    /**
     * Content : 注意休息
     * DepartmentId : 0000000001
     * DisplayOrder : 2
     * HospitalId : 0000000001
     * IsFlag : 1
     * IsVital : 1
     * Issuer : 护士01
     * NoticeId : 0000000002
     * NoticeTime : /Date(1501909538000+0800)/
     * Title : 眼部保养
     * Type : 2
     */

    private int id;
    private String Content;
    private String DepartmentId;
    private int DisplayOrder;
    private String HospitalId;
    private int IsFlag;
    private int IsVital;
    private String Issuer;
    private String NoticeId;
    private String NoticeTime;
    private String Title;
    private int Type;
    private String Agency;


    public String getContent() {
        return Content;
    }

    public void setContent(String Content) {
        this.Content = Content;
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

    public int getIsVital() {
        return IsVital;
    }

    public void setIsVital(int IsVital) {
        this.IsVital = IsVital;
    }

    public String getIssuer() {
        return Issuer;
    }

    public void setIssuer(String Issuer) {
        this.Issuer = Issuer;
    }

    public String getNoticeId() {
        return NoticeId;
    }

    public void setNoticeId(String NoticeId) {
        this.NoticeId = NoticeId;
    }

    public String getNoticeTime() {
        return NoticeTime;
    }

    public void setNoticeTime(String NoticeTime) {
        this.NoticeTime = NoticeTime;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String Title) {
        this.Title = Title;
    }

    public int getType() {
        return Type;
    }

    public void setType(int Type) {
        this.Type = Type;
    }

    public String getAgency() {
        return Agency;
    }

    public void setAgency(String agency) {
        Agency = agency;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "id=" + id +
                ", Content='" + Content + '\'' +
                ", DepartmentId='" + DepartmentId + '\'' +
                ", DisplayOrder=" + DisplayOrder +
                ", HospitalId='" + HospitalId + '\'' +
                ", IsFlag=" + IsFlag +
                ", IsVital=" + IsVital +
                ", Issuer='" + Issuer + '\'' +
                ", NoticeId='" + NoticeId + '\'' +
                ", NoticeTime='" + NoticeTime + '\'' +
                ", Title='" + Title + '\'' +
                ", Type=" + Type +
                ", Agency='" + Agency + '\'' +
                '}';
    }
}