package com.jerry.nurse.constant;

/**
 * Created by Jerry on 2017/7/20.
 */

public class ServiceMethod {


    public static final String IP = "http://192.168.0.100:9400/Aers.svc/";

    /**
     * 获取验证码
     * http://192.168.0.100:9400/Aers.svc/GetSMSCodeByPhone?Phone=13002909620
     */
    public static final String GET_VERIFICATION_CODE = IP + "GetSMSCodeByPhone";

    /**
     * 验证验证码
     */
    public static final String VALIDATE_VERIFICATION_CODE = IP + "IsOKSMSCode";

    /**
     * 注册方法
     */
    public static final String SIGNUP = IP + "";

    /**
     * 登陆方法
     */
    public static final String LOGIN = IP + "";


}
