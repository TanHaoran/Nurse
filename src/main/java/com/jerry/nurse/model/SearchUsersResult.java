package com.jerry.nurse.model;

import android.text.TextUtils;

import java.util.List;

import static com.jerry.nurse.constant.ServiceConstant.AVATAR_ADDRESS;

/**
 * Created by Jerry on 2017/8/9.
 */

public class SearchUsersResult {


    /**
     * body : [{"Avatar":"","Name":"","NickName":"ym","Password":"12345","Phone":"441","RegisterId":"0000000046"},{"Avatar":"","Name":"ym","NickName":"","Password":"","Phone":"13991853237","RegisterId":"0000000048"}]
     * code : 0
     * msg :
     */

    private int code;
    private String msg;
    private List<User> body;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<User> getBody() {
        return body;
    }

    public void setBody(List<User> body) {
        this.body = body;
    }

    public static class User {
        /**
         * Avatar :
         * Name :
         * NickName : ym
         * Password : 12345
         * Phone : 441
         * RegisterId : 0000000046
         */

        private String Avatar;
        private String Name;
        private String NickName;
        private String Password;
        private String Phone;
        private String RegisterId;

        public String getAvatar() {
            if (!TextUtils.isEmpty(Avatar)) {
                if (Avatar.startsWith("http")) {
                    return Avatar;
                } else {
                    return AVATAR_ADDRESS + Avatar;
                }
            }
            return "";
        }

        public void setAvatar(String Avatar) {
            this.Avatar = Avatar;
        }

        public String getName() {
            return Name;
        }

        public void setName(String Name) {
            this.Name = Name;
        }

        public String getNickName() {
            return NickName;
        }

        public void setNickName(String NickName) {
            this.NickName = NickName;
        }

        public String getPassword() {
            return Password;
        }

        public void setPassword(String Password) {
            this.Password = Password;
        }

        public String getPhone() {
            return Phone;
        }

        public void setPhone(String Phone) {
            this.Phone = Phone;
        }

        public String getRegisterId() {
            return RegisterId;
        }

        public void setRegisterId(String RegisterId) {
            this.RegisterId = RegisterId;
        }


    }
}
