package com.jerry.nurse.util;

import android.content.ContentValues;
import android.content.Context;
import android.text.TextUtils;

import com.jerry.nurse.model.User;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/7/26.
 */

public class UserUtil {

    /**
     * 保存用户信息
     *
     * @param context
     * @param user
     */
    public static void saveUser(Context context, User user) {

        // 保存到SP
        updateSP(context, user);

        // 清除用户表信息
        try {
            DataSupport.deleteAll(User.class);
            L.i("用户表删除成功");
        } catch (Exception e) {
            L.i("用户表已经为空");
            e.printStackTrace();
        }
        user.save();
        L.i("保存用户信息成功");
    }

    /**
     * 更新SP中的数据
     *
     * @param context
     * @param user
     */
    private static void updateSP(Context context, User user) {
        if (!TextUtils.isEmpty(user.getPhone())) {
            SPUtil.put(context, SPUtil.CELLPHONE, user.getPhone());
        }
        if (!TextUtils.isEmpty(user.getName())) {
            SPUtil.put(context, SPUtil.NAME, user.getName());
        }
        if (!TextUtils.isEmpty(user.getNickName())) {
            SPUtil.put(context, SPUtil.NICKNAME, user.getNickName());
        }
        if (!TextUtils.isEmpty(user.getRegisterId())) {
            SPUtil.put(context, SPUtil.REGISTER_ID, user.getRegisterId());
        }
    }

    public static void deleteUser(Context context) {
        SPUtil.remove(context, SPUtil.CELLPHONE);
        SPUtil.remove(context, SPUtil.NAME);
        SPUtil.remove(context, SPUtil.NICKNAME);
        SPUtil.remove(context, SPUtil.REGISTER_ID);

        // 清除用户表信息
        try {
            DataSupport.deleteAll(User.class);
            L.i("用户表删除成功");
        } catch (Exception e) {
            L.i("用户表已经为空");
            e.printStackTrace();
        }
        L.i("清除用户信息成功");
    }

    /**
     * 更新用户信息
     * @param context
     * @param user
     */
    public static void updateUser(Context context, User user) {
        updateSP(context, user);
        ContentValues values = new ContentValues();
        values.put("RegisterId", user.getRegisterId());
        DataSupport.updateAll(User.class, values, "RegisterId=?", user.getRegisterId());

        user.saveAsync();
    }
}
