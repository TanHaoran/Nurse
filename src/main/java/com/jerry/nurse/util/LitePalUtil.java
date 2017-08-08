package com.jerry.nurse.util;

import android.content.Context;
import android.text.TextUtils;

import com.jerry.nurse.model.Announcement;
import com.jerry.nurse.model.Banner;
import com.jerry.nurse.model.LoginInfo;
import com.jerry.nurse.model.UserInfo;

import org.litepal.crud.DataSupport;

/**
 * Created by Jerry on 2017/7/26.
 */

public class LitePalUtil {

    /**
     * 保存用户登录信息
     *
     * @param context
     * @param loginInfo
     */
    public static void saveLoginInfo(Context context, LoginInfo loginInfo) {
        // 保存到SP
        updateSP(context, loginInfo);

        DataSupport.deleteAll(LoginInfo.class);
        loginInfo.save();
    }

    /**
     * 更新用户登录信息
     * @param context
     * @param loginInfo
     */
    public static void updateLoginInfo(Context context, LoginInfo loginInfo) {
        // 保存到SP
        updateSP(context, loginInfo);

        loginInfo.save();
    }


    /**
     * 保存用户个人信息
     *
     * @param context
     * @param userInfo
     */
    public static void saveUserInfo(Context context, UserInfo userInfo) {
        // 保存到SP
        updateSP(context, userInfo);

        DataSupport.deleteAll(UserInfo.class);
        userInfo.save();
    }

    /**
     * 更新用户个人信息
     * @param context
     * @param userInfo
     */
    public static void updateUserInfo(Context context, UserInfo userInfo) {
        // 保存到SP
        updateSP(context, userInfo);
        userInfo.save();
    }





    /**
     * 更新SP中的数据
     *
     * @param context
     * @param loginInfo
     */
    private static void updateSP(Context context, LoginInfo loginInfo) {
        if (!TextUtils.isEmpty(loginInfo.getRegisterId())) {
            SPUtil.put(context, SPUtil.REGISTER_ID, loginInfo.getRegisterId());
        }
        if (!TextUtils.isEmpty(loginInfo.getName())) {
            SPUtil.put(context, SPUtil.NAME, loginInfo.getName());
        }
        if (!TextUtils.isEmpty(loginInfo.getNickName())) {
            SPUtil.put(context, SPUtil.NICKNAME, loginInfo.getNickName());
        }
    }

    /**
     * 更新SP中的数据
     *
     * @param context
     * @param userInfo
     */
    private static void updateSP(Context context, UserInfo userInfo) {
        if (!TextUtils.isEmpty(userInfo.getRegisterId())) {
            SPUtil.put(context, SPUtil.REGISTER_ID, userInfo.getRegisterId());
        }
        if (!TextUtils.isEmpty(userInfo.getName())) {
            SPUtil.put(context, SPUtil.NAME, userInfo.getName());
        }
        if (!TextUtils.isEmpty(userInfo.getNickName())) {
            SPUtil.put(context, SPUtil.NICKNAME, userInfo.getNickName());
        }
    }

    public static void deleteAllInfo(Context context) {
        SPUtil.remove(context, SPUtil.NAME);
        SPUtil.remove(context, SPUtil.NICKNAME);
        SPUtil.remove(context, SPUtil.REGISTER_ID);

        // 清除用户表信息
        try {
            DataSupport.deleteAll(LoginInfo.class);
            DataSupport.deleteAll(Banner.class);
            DataSupport.deleteAll(Announcement.class);
            L.i("用户所有信息表删除成功");
        } catch (Exception e) {
            L.i("用户所有信息表删除异常");
            e.printStackTrace();
        }
    }
}
