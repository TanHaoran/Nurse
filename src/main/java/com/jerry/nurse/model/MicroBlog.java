package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/31.
 */

public class MicroBlog {

    private String idstr;
    private String name;
    private String location;
    private String description;
    private String profile_image_url;
    private String gender;
    private String DeviceRegId;

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDeviceRegId() {
        return DeviceRegId;
    }

    public void setDeviceRegId(String deviceRegId) {
        DeviceRegId = deviceRegId;
    }
}
