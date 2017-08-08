package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/7.
 */

public class ProfessionalResult {

    /**
     * body : {"ApproveDate":"/Date(-2209017600000+0800)/","Category":"","CertificateId":"","DateBirth":"/Date(-2209017600000+0800)/","IssuingAgency":"","IssuingDate":"/Date(-2209017600000+0800)/","MajorName":"","ManageId":"","Name":"","Picture1":"","Picture2":"","QuaLevel":"","RegisterId":"0000000035","Sex":"","VerifyStatus":0,"VerifyView":""}
     * code : 0
     * msg :
     */

    private Professional body;
    private int code;
    private String msg;

    public Professional getBody() {
        return body;
    }

    public void setBody(Professional body) {
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

    public static class Professional {
        /**
         * ApproveDate : /Date(-2209017600000+0800)/
         * Category :
         * CertificateId :
         * DateBirth : /Date(-2209017600000+0800)/
         * IssuingAgency :
         * IssuingDate : /Date(-2209017600000+0800)/
         * MajorName :
         * ManageId :
         * Name :
         * Picture1 :
         * Picture2 :
         * QuaLevel :
         * RegisterId : 0000000035
         * Sex :
         * VerifyStatus : 0
         * VerifyView :
         */

        private String ApproveDate;
        private String Category;
        private String CertificateId;
        private String DateBirth;
        private String IssuingAgency;
        private String IssuingDate;
        private String MajorName;
        private String ManageId;
        private String Name;
        private String Picture1;
        private String Picture2;
        private String QuaLevel;
        private String RegisterId;
        private String Sex;
        private int VerifyStatus;
        private String VerifyView;

        public String getApproveDate() {
            return ApproveDate;
        }

        public void setApproveDate(String ApproveDate) {
            this.ApproveDate = ApproveDate;
        }

        public String getCategory() {
            return Category;
        }

        public void setCategory(String Category) {
            this.Category = Category;
        }

        public String getCertificateId() {
            return CertificateId;
        }

        public void setCertificateId(String CertificateId) {
            this.CertificateId = CertificateId;
        }

        public String getDateBirth() {
            return DateBirth;
        }

        public void setDateBirth(String DateBirth) {
            this.DateBirth = DateBirth;
        }

        public String getIssuingAgency() {
            return IssuingAgency;
        }

        public void setIssuingAgency(String IssuingAgency) {
            this.IssuingAgency = IssuingAgency;
        }

        public String getIssuingDate() {
            return IssuingDate;
        }

        public void setIssuingDate(String IssuingDate) {
            this.IssuingDate = IssuingDate;
        }

        public String getMajorName() {
            return MajorName;
        }

        public void setMajorName(String MajorName) {
            this.MajorName = MajorName;
        }

        public String getManageId() {
            return ManageId;
        }

        public void setManageId(String ManageId) {
            this.ManageId = ManageId;
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

        public String getQuaLevel() {
            return QuaLevel;
        }

        public void setQuaLevel(String QuaLevel) {
            this.QuaLevel = QuaLevel;
        }

        public String getRegisterId() {
            return RegisterId;
        }

        public void setRegisterId(String RegisterId) {
            this.RegisterId = RegisterId;
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
