package com.jerry.nurse.constant;

/**
 * Created by Jerry on 2017/7/20.
 */

public class ServiceConstant {

    // 请求返回类型
    public static final int RESPONSE_SUCCESS = 0;
    public static final String USER_COLON = ":";
    public static final String USER_NAME = "Name:";

    /**
     * 几种审核状态
     */
    // 未认证
    public static final int AUDIT_EMPTY = 0;
    // 认证中
    public static final int AUDIT_ING = 1;
    // 未通过
    public static final int AUDIT_FAILED = 2;
    // 已认证
    public static final int AUDIT_SUCCESS = 3;

    public static final String IP = "http://192.168.0.100:9400/UserService.svc/";

    public static final String UPLOAD_IP = "http://192.168.0.100:9400/";

    public static final String AVATAR_ADDRESS = "http://192.168.0.100:9400/Avatar/";
    public static final String PROFESSIONAL_ADDRESS = "http://192.168.0.100:9400/QC/";
    public static final String PRACTISING_ADDRESS = "http://192.168.0.100:9400/PC/";
    public static final String QR_CODE_ADDRESS = "http://192.168.0.100:9400/QRCode/";
    public static final String BANNER_ADDRESS = "http://192.168.0.100:9400/Banner/";

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
     * QQ第三方登陆
     */
    public static final String LOGIN_BY_QQ = IP + "ThirdPartLoginQQ";

    /**
     * 绑定手机号、QQ
     */
    public static final String BIND = IP + "Bind";

    /**
     * 解绑手机号、qq
     */
    public static final String UNBIND = IP + "UnBind";

    /**
     * 通过注册ID获取用户注册信息
     */
    public static final String GET_USER_REGISTER_INFO_BY_REGISTER_ID = IP + "GetUserReginfoById";

    /**
     * 获取二维码
     */
    public static final String GET_QR_CODE = IP + "GetQRCodeById";

    /**
     * 上传头像
     */
    public static final String UPLOAD_AVATAR = UPLOAD_IP + "AvatarImgHandler.ashx";

    /**
     * 上传专业资格证
     */
    public static final String UPLOAD_PROFESSIONAL_PICTURE = UPLOAD_IP + "QCImgHandler.ashx";

    /**
     * 上传执业证
     */
    public static final String UPLOAD_PRACTISING_PICTURE = UPLOAD_IP + "PCImgHandler.ashx";

    /**
     * 上传文件的方法
     */
    public static final String UPLOAD_FILE = IP + "ImgHandler.ashx";

    /**
     * 获取用户信息
     */
    public static final String GET_USER_INFO = IP + "GetUserInfo";

    /**
     * 获取专业资格证书
     */
    public static final String GET_PROFESSIONAL_CERTIFICATE_INFO = IP + "GetUserQuacetById";

    /**
     * 获取QQ昵称
     */
    public static final String GET_BIND_INFO = IP + "GetUserBindInfo";

    /**
     * 获取护士职业证书
     */
    public static final String GET_PRACTISING_CERTIFICATE_INFO = IP + "GetUserPtccetById";

    /**
     * 获取用户医院信息
     */
    public static final String GET_USER_HOSPITAL_INFO = IP + "GetUserRelcodById";

    /**
     * 更新用户注册信息
     */
    public static final String UPDATE_REGISTER_INFO = IP + "UpdateUserRegisterInfo";

    /**
     * 更新用户基础信息
     */
    public static final String UPDATE_BASIC_INFO = IP + "Updateuserbasicinfo";

    /**
     * 更新专业资格证信息
     */
    public static final String UPDATE_PROFESSIONAL_CERTIFICATE_INFO = IP + "UpdateuserquacertificateInfo";

    /**
     * 更新用户执业证信息
     */
    public static final String UPDATE_PRACTISING_CERTIFICATE_INFO = IP + "UpdateuserpracticecertificateInfo";

    /**
     * 更新用户医院信息
     */
    public static final String UPDATE_HOSPITAL_INFO = IP + "UpdateuserrelrecordInfo";

    /**
     * 提交反馈信息
     */
    public static final String SEND_FEEDBACK = IP + "AddfeedbackInfo";

    /**
     * 修改密码
     */
    public static final String CHANGE_PASSWORD = IP + "ResetPassword";

    /**
     * 获取医院信息
     */
    public static final String GET_NEARBY_HOSPITAL_LIST = IP + "GetAddressByLngLat";

    /**
     * 获取科室信息
     */
    public static final String GET_OFFICE_LIST = IP + "GetDepartmentList";


    /**
     * 读取APP最新版本信息
     */
    public static final String GET_APP_VERSION = IP + "GetReleaseversionInfo";

    /**
     * 根据科室查询所有联系人
     */
    public static final String GET_CONTACT_LIST_BY_OFFICE_ID = IP + "GetContactByHopDepId";

    /**
     * 获取公告的方法
     */
    public static final String GET_ANNOUNCEMENT = IP + "GetNotice";

    /**
     * 获取Banner的方法
     */
    public static final String GET_BANNER = IP + "GetBanner";

    /**
     * 获取用户登陆信息
     */
    public static final String GET_USER_LOGIN_INFO = IP + "GetUserFirstInfoById";

    /**
     * 根据关键字搜索用户
     */
    public static final String SEARCH_USER_BY_KEY_WORD = IP + "GetFriendsList";

    /**
     * 查询用户详细信息
     */
    public static final String GET_USER_DETAIL_INFO = IP + "GetFriendInfo";

    /**
     * 获取好友列表
     */
    public static final String GET_FRIEND_LIST = IP + "GetEMFriend";

    /**
     * 获取群信息
     */
    public static final String GET_GROUP_LIST = IP + "GetXHGroupInfoList";

    /**
     * 成为好友
     */
    public static final String ADD_AS_FRIEND = IP + "AddEMFriend";

    /**
     * 删除好友
     */
    public static final String DELETE_FRIEND = IP + "DeleteEMFriend";

    /**
     * 获取手机联系人信息
     */
    public static final String GET_CELLPHONE_CONTACT = IP + "GetContactInfo";

    /**
     * 创建群组
     */
    public static final String CREATE_GROUP = IP + "CreateGroup";


}
