package com.jerry.nurse.model;

/**
 * Created by Jerry on 2017/8/29.
 */

public class WeChatTokenResult {

    /**
     * access_token : meq1Ad_vGpRcwVKAFuNGpYInqfH5vhdd9kYIrd3sr19-SA8vdkJ0B92ncPx2YjFGbZO0NXW-jt1HCyJ8DGwGIQ
     * expires_in : 7200
     * refresh_token : v2XQCKos16LsBgrMCLON3y8kT6z4oAijMOmrvftsJrKUkhcgb3Bvi8lBlE0fY0MGenIb3eTVdhRO26aO6z43-Q
     * openid : oA1jk0u4mO35BZUqnD7TckWp5IOk
     * scope : snsapi_userinfo
     * unionid : oGNPF0q13hCQyV43lCmirbcBnSaI
     */

    private String access_token;
    private int expires_in;
    private String refresh_token;
    private String openid;
    private String scope;
    private String unionid;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(String refresh_token) {
        this.refresh_token = refresh_token;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}
