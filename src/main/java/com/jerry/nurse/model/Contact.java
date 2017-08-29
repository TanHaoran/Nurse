package com.jerry.nurse.model;

import android.text.TextUtils;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import java.io.Serializable;

import static com.jerry.nurse.constant.ServiceConstant.AVATAR_ADDRESS;

public class Contact extends BaseIndexPinyinBean implements Serializable {
    /**
     * Avatar : Screen-20170622064742time20170809054355.png
     * DepartmentName :
     * HospitalName :
     * IsFriend : false
     * Name : 刘备
     * NickName : 灰太狼
     * Phone : 18709269196
     * Remark :
     * Role : 0
     * Sex : 男
     */

    private String Avatar;
    private String DepartmentName;
    private String HospitalName;
    private boolean IsFriend;
    private String Name;
    private String NickName;
    private String Phone;
    private String Remark;
    private int Role;
    private String Sex;
    private String FriendId;
    private boolean isChoose;
    private boolean IsInternalHospital;

    public String getAvatar() {
        if (!TextUtils.isEmpty(Avatar)) {
            if (Avatar.startsWith("http")) {
                return Avatar;
            } else {
                return AVATAR_ADDRESS + Avatar;
            }
        }
        return "";
    }

    public void setAvatar(String Avatar) {
        this.Avatar = Avatar;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String DepartmentName) {
        this.DepartmentName = DepartmentName;
    }

    public String getHospitalName() {
        return HospitalName;
    }

    public void setHospitalName(String HospitalName) {
        this.HospitalName = HospitalName;
    }

    public boolean isIsFriend() {
        return IsFriend;
    }

    public void setIsFriend(boolean IsFriend) {
        this.IsFriend = IsFriend;
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

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String Phone) {
        this.Phone = Phone;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String Remark) {
        this.Remark = Remark;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int Role) {
        this.Role = Role;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String Sex) {
        this.Sex = Sex;
    }

    public boolean isFriend() {
        return IsFriend;
    }

    public void setFriend(boolean friend) {
        IsFriend = friend;
    }

    public String getFriendId() {
        return FriendId;
    }

    public void setFriendId(String friendId) {
        FriendId = friendId;
    }

    public boolean isInternalHospital() {
        return IsInternalHospital;
    }

    public void setInternalHospital(boolean internalHospital) {
        IsInternalHospital = internalHospital;
    }

    @Override
    public String getTarget() {
        if (!TextUtils.isEmpty(Remark)) {
            return Remark;
        } else if (!TextUtils.isEmpty(NickName)) {
            return NickName;
        } else if (!TextUtils.isEmpty(Name)) {
            return Name;
        } else if (!TextUtils.isEmpty(Phone)) {
            return Phone;
        } else {
            return " ";
        }
    }

    public boolean isChoose() {
        return isChoose;
    }

    public void setChoose(boolean choose) {
        isChoose = choose;
    }

    @Override
    public String toString() {
        return "Contact{" +
                "Avatar='" + Avatar + '\'' +
                ", DepartmentName='" + DepartmentName + '\'' +
                ", HospitalName='" + HospitalName + '\'' +
                ", IsFriend=" + IsFriend +
                ", Name='" + Name + '\'' +
                ", NickName='" + NickName + '\'' +
                ", Phone='" + Phone + '\'' +
                ", Remark='" + Remark + '\'' +
                ", Role=" + Role +
                ", Sex='" + Sex + '\'' +
                ", FriendId='" + FriendId + '\'' +
                ", isChoose=" + isChoose +
                ", IsInternalHospital=" + IsInternalHospital +
                '}';
    }
}