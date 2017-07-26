package com.jerry.nurse.model;

import com.jerry.nurse.util.DateUtil;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/7/16.
 */

public class User extends DataSupport {

    /**
     * Avatar :
     * Name :
     * NickName :
     * Password :
     * Phone : 13002909620
     * RegisterId : 0000000002
     */

    private String Avatar;
    private String Name;
    private String NickName;
    private String Password;
    private String Phone;
    private String RegisterId;

    private String Sex;
    private String IDCard;
    private String Birthday;
    private int Age;
    private String City;
    private String Region;
    private String Address;
    private String Nation;
    private String MeritalStatus;
    private String Education;
    private String EMail;
    private String QQ;

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String Avatar) {
        this.Avatar = Avatar;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getNickName() {
        return NickName;
    }

    public void setNickName(String NickName) {
        this.NickName = NickName;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
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

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public String getBirthday() {
        if (Birthday != null && Birthday.length() > 13) {
            return DateUtil.parseMysqlDateToString(Birthday);
        }
        return null;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int age) {
        Age = age;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String region) {
        Region = region;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getNation() {
        return Nation;
    }

    public void setNation(String nation) {
        Nation = nation;
    }

    public String getMeritalStatus() {
        return MeritalStatus;
    }

    public void setMeritalStatus(String meritalStatus) {
        MeritalStatus = meritalStatus;
    }

    public String getEducation() {
        return Education;
    }

    public void setEducation(String education) {
        Education = education;
    }

    public String getEMail() {
        return EMail;
    }

    public void setEMail(String EMail) {
        this.EMail = EMail;
    }

    public String getQQ() {
        return QQ;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
    }

    @Override
    public String toString() {
        return "User{" +
                "Avatar='" + Avatar + '\'' +
                ", Name='" + Name + '\'' +
                ", NickName='" + NickName + '\'' +
                ", Password='" + Password + '\'' +
                ", Phone='" + Phone + '\'' +
                ", RegisterId='" + RegisterId + '\'' +
                ", Sex='" + Sex + '\'' +
                ", IDCard='" + IDCard + '\'' +
                ", Birthday='" + Birthday + '\'' +
                ", Age=" + Age +
                ", City='" + City + '\'' +
                ", Region='" + Region + '\'' +
                ", Address='" + Address + '\'' +
                ", Nation='" + Nation + '\'' +
                ", MeritalStatus='" + MeritalStatus + '\'' +
                ", Education='" + Education + '\'' +
                ", EMail='" + EMail + '\'' +
                ", QQ='" + QQ + '\'' +
                '}';
    }
}
