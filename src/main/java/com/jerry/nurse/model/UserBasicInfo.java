package com.jerry.nurse.model;

import android.text.TextUtils;

import com.jerry.nurse.util.DateUtil;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/7/26.
 * 用户基本信息
 */

public class UserBasicInfo extends DataSupport {


    /**
     * Address :
     * Age : 0
     * Birthday : /Date(-2209017600000+0800)/
     * City :
     * EMail :
     * Education :
     * IDCard :
     * MeritalStatus :
     * Name :
     * Nation :
     * Phone : 18709269196
     * Province :
     * QQ :
     * Region :
     * RegisterId : 0000000012
     * Sex :
     */

    private String Address;
    private int Age;
    private String Birthday;
    private String City;
    private String EMail;
    private String Education;
    private String IDCard;
    private String MeritalStatus;
    private String Name;
    private String Nation;
    private String Phone;
    private String Province;
    private String QQ;
    private String Region;
    private String RegisterId;
    private String Sex;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String Address) {
        this.Address = Address;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int Age) {
        this.Age = Age;
    }

    public String getBirthday() {
        if (!TextUtils.isEmpty(Birthday)) {
            return DateUtil.parseMysqlDateToString(Birthday);
        }
        return null;
    }

    public void setBirthday(String Birthday) {
        this.Birthday = Birthday;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String City) {
        this.City = City;
    }

    public String getEMail() {
        return EMail;
    }

    public void setEMail(String EMail) {
        this.EMail = EMail;
    }

    public String getEducation() {
        return Education;
    }

    public void setEducation(String Education) {
        this.Education = Education;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public String getMeritalStatus() {
        return MeritalStatus;
    }

    public void setMeritalStatus(String MeritalStatus) {
        this.MeritalStatus = MeritalStatus;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getNation() {
        return Nation;
    }

    public void setNation(String Nation) {
        this.Nation = Nation;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getProvince() {
        return Province;
    }

    public void setProvince(String Province) {
        this.Province = Province;
    }

    public String getQQ() {
        return QQ;
    }

    public void setQQ(String QQ) {
        this.QQ = QQ;
    }

    public String getRegion() {
        return Region;
    }

    public void setRegion(String Region) {
        this.Region = Region;
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
}
