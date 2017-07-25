package com.jerry.nurse.util;

import com.google.gson.Gson;

/**
 * Created by Jerry on 2017/7/22.
 */

public class StringUtil {

    /**
     * 过滤掉返回值带有的反斜杠和开头结尾的双引号
     *
     * @param json
     * @return
     */
    public static String dealJsonString(String json) {
        json = json.replace("\\", "");
        if (json.startsWith("\"")) {
            json = json.substring(1, json.length() - 1);
        }
        return json;
    }

    /**
     * 将实体添加model返回json字符串
     *
     * @param o
     * @return
     */
    public static String addModelWithJson(Object o) {
        return "{\"model\":" + new Gson().toJson(o) + "}";
    }
}
