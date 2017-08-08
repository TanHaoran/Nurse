package com.jerry.nurse.util;

import com.mcxtzhang.indexlib.IndexBar.bean.BaseIndexPinyinBean;

/**
 * 手机通讯录联系人
 */
public class CellphoneContact extends BaseIndexPinyinBean {
    private String mName;
    private String mNumber;

    public CellphoneContact() {
    }

    public CellphoneContact(String name, String number) {
        mName = name;
        mNumber = number;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getNumber() {
        return mNumber;
    }

    public void setNumber(String number) {
        mNumber = number;
    }

    @Override
    public String toString() {
        return "CellphoneContact{" +
                "mName='" + mName + '\'' +
                ", mNumber='" + mNumber + '\'' +
                '}';
    }

    @Override
    public String getTarget() {
        return mName;
    }
}