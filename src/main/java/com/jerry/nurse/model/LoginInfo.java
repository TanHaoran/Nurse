package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

/**
 * 用户登陆信息
 */
public class LoginInfo extends DataSupport {
    /**
     * Avatar :
     * DepartmentId :
     * DepartmentName :
     * HospitalId :
     * HospitalName :
     * Name :
     * NickName :
     * PStatus : 0
     * QStatus : 0
     * RegisterId :
     */

    private int id;
    private String Avatar;
    private String DepartmentId;
    private String DepartmentName;
    private String HospitalId;
    private String HospitalName;
    private String Name;
    private String NickName;
    private int PStatus;
    private int QStatus;
    private String RegisterId;

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String Avatar) {
        this.Avatar = Avatar;
    }

    public String getDepartmentId() {
        return DepartmentId;
    }

    public void setDepartmentId(String DepartmentId) {
        this.DepartmentId = DepartmentId;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String DepartmentName) {
        this.DepartmentName = DepartmentName;
    }

    public String getHospitalId() {
        return HospitalId;
    }

    public void setHospitalId(String HospitalId) {
        this.HospitalId = HospitalId;
    }

    public String getHospitalName() {
        return HospitalName;
    }

    public void setHospitalName(String HospitalName) {
        this.HospitalName = HospitalName;
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

    public int getPStatus() {
        return PStatus;
    }

    public void setPStatus(int PStatus) {
        this.PStatus = PStatus;
    }

    public int getQStatus() {
        return QStatus;
    }

    public void setQStatus(int QStatus) {
        this.QStatus = QStatus;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String RegisterId) {
        this.RegisterId = RegisterId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}