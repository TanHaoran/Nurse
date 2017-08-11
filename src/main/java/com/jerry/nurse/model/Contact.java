package com.jerry.nurse.model;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

import java.io.Serializable;

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

        public String getAvatar() {
            return Avatar;
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

    @Override
    public String getTarget() {
        return NickName;
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
                ", RegisterId='" + FriendId + '\'' +
                '}';
    }
}