package com.jerry.nurse.util;

import android.app.Activity;
import android.webkit.JavascriptInterface;

/**
 * Created by Jerry on 2017/8/9.
 */

public class BackUtil extends Object {

    /**
     * 关闭Activity
     */
    @JavascriptInterface
    public void back() {
        L.i("进入BackUtil方法");
        Activity activity = ActivityCollector.getTopActivity();
        ActivityCollector.removeActivity(activity);
    }


}
