package com.jerry.nurse.util;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * Created by Jerry on 2017/7/20.
 */

public class DeviceUtil {

    /**
     * 获取设备唯一标识码
     *
     * @param context
     * @return
     */
    public static String getDeviceId(Context context) {
        final TelephonyManager tm = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        return deviceId;
    }
}
