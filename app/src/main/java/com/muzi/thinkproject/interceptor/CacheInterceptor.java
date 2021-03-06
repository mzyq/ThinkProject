package com.muzi.thinkproject.interceptor;

import com.muzi.thinkproject.App;
import com.muzi.thinkproject.utils.NetUtils;

import java.io.IOException;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by muzi on 2018/3/2.
 * 727784430@qq.com
 * <p>
 * 为okhttp添加缓存，这里是考虑到服务器不支持缓存时，从而让okhttp支持缓存
 */
public class CacheInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        // 有网络时 设置缓存超时时间1个小时
        int maxAge = 60 * 60;
        // 无网络时，设置超时为1天
        int maxStale = 60 * 60 * 24;
        Request request = chain.request();
        if (NetUtils.isNetworkAvailable(App.getInstance())) {
            //有网络时只从网络获取
            request = request.newBuilder().cacheControl(CacheControl.FORCE_NETWORK).build();
        } else {
            //无网络时只从缓存中读取
            request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
        }
        Response response = chain.proceed(request);
        if (NetUtils.isNetworkAvailable(App.getInstance())) {
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, max-age=" + maxAge)
                    .build();
        } else {
            response = response.newBuilder()
                    .removeHeader("Pragma")
                    .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                    .build();
        }
        return response;
    }
}
