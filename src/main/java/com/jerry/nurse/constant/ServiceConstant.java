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
     * 获取用户执业证信息
     */
    public static final String GET_USER_CERTIFICATE_INFO = IP + "GetUserPtccetById";

    /**
     * 获取用户医院相关信息
     */
    public static final String GET_USER_HOSPITAL_INFO = IP + "GetUserRelcodById";


    /**
     * 设置头像
     */
    public static final String SET_AVATAR = IP + "SetNickNameById";

    /**
     * 更新用户注册信息
     */
    public static final String UPDATE_REGISTER_INFO = IP + "UpdateUserRegisterInfo";

    /**
     * 更新用户基础信息
     */
    public static final String UPDATE_BASIC_INFO = IP + "Updateuserbasicinfo";

    /**
     * 修改用户执业证信息
     */
    public static final String UPDATE_CERTIFICATE_INFO = IP + "Updateuserbasicinfo";

    /**
     * 修改用户医院信息
     */
    public static final String UPDATE_HOSPITAL_INFO = IP + "UpdateuserrelrecordInfo";

    /**
     * 提交反馈信息
     */
    public static final String SEND_FEEDBACK = IP + "SetNickNameById";

    /**
     * 修改密码
     */
    public static final String CHANGE_PASSWORD = IP + "SetNickNameById";


    /**
     * 获取医院信息
     */
    public static final String GET_HOSPITAL_INFO = IP + "GethospitalById";


    /**
     * 获取科室信息
     */
    public static final String GET_OFFICE_INFO = IP + "GetdepartmentById";


    /**
     * 读取APP最新版本信息
     */
    public static final String GET_APP_VERSION = IP + "";
}
