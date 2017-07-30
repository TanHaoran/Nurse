package com.jerry.nurse.model;

import android.text.TextUtils;

import com.jerry.nurse.util.DateUtil;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/7/28.
 */

public class UserProfessionalCertificateInfo extends DataSupport {


    /**
     * ApproveDate : /Date(-2209017600000+0800)/
     * Category :
     * DateBirth : /Date(-2209017600000+0800)/
     * IssuingAgency :
     * IssuingDate : /Date(-2209017600000+0800)/
     * MajorName :
     * ManageId :
     * Name :
     * Picture1 :
     * Picture2 :
     * QuaCertNumber :
     * QuaLevel :
     * RegisterId : 0000000016
     * Sex :
     * VerifyStatus : 0
     * VerifyView :
     */

    /**
     * 批准日期
     */
    private String ApproveDate;
    /**
     * 资格级别
     */
    private String Category;
    /**
     * 出生年月
     */
    private String DateBirth;
    /**
     * 签发单位
     */
    private String IssuingAgency;
    /**
     * 签发日期
     */
    private String IssuingDate;
    /**
     * 专业名称
     */
    private String MajorName;
    /**
     * 管理号
     */
    private String ManageId;
    /**
     * 姓名
     */
    private String Name;
    private String Picture1;
    private String Picture2;
    /**
     * 编号
     */
    private String CertificateId;
    /**
     * 类别
     */
    private String QuaLevel;
    /**
     * 注册ID
     */
    private String RegisterId;
    /**
     * 性别
     */
    private String Sex;
    /**
     * 认证状态
     */
    private int VerifyStatus;
    /**
     * 审核意见
     */
    private String VerifyView;

    public String getApproveDate() {
        return ApproveDate;
    }

    public void setApproveDate(String ApproveDate) {
        this.ApproveDate = ApproveDate;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String Category) {
        this.Category = Category;
    }

    public String getDateBirth() {
        return DateBirth;
    }

    public void setDateBirth(String DateBirth) {
        this.DateBirth = DateBirth;
    }

    public String getIssuingAgency() {
        return IssuingAgency;
    }

    public void setIssuingAgency(String IssuingAgency) {
        this.IssuingAgency = IssuingAgency;
    }

    public String getIssuingDate() {
        return IssuingDate;
    }

    public void setIssuingDate(String IssuingDate) {
        this.IssuingDate = IssuingDate;
    }

    public String getMajorName() {
        return MajorName;
    }

    public void setMajorName(String MajorName) {
        this.MajorName = MajorName;
    }

    public String getManageId() {
        return ManageId;
    }

    public void setManageId(String ManageId) {
        this.ManageId = ManageId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getPicture1() {
        return Picture1;
    }

    public void setPicture1(String Picture1) {
        this.Picture1 = Picture1;
    }

    public String getPicture2() {
        return Picture2;
    }

    public void setPicture2(String Picture2) {
        this.Picture2 = Picture2;
    }

    public String getCertificateId() {
        return CertificateId;
    }

    public void setCertificateId(String CertificateId) {
        this.CertificateId = CertificateId;
    }

    public String getQuaLevel() {
        return QuaLevel;
    }

    public void setQuaLevel(String QuaLevel) {
        this.QuaLevel = QuaLevel;
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

    public int getVerifyStatus() {
        return VerifyStatus;
    }

    public void setVerifyStatus(int VerifyStatus) {
        this.VerifyStatus = VerifyStatus;
    }

    public String getVerifyView() {
        return VerifyView;
    }

    public void setVerifyView(String VerifyView) {
        this.VerifyView = VerifyView;
    }
}
