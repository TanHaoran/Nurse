package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/27.
 */

public class Hospital {

    private String HospitalId;
    private String Name;

    public Hospital(String hospitalId, String name) {
        HospitalId = hospitalId;
        Name = name;
    }

    public String getHospitalId() {
        return HospitalId;
    }

    public void setHospitalId(String hospitalId) {
        HospitalId = hospitalId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }
}
