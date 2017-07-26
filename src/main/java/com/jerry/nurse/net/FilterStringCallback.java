package com.jerry.nurse.net;

import com.jerry.nurse.util.ActivityCollector;
import com.jerry.nurse.util.L;
import com.jerry.nurse.util.StringUtil;
import com.jerry.nurse.util.T;
import com.zhy.http.okhttp.callback.StringCallback;

import okhttp3.Call;

/**
 * Created by Jerry on 2017/7/24.
 */

public abstract class FilterStringCallback extends StringCallback {

    protected abstract void onFilterError(Call call, Exception e, int id);

    public abstract void onFilterResponse(String response, int id);

    @Override
    public void onError(Call call, Exception e, int id) {
        L.e("请求失败：" + e.getMessage());
        T.showShort(ActivityCollector.getTopActivity(), "请求失败");
        onFilterError(call, e, id);
    }

    @Override
    public void onResponse(String response, int id) {
        response = StringUtil.dealJsonString(response);
        L.i("请求成功" + response);
        onFilterResponse(response, id);
    }
}
