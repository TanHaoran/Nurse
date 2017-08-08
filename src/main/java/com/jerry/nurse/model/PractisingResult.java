package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/7.
 */

public class PractisingResult {

    /**
     * body : {"BirthDate":"/Date(-2209017600000+0800)/","CertificateAuthority":"","CertificateDate":"/Date(-2209017600000+0800)/","CertificateId":"","Country":"","FirstJobTime":"/Date(-2209017600000+0800)/","FirstRegisterDate":"/Date(-2209017600000+0800)/","IDCard":"","LastRegisterDate":"/Date(-2209017600000+0800)/","Name":"","Picture1":"","Picture2":"","Picture3":"","Picture4":"","PracticeAddress":"","RegisterAuthority":"","RegisterId":"0000000035","RegisterToDate":"/Date(-2209017600000+0800)/","Sex":"","VerifyStatus":0,"VerifyView":""}
     * code : 0
     * msg :
     */

    private Practising body;
    private int code;
    private String msg;

    public Practising getBody() {
        return body;
    }

    public void setBody(Practising body) {
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

    public static class Practising {
        /**
         * BirthDate : /Date(-2209017600000+0800)/
         * CertificateAuthority :
         * CertificateDate : /Date(-2209017600000+0800)/
         * CertificateId :
         * Country :
         * FirstJobTime : /Date(-2209017600000+0800)/
         * FirstRegisterDate : /Date(-2209017600000+0800)/
         * IDCard :
         * LastRegisterDate : /Date(-2209017600000+0800)/
         * Name :
         * Picture1 :
         * Picture2 :
         * Picture3 :
         * Picture4 :
         * PracticeAddress :
         * RegisterAuthority :
         * RegisterId : 0000000035
         * RegisterToDate : /Date(-2209017600000+0800)/
         * Sex :
         * VerifyStatus : 0
         * VerifyView :
         */

        private String BirthDate;
        private String CertificateAuthority;
        private String CertificateDate;
        private String CertificateId;
        private String Country;
        private String FirstJobTime;
        private String FirstRegisterDate;
        private String IDCard;
        private String LastRegisterDate;
        private String Name;
        private String Picture1;
        private String Picture2;
        private String Picture3;
        private String Picture4;
        private String PracticeAddress;
        private String RegisterAuthority;
        private String RegisterId;
        private String RegisterToDate;
        private String Sex;
        private int VerifyStatus;
        private String VerifyView;

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

        public String getCertificateId() {
            return CertificateId;
        }

        public void setCertificateId(String CertificateId) {
            this.CertificateId = CertificateId;
        }

        public String getCountry() {
            return Country;
        }

        public void setCountry(String Country) {
            this.Country = Country;
        }

        public String getFirstJobTime() {
            return FirstJobTime;
        }

        public void setFirstJobTime(String FirstJobTime) {
            this.FirstJobTime = FirstJobTime;
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

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
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
    }
}
