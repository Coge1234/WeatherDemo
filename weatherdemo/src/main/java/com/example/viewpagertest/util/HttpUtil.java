package com.example.viewpagertest.util;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/2/7.
 */

public class HttpUtil {
    private final static long TIMES=5000;
    public static void sendOkHttpRequest(String url_address, okhttp3.Callback callback){
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(TIMES, TimeUnit.MILLISECONDS)
                .readTimeout(TIMES, TimeUnit.MILLISECONDS)
                .writeTimeout(TIMES, TimeUnit.MILLISECONDS).build();
        Request request = new Request.Builder().url(url_address).build();
        client.newCall(request).enqueue(callback);
    }
}
