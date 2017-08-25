package com.jerry.nurse.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Jerry on 2017/8/7.
 */

public class OfficeResult {

    /**
     * body : [{"Address":"","Contact":"","DepartmentId":"0000000001","DisplayOrder":0,"HospitalId":"0000000152","Introduction":"","IsFlag":0,"Logo":"","Name":"眼科","ParentId":"","Phone":"","SpellCode":""},{"Address":"","Contact":"","DepartmentId":"0000000002","DisplayOrder":0,"HospitalId":"0000000152","Introduction":"","IsFlag":0,"Logo":"","Name":"耳科","ParentId":"","Phone":"","SpellCode":""},{"Address":"","Contact":"","DepartmentId":"0000000003","DisplayOrder":0,"HospitalId":"0000000152","Introduction":"","IsFlag":0,"Logo":null,"Name":"神经科","ParentId":"","Phone":"","SpellCode":""},{"Address":"","Contact":"","DepartmentId":"0000000004","DisplayOrder":0,"HospitalId":"0000000152","Introduction":"","IsFlag":0,"Logo":"","Name":"内科","ParentId":"","Phone":"","SpellCode":""}]
     * code : 0
     * msg :
     */

    private int code;
    private String msg;
    private List<Office> body;

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

    public List<Office> getBody() {
        return body;
    }

    public void setBody(List<Office> body) {
        this.body = body;
    }

    public static class Office implements Serializable{
        /**
         * Address :
         * Contact :
         * DepartmentId : 0000000001
         * DisplayOrder : 0
         * HospitalId : 0000000152
         * Introduction :
         * IsFlag : 0
         * Logo :
         * Name : 眼科
         * ParentId :
         * Phone :
         * SpellCode :
         */

        private String Address;
        private String Contact;
        private String DepartmentId;
        private int DisplayOrder;
        private String HospitalId;
        private String Introduction;
        private int IsFlag;
        private String Logo;
        private String Name;
        private String ParentId;
        private String Phone;
        private String SpellCode;

        public String getAddress() {
            return Address;
        }

        public void setAddress(String Address) {
            this.Address = Address;
        }

        public String getContact() {
            return Contact;
        }

        public void setContact(String Contact) {
            this.Contact = Contact;
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

        public String getIntroduction() {
            return Introduction;
        }

        public void setIntroduction(String Introduction) {
            this.Introduction = Introduction;
        }

        public int getIsFlag() {
            return IsFlag;
        }

        public void setIsFlag(int IsFlag) {
            this.IsFlag = IsFlag;
        }

        public String getLogo() {
            return Logo;
        }

        public void setLogo(String Logo) {
            this.Logo = Logo;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getParentId() {
            return ParentId;
        }

        public void setParentId(String ParentId) {
            this.ParentId = ParentId;
        }

        public String getPhone() {
            return Phone;
        }

        public void setPhone(String Phone) {
            this.Phone = Phone;
        }

        public String getSpellCode() {
            return SpellCode;
        }

        public void setSpellCode(String SpellCode) {
            this.SpellCode = SpellCode;
        }
    }
}
