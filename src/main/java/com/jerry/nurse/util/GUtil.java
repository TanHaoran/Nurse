package com.jerry.nurse.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;

/**
 * Created by Jerry on 2017/8/7.
 */

public class GUtil {


    public GUtil() {
    }

    public <T> T fromJson(String json, Class<T> classOfT) {
        T t = null;
        try {
            t = new Gson().fromJson(json, classOfT);
        } catch (JsonSyntaxException e) {
            L.i("解析Json出现异常");
            e.printStackTrace();
        }
        return t;
    }

    public <T> T fromJson(String json, Type typeOfT) {
        T t = null;
        try {
            t = new Gson().fromJson(json, typeOfT);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return t;
    }


}
