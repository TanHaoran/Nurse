package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/7/27.
 */

public class Office {
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Office(String name) {

        this.name = name;
    }

    private String name;
}
