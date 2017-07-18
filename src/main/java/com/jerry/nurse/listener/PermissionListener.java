package com.jerry.nurse.listener;

import java.util.List;

/**
 * Created by Jerry on 2016/12/29.
 * <p>
 * 用来监听申请权限回调的接口
 */
public interface PermissionListener {

    /**
     * 请求权限同意
     */
    void onGranted();

    /**
     * 请求权限拒绝
     *
     * @param deniedPermission 被拒绝的权限集合
     */
    void onDenied(List<String> deniedPermission);

}
