package com.jerry.nurse.util;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.jerry.nurse.model.UserBasicInfo;
import com.jerry.nurse.model.UserCertificateInfo;
import com.jerry.nurse.model.UserHospitalInfo;
import com.jerry.nurse.model.UserRegisterInfo;

import org.litepal.crud.DataSupport;

import java.util.List;

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

        List<UserRegisterInfo> list = DataSupport.where("RegisterId=?",
                info.getRegisterId()).find(UserRegisterInfo.class);
        if (list.size() == 1) {
            ContentValues values = new ContentValues();
            values.put("RegisterId", info.getRegisterId());
            DataSupport.updateAll(UserHospitalInfo.class, values, "RegisterId=?",
                    info.getRegisterId());
            info.save();
            L.i("保存用户注册信息成功");
        } else {
            info.save();
            L.i("保存用户注册信息成功");
        }
    }

    /**
     * 保存用户基本信息
     *
     * @param info
     */
    public static void saveBasicInfo(UserBasicInfo info) {

        List<UserBasicInfo> list = DataSupport.where("RegisterId=?",
                info.getRegisterId()).find(UserBasicInfo.class);
        if (list.size() == 1) {
            ContentValues values = new ContentValues();
            values.put("RegisterId", info.getRegisterId());
            DataSupport.updateAll(UserBasicInfo.class, values, "RegisterId=?",
                    info.getRegisterId());
            info.save();
            L.i("保存用户基本信息成功");
        } else {
            info.save();
            L.i("保存用户基本信息成功");
        }
    }


    /**
     * 保存用户执业证信息
     *
     * @param info
     */
    public static void saveCertificateInfo(UserCertificateInfo info) {

        List<UserCertificateInfo> list = DataSupport.where("RegisterId=?",
                info.getRegisterId()).find(UserCertificateInfo.class);
        if (list.size() == 1) {
            ContentValues values = new ContentValues();
            values.put("RegisterId", info.getRegisterId());
            DataSupport.updateAll(UserCertificateInfo.class, values, "RegisterId=?",
                    info.getRegisterId());
            info.save();
            L.i("保存用户执业证信息成功");
        } else {
            info.save();
            L.i("保存用户执业证信息成功");
        }
    }

    /**
     * 保存用户医院信息
     *
     * @param info
     */
    public static void saveHospitalInfo(UserHospitalInfo info) {

        List<UserHospitalInfo> list = DataSupport.where("RegisterId=?",
                info.getRegisterId()).find(UserHospitalInfo.class);
        if (list.size() == 1) {
            ContentValues values = new ContentValues();
            values.put("RegisterId", info.getRegisterId());
            DataSupport.updateAll(UserHospitalInfo.class, values, "RegisterId=?",
                    info.getRegisterId());
            info.save();
            L.i("保存用户医院信息成功");
        } else {
            info.save();
            L.i("保存用户医院信息成功");
        }
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
            DataSupport.deleteAll(UserCertificateInfo.class);
            DataSupport.deleteAll(UserHospitalInfo.class);
            L.i("用户所有信息表删除成功");
        } catch (Exception e) {
            L.i("用户所有信息表删除异常");
            e.printStackTrace();
        }
        L.i("清除用户信息成功");
    }
}
