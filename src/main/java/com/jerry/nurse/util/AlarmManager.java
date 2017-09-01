package com.jerry.nurse.util;

import android.content.Context;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

/**
 * Created by Jerry on 2017/9/1.
 */

public class AlarmManager {

    /**
     * 播放系统提示音
     *
     * @param context
     */
    public static void playAlarm(Context context) {
        boolean alarmOn = (boolean) SPUtil.get(context, SPUtil.ALARM_ON, false);
        if (alarmOn) {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            if (notification == null) return;
            Ringtone r = RingtoneManager.getRingtone(context, notification);
            r.play();
        }
    }
}
