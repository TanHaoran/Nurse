package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

public class UserInfo extends DataSupport {
    /**
     * Avatar :
     * Birthday : /Date(-2209017600000+0800)/
     * DepartmentId :
     * DepartmentName :
     * EmployeeId :
     * FirstJobTime : /Date(-2209017600000+0800)/
     * HospitalId :
     * HospitalName :
     * Name :
     * NickName :
     * PCertificateId :
     * QCertificateId :
     * RegisterId :
     * Sex :
     */

    private String Avatar;
    private String Birthday;
    private String DepartmentId;
    private String DepartmentName;
    private String EmployeeId;
    private String FirstJobTime;
    private String HospitalId;
    private String HospitalName;
    private String Name;
    private String NickName;
    private String PCertificateId;
    private String QCertificateId;
    private String RegisterId;
    private String Sex;
    private int PVerifyStatus;
    private int QVerifyStatus;

    public String getAvatar() {
        return Avatar;
    }

    public void setAvatar(String Avatar) {
        this.Avatar = Avatar;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String Birthday) {
        this.Birthday = Birthday;
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

    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String EmployeeId) {
        this.EmployeeId = EmployeeId;
    }

    public String getFirstJobTime() {
        return FirstJobTime;
    }

    public void setFirstJobTime(String FirstJobTime) {
        this.FirstJobTime = FirstJobTime;
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

    public String getPCertificateId() {
        return PCertificateId;
    }

    public void setPCertificateId(String PCertificateId) {
        this.PCertificateId = PCertificateId;
    }

    public String getQCertificateId() {
        return QCertificateId;
    }

    public void setQCertificateId(String QCertificateId) {
        this.QCertificateId = QCertificateId;
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

    public int getPVerifyStatus() {
        return PVerifyStatus;
    }

    public void setPVerifyStatus(int PVerifyStatus) {
        this.PVerifyStatus = PVerifyStatus;
    }

    public int getQVerifyStatus() {
        return QVerifyStatus;
    }

    public void setQVerifyStatus(int QVerifyStatus) {
        this.QVerifyStatus = QVerifyStatus;
    }
}