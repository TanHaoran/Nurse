package com.jerry.nurse.constant;

/**
 * Created by Jerry on 2017/7/20.
 */

public class ServiceConstant {
    // 微信AppId
    public static String WX_APP_ID = "wx96c5f62ab4cd1676";
    // 微信AppSecret
    public static final String WX_APP_SECRET = "9495559c8f0bb79577076d6e88adece6";

    public static final String SINA_APP_KEY = "3324408859";
    public static final String SINA_REDIRECT_URL = "http://api.weibo.com/oauth?/default.html";
    public static final String SINA_SCOPE =
            "email,direct_messages_read,direct_messages_write,"
                    + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
                    + "follow_app_official_microblog," + "invitation_write";

    // 请求返回类型
    public static final int RESPONSE_SUCCESS = 0;

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

    public static final String MAIN_IP = "http://zh.buzzlysoft.com/";

    public static final String EVENT_REPORT_IP = MAIN_IP;

    public static final String IP = MAIN_IP + "UserService.svc/";


    public static final String AVATAR_ADDRESS = MAIN_IP + "Avatar/";
    public static final String PROFESSIONAL_ADDRESS = MAIN_IP + "QC/";
    public static final String PRACTISING_ADDRESS = MAIN_IP + "PC/";
    public static final String QR_CODE_ADDRESS = MAIN_IP + "QRCode/";
    public static final String BANNER_ADDRESS = MAIN_IP + "Banner/";
    public static final String APK_ADDRESS = MAIN_IP + "APK/";

    // 服务协议
    public static final String PROTOCOL_URL = "http://zhihu.buzzlysoft.com/agreement.html";

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
     * 使用院内账号登录
     */
    public static final String HOSPITAL_LOGIN = IP + "ThirdPartLoginHospital";

    /**
     * QQ第三方登陆
     */
    public static final String LOGIN_BY_QQ = IP + "ThirdPartLoginQQ";

    /**
     * 微信第三方登陆
     */
    public static final String LOGIN_BY_WX = IP + "ThirdPartLoginWeixin";

    /**
     * 微博第三方登陆
     */
    public static final String LOGIN_BY_WB = IP + "ThirdPartLoginWeibo";

    /**
     * 绑定手机号、QQ
     */
    public static final String BIND = IP + "Bind";

    /**
     * 解绑手机号、qq
     */
    public static final String UNBIND = IP + "UnBind";

    /**
     * 获取二维码
     */
    public static final String GET_QR_CODE = IP + "GetQRCodeById";

    /**
     * 上传头像
     */
    public static final String UPLOAD_AVATAR = MAIN_IP + "AvatarImgHandler.ashx";

    /**
     * 上传专业资格证
     */
    public static final String UPLOAD_PROFESSIONAL_PICTURE = MAIN_IP + "QCImgHandler.ashx";

    /**
     * 上传执业证
     */
    public static final String UPLOAD_PRACTISING_PICTURE = MAIN_IP + "PCImgHandler.ashx";

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


    /**
     * 获取某个群组的信息
     */
    public static final String GET_GROUP_INFO = IP + "GetXHGroupList";

    /**
     * 添加群成员
     */
    public static final String ADD_GROUP_MEMBER = IP + "AddGroupMember";

    /**
     * 退出群聊
     */
    public static final String QUIT_GROUP = IP + "QuitGroup";


    /**
     * 修改群昵称
     */
    public static final String UPDATE_GROUP_NICKNAME = IP + "UpdateGroupNickName";


    /**
     * 获取微信登录Token
     */
    public static final String WECHAT_GET_TOKEN = "https://api.weixin.qq.com/sns/oauth2/access_token";

    /**
     * 获取微信账户基本信息
     */
    public static final String WECHAT_GET_USER_INFO = "https://api.weixin.qq.com/sns/userinfo";

    /**
     * 获取微博基本信息
     */
    public static final String MICRO_BLOG_GET_USER_INFO = "https://api.weibo.com/2/users/show.json";

}
