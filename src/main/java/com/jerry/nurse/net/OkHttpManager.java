package com.jerry.nurse.net;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Jerry on 2017/7/16.
 * OkHttp封装类
 */
public class OkHttpManager {

    private static OkHttpManager sInstance;

    private OkHttpClient mClient;

    private Handler mHandler;

    /**
     * 获取单例
     *
     * @return
     */
    public static OkHttpManager getInstance() {
        if (sInstance == null) {
            synchronized (OkHttpManager.class) {
                if (sInstance == null) {
                    sInstance = new OkHttpManager();
                    return sInstance;
                }
            }
        }
        return sInstance;
    }

    private OkHttpManager() {
        mHandler = new Handler(Looper.getMainLooper());

        int cacheSize = 10 * 1024 * 1024;

        OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(20, TimeUnit.SECONDS);
        mClient = builder.build();
    }

    /**
     * 发送get请求
     *
     * @param url
     * @param callback
     */
    public void getNet(String url, ResultCallback callback) {
        Request request = new Request.Builder()
                .url(url)
                .method("GET", null)
                .build();
        dealNet(request, callback);
    }

    /**
     * 发送Post请求
     *
     * @param url      服务地址
     * @param callback 回调函数
     * @param params   参数
     */
    public void postNet(String url, ResultCallback callback, Param...
            params) {
        if (params == null) {
            params = new Param[0];
        }

        FormBody.Builder formBody = new FormBody.Builder();
        for (Param p : params) {
            formBody.add(p.key, p.value);
        }

        RequestBody requestBody = formBody.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        dealNet(request, callback);
    }

    /**
     * 发送网络请求
     *
     * @param request
     * @param callback
     */
    private void dealNet(final Request request, final ResultCallback callback) {
        mClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onFailed(request, e);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();

                final String data = string;
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onSuccess(data);
                    }
                });
            }
        });
    }

    /**
     * 回调函数
     */
    public static abstract class ResultCallback {
        public abstract void onFailed(Request request, IOException
                e);

        public abstract void onSuccess(String response);
    }

    /**
     * 参数封装类
     */
    public static class Param {
        String key;
        String value;

        public Param() {
        }

        public Param(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }


}
