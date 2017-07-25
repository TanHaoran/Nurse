package com.jerry.nurse.constant;

/**
 * Created by Jerry on 2017/7/20.
 */

public class ServiceConstant {

    // 请求返回类型
    public static final String REQUEST_SUCCESS = "0";
    public static final String USER_EXIST = "|";
    public static final String USER_NOT_EXIST = "Id:";

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
     * 注册方法
     * http://192.168.0.100:9400/UserService.svc/Register?Phone={Phone}&Password={Password}
     */
    public static final String SIGNUP = IP + "Register";

    /**
     * 登陆方法
     * http://192.168.0.100:9400/UserService.svc/Login?Phone={Phone}&Password={Password}
     */
    public static final String LOGIN = IP + "Login";

    /**
     * 获取用户信息
     */
    public static final String GET_USER_INFO = IP + "GetUserById";

    /**
     * 设置密码
     */
    public static final String SET_PASSWORD = IP + "SetPwdByPhone";

    public static final String CHANGE_AVATAR = "";
}
