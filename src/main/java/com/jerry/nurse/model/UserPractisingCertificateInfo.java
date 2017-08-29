package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/7/28.
 */

public class UserPractisingCertificateInfo extends DataSupport {

    /**
     * BirthDate : /Date(-2209017600000+0800)/
     * CertificateAuthority :
     * CertificateDate : /Date(-2209017600000+0800)/
     * CountryResult :
     * FirstRegisterDate : /Date(-2209017600000+0800)/
     * IDCard :
     * LastRegisterDate : /Date(-2209017600000+0800)/
     * Picture1 :
     * Picture2 :
     * Picture3 :
     * Picture4 :
     * PracticeAddress :
     * PracticeCertificateNumber :
     * RegisterAuthority :
     * RegisterId : 0000000016
     * RegisterToDate : /Date(-2209017600000+0800)/
     * Sex :
     * VerifyStatus : 0
     * VerifyView :
     * Name:
     * FirstJobTime:
     */

    /**
     * 出生日期
     */
    private String BirthDate;
    /**
     * 发证机关
     */
    private String CertificateAuthority;
    /**
     * 发证日期
     */
    private String CertificateDate;
    /**
     * 国籍
     */
    private String Country;
    /**
     * 首次注册日期
     */
    private String FirstRegisterDate;
    /**
     * 身份证编号
     */
    private String IDCard;
    /**
     * 最近注册日期
     */
    private String LastRegisterDate;
    private String Picture1;
    private String Picture2;
    private String Picture3;
    private String Picture4;

    /**
     * 职业地点
     */
    private String PracticeAddress;
    /**
     * 执业证编号
     */
    private String CertificateId;
    /**
     * 注册机关
     */
    private String RegisterAuthority;
    /**
     * 注册ID
     */
    private String RegisterId;
    /**
     * 最近注册有效期至
     */
    private String RegisterToDate;
    /**
     * 性别
     */
    private String Sex;
    /**
     * 认证状态
     */
    private int VerifyStatus;
    /**
     * 审核意见
     */
    private String VerifyView;

    /**
     * 姓名
     */
    private String Name;

    /**
     * 首次工作时间
     */
    private String FirstJobTime;

    public String getBirthDate() {
        return BirthDate;
    }

    public void setBirthDate(String BirthDate) {
        this.BirthDate = BirthDate;
    }

    public String getCertificateAuthority() {
        return CertificateAuthority;
    }

    public void setCertificateAuthority(String CertificateAuthority) {
        this.CertificateAuthority = CertificateAuthority;
    }

    public String getCertificateDate() {
        return CertificateDate;
    }

    public void setCertificateDate(String CertificateDate) {
        this.CertificateDate = CertificateDate;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String Country) {
        this.Country = Country;
    }

    public String getFirstRegisterDate() {
        return FirstRegisterDate;
    }

    public void setFirstRegisterDate(String FirstRegisterDate) {
        this.FirstRegisterDate = FirstRegisterDate;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public String getLastRegisterDate() {
        return LastRegisterDate;
    }

    public void setLastRegisterDate(String LastRegisterDate) {
        this.LastRegisterDate = LastRegisterDate;
    }

    public String getPicture1() {
        return Picture1;
    }

    public void setPicture1(String Picture1) {
        this.Picture1 = Picture1;
    }

    public String getPicture2() {
        return Picture2;
    }

    public void setPicture2(String Picture2) {
        this.Picture2 = Picture2;
    }

    public String getPicture3() {
        return Picture3;
    }

    public void setPicture3(String Picture3) {
        this.Picture3 = Picture3;
    }

    public String getPicture4() {
        return Picture4;
    }

    public void setPicture4(String Picture4) {
        this.Picture4 = Picture4;
    }

    public String getPracticeAddress() {
        return PracticeAddress;
    }

    public void setPracticeAddress(String PracticeAddress) {
        this.PracticeAddress = PracticeAddress;
    }

    public String getCertificateId() {
        return CertificateId;
    }

    public void setCertificateId(String CertificateId) {
        this.CertificateId = CertificateId;
    }

    public String getRegisterAuthority() {
        return RegisterAuthority;
    }

    public void setRegisterAuthority(String RegisterAuthority) {
        this.RegisterAuthority = RegisterAuthority;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String RegisterId) {
        this.RegisterId = RegisterId;
    }

    public String getRegisterToDate() {
        return RegisterToDate;
    }

    public void setRegisterToDate(String RegisterToDate) {
        this.RegisterToDate = RegisterToDate;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String Sex) {
        this.Sex = Sex;
    }

    public int getVerifyStatus() {
        return VerifyStatus;
    }

    public void setVerifyStatus(int VerifyStatus) {
        this.VerifyStatus = VerifyStatus;
    }

    public String getVerifyView() {
        return VerifyView;
    }

    public void setVerifyView(String VerifyView) {
        this.VerifyView = VerifyView;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getFirstJobTime() {
        return FirstJobTime;
    }

    public void setFirstJobTime(String firstJobTime) {
        FirstJobTime = firstJobTime;
    }
}
