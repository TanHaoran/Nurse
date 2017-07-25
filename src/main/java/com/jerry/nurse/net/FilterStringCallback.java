package com.jerry.nurse.net;

import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.zhy.http.okhttp.callback.StringCallback;

/**
 * Created by Jerry on 2017/7/24.
 */

public abstract class FilterStringCallback extends StringCallback {


    @Override
    public void onResponse(String response, int id) {
        response = StringUtil.dealJsonString(response);
        L.i(response);
        onFilterResponse(response, id);
    }

    public abstract void onFilterResponse(String response, int id);
}
