package com.jerry.nurse.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/7/27.
 * 用户医院关联表
 */

public class UserHospitalInfo  extends DataSupport {


    /**
     * DepartmentId :
     * EmployeeId :
     * HospitalId :
     * NursingUnitId :
     * RegisterId : 0000000012
     * RelRecordId : 0000000008
     * Role : 0
     */

    private String DepartmentId;
    private String DepartmentName;
    private String EmployeeId;
    private String HospitalId;
    private String HospitalName;
    private String NursingUnitId;
    private String NursingUnitName;
    private String RegisterId;
    private String RelRecordId;
    private int Role;

    public String getDepartmentId() {
        return DepartmentId;
    }

    public void setDepartmentId(String DepartmentId) {
        this.DepartmentId = DepartmentId;
    }

    public String getEmployeeId() {
        return EmployeeId;
    }

    public void setEmployeeId(String EmployeeId) {
        this.EmployeeId = EmployeeId;
    }

    public String getHospitalId() {
        return HospitalId;
    }

    public void setHospitalId(String HospitalId) {
        this.HospitalId = HospitalId;
    }

    public String getNursingUnitId() {
        return NursingUnitId;
    }

    public void setNursingUnitId(String NursingUnitId) {
        this.NursingUnitId = NursingUnitId;
    }

    public String getRegisterId() {
        return RegisterId;
    }

    public void setRegisterId(String RegisterId) {
        this.RegisterId = RegisterId;
    }

    public String getRelRecordId() {
        return RelRecordId;
    }

    public void setRelRecordId(String RelRecordId) {
        this.RelRecordId = RelRecordId;
    }

    public int getRole() {
        return Role;
    }

    public void setRole(int Role) {
        this.Role = Role;
    }

    public String getDepartmentName() {
        return DepartmentName;
    }

    public void setDepartmentName(String departmentName) {
        DepartmentName = departmentName;
    }

    public String getHospitalName() {
        return HospitalName;
    }

    public void setHospitalName(String hospitalName) {
        HospitalName = hospitalName;
    }

    public String getNursingUnitName() {
        return NursingUnitName;
    }

    public void setNursingUnitName(String nursingUnitName) {
        NursingUnitName = nursingUnitName;
    }
}
