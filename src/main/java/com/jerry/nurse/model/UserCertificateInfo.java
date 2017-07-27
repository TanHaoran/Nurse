package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/7/27.
 * 用户资格证关联表
 */

public class UserCertificateInfo  extends DataSupport {

    /**
     * BirthDate : /Date(-2209017600000+0800)/
     * CertificateAuthority :
     * CertificateDate : /Date(-2209017600000+0800)/
     * Country :
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
     * RegisterId : 0000000012
     * RegisterToDate : /Date(-2209017600000+0800)/
     * Sex :
     */

    private String BirthDate;
    private String CertificateAuthority;
    private String CertificateDate;
    private String Country;
    private String FirstRegisterDate;
    private String IDCard;
    private String LastRegisterDate;
    private String Picture1;
    private String Picture2;
    private String Picture3;
    private String Picture4;
    private String PracticeAddress;
    private String PracticeCertificateNumber;
    private String RegisterAuthority;
    private String RegisterId;
    private String RegisterToDate;
    private String Sex;

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

    public String getPracticeCertificateNumber() {
        return PracticeCertificateNumber;
    }

    public void setPracticeCertificateNumber(String PracticeCertificateNumber) {
        this.PracticeCertificateNumber = PracticeCertificateNumber;
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
}
