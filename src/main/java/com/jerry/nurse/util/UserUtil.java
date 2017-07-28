package com.jerry.nurse.util;

import android.content.Context;
import android.text.TextUtils;

import com.jerry.nurse.model.UserBasicInfo;
import com.jerry.nurse.model.UserHospitalInfo;
import com.jerry.nurse.model.UserPractisingCertificateInfo;
import com.jerry.nurse.model.UserProfessionalCertificateInfo;
import com.jerry.nurse.model.UserRegisterInfo;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/7/26.
 */

public class UserUtil {

    /**
     * 保存用户注册信息
     *
     * @param context
     * @param info
     */
    public static void saveRegisterInfo(Context context, UserRegisterInfo info) {

        // 保存到SP
        updateSP(context, info);

        DataSupport.deleteAll(UserRegisterInfo.class);

        UserRegisterInfo user = new UserRegisterInfo();

        user.setAvatar(info.getAvatar());
        user.setName(info.getName());
        user.setNickName(info.getNickName());
        user.setPassword(info.getPassword());
        user.setPhone(info.getPhone());
        user.setRegisterId(info.getRegisterId());
        user.save();
    }

    /**
     * 保存用户基本信息
     *
     * @param info
     */
    public static void saveBasicInfo(UserBasicInfo info) {

        DataSupport.deleteAll(UserBasicInfo.class);

        UserBasicInfo user = new UserBasicInfo();
        user.setAddress(info.getAddress());
        user.setAge(info.getAge());
        user.setBirthday(DateUtil.parseStringToMysqlDate(info.getBirthday()));
        user.setCity(info.getCity());
        user.setEMail(info.getEMail());
        user.setEducation(info.getEducation());
        user.setIDCard(info.getIDCard());
        user.setMeritalStatus(info.getMeritalStatus());
        user.setName(info.getName());
        user.setNation(info.getNation());
        user.setPhone(info.getPhone());
        user.setProvince(info.getProvince());
        user.setQQ(info.getQQ());
        user.setRegion(info.getRegion());
        user.setRegisterId(info.getRegisterId());
        user.setSex(info.getSex());

        user.save();
    }

    /**
     * 保存用户专业资格证信息
     *
     * @param info
     */
    public static void saveProfessionalCertificateInfo(UserProfessionalCertificateInfo info) {

        DataSupport.deleteAll(UserProfessionalCertificateInfo.class);

        UserProfessionalCertificateInfo user = new UserProfessionalCertificateInfo();
        user.setApproveDate(DateUtil.parseStringToMysqlDate(info.getApproveDate()));
        user.setCategory(info.getCategory());
        user.setDateBirth(DateUtil.parseStringToMysqlDate(info.getDateBirth()));
        user.setIssuingAgency(info.getIssuingAgency());
        user.setIssuingDate(DateUtil.parseStringToMysqlDate(info.getIssuingDate()));
        user.setMajorName(info.getMajorName());
        user.setManageId(info.getManageId());
        user.setName(info.getName());
        user.setPicture1(info.getPicture1());
        user.setPicture2(info.getPicture2());
        user.setCertificateId(info.getCertificateId());
        user.setQuaLevel(info.getQuaLevel());
        user.setRegisterId(info.getRegisterId());
        user.setSex(info.getSex());
        user.setVerifyStatus(info.getVerifyStatus());
        user.setVerifyView(info.getVerifyView());

        user.save();
    }

    /**
     * 保存用户执业资格证信息
     *
     * @param info
     */
    public static void savePractisingCertificateInfo(UserPractisingCertificateInfo info) {

        DataSupport.deleteAll(UserPractisingCertificateInfo.class);

        UserPractisingCertificateInfo user = new UserPractisingCertificateInfo();
        user.setBirthDate(DateUtil.parseStringToMysqlDate(info.getBirthDate()));
        user.setCertificateAuthority(info.getCertificateAuthority());
        user.setCertificateDate(DateUtil.parseStringToMysqlDate(info.getCertificateDate()));
        user.setCountry(info.getCountry());
        user.setFirstRegisterDate(DateUtil.parseStringToMysqlDate(info.getFirstRegisterDate()));
        user.setIDCard(info.getIDCard());
        user.setLastRegisterDate(DateUtil.parseStringToMysqlDate(info.getLastRegisterDate()));
        user.setPicture1(info.getPicture1());
        user.setPicture2(info.getPicture2());
        user.setPicture3(info.getPicture3());
        user.setPicture4(info.getPicture4());
        user.setPracticeAddress(info.getPracticeAddress());
        user.setCertificateId(info.getCertificateId());
        user.setCertificateAuthority(info.getCertificateAuthority());
        user.setRegisterAuthority(info.getRegisterAuthority());
        user.setRegisterId(info.getRegisterId());
        user.setRegisterToDate(DateUtil.parseStringToMysqlDate(info.getRegisterToDate()));
        user.setSex(info.getSex());
        user.setVerifyStatus(info.getVerifyStatus());
        user.setVerifyView(info.getVerifyView());

        user.save();

    }

    /**
     * 保存用户医院信息
     *
     * @param info
     */
    public static void saveHospitalInfo(UserHospitalInfo info) {

        DataSupport.deleteAll(UserHospitalInfo.class);

        UserHospitalInfo user = new UserHospitalInfo();
        user.setDepartmentId(info.getDepartmentId());
        user.setEmployeeId(info.getEmployeeId());
        user.setHospitalId(info.getHospitalId());
        user.setNursingUnitId(info.getNursingUnitId());
        user.setRegisterId(info.getRegisterId());
        user.setRelRecordId(info.getRelRecordId());
        user.setRole(info.getRole());

        user.save();
    }

    /**
     * 更新SP中的数据
     *
     * @param context
     * @param info
     */
    private static void updateSP(Context context, UserRegisterInfo info) {
        if (!TextUtils.isEmpty(info.getPhone())) {
            SPUtil.put(context, SPUtil.CELLPHONE, info.getPhone());
        }
        if (!TextUtils.isEmpty(info.getName())) {
            SPUtil.put(context, SPUtil.NAME, info.getName());
        }
        if (!TextUtils.isEmpty(info.getNickName())) {
            SPUtil.put(context, SPUtil.NICKNAME, info.getNickName());
        }
        if (!TextUtils.isEmpty(info.getRegisterId())) {
            SPUtil.put(context, SPUtil.REGISTER_ID, info.getRegisterId());
        }
    }

    public static void deleteAllInfo(Context context) {
        SPUtil.remove(context, SPUtil.CELLPHONE);
        SPUtil.remove(context, SPUtil.NAME);
        SPUtil.remove(context, SPUtil.NICKNAME);
        SPUtil.remove(context, SPUtil.REGISTER_ID);

        // 清除用户表信息
        try {
            DataSupport.deleteAll(UserRegisterInfo.class);
            DataSupport.deleteAll(UserBasicInfo.class);
            DataSupport.deleteAll(UserProfessionalCertificateInfo.class);
            DataSupport.deleteAll(UserPractisingCertificateInfo.class);
            DataSupport.deleteAll(UserHospitalInfo.class);
            L.i("用户所有信息表删除成功");
        } catch (Exception e) {
            L.i("用户所有信息表删除异常");
            e.printStackTrace();
        }
    }
}
