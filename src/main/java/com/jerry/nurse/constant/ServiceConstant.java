package com.jerry.nurse.constant;

/**
 * Created by Jerry on 2017/7/20.
 */

public class ServiceConstant {

    // 请求返回类型
    public static final String REQUEST_SUCCESS = "0";
    public static final String USER_COLON = ":";
    public static final String USER_REGISTER_ID = "Id:";
    public static final String USER_PHONE = "Phone:";

    // 环信注册密码
    public static final String EASE_MOB_PASSWORD = "queYL798";


    public static final String IP = "http://192.168.0.100:9400/UserService.svc/";

    /**
     * 获取验证码
     * http://192.168.0.100:9400/UserService.svc/GetSMSCodeByPhone?Phone=18709269196&Type=1
     */
    public static final String GET_VERIFICATION_CODE = IP + "GetSMSCodeByPhone";

    /**
     * 验证验证码
     */
    public static final String VALIDATE_VERIFICATION_CODE = IP + "IsOKSMSCode";

    /**
     * 获取国家电话代码列表
     */
    public static final String GET_COUNTRIES = IP + "GetCountryCodeAll";

    /**
     * 登陆方法
     * http://192.168.0.100:9400/UserService.svc/Login?Phone={Phone}&Password={Password}
     */
    public static final String LOGIN = IP + "Login";

    /**
     * 设置密码
     */
    public static final String SET_PASSWORD = IP + "SetPwdByPhone";

    /**
     * 获取用户注册信息
     */
    public static final String GET_USER_REGISTER_INFO = IP + "GetUserReginfoById";

    /**
     * 获取用户基本信息
     */
    public static final String GET_USER_BASIC_INFO = IP + "GetUserBasicinfoById";


    /**
     * 设置头像
     */
    public static final String SET_AVATAR = IP + "SetNickNameById";

    /**
     * 设置昵称
     */
    public static final String SET_NICKNAME = IP + "SetNickNameById";

    /**
     * 设置性别
     */
    public static final String SET_SEX = "";
}
