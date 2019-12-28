package com_7idear.framework.config;

import com_7idear.framework.asynccall.AsyncCallAdapterFactory;
import com_7idear.framework.asynccall.AsyncCallConverterFactory;
import com_7idear.framework.utils.TxtUtils;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * @author ieclipse 19-12-13
 * @description
 */
public abstract class AsyncCallConfig
        extends BaseConfig {

    protected void initAsyncCall(Retrofit.Builder builder, boolean isLog) {
        setLog(isLog);
        setInit(init(builder));
    }

    protected abstract boolean init(Retrofit.Builder builder);

    /**
     * 异步任务模块默认配置
     * @param serverUrl 服务器地址
     * @param list      拦截器
     * @return
     */
    public static Retrofit.Builder getDefaultConfig(String serverUrl, Interceptor... list) {
        if (TxtUtils.isEmpty(serverUrl)) return null;
        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
        if (list != null) {
            for (int i = 0; i < list.length; i++) {
                if (list[i] != null) clientBuilder.addInterceptor(list[i]);
            }
        }

        Retrofit.Builder builder = new Retrofit.Builder();
        builder.baseUrl(serverUrl);
        builder.addCallAdapterFactory(AsyncCallAdapterFactory.create());
        builder.addConverterFactory(new AsyncCallConverterFactory());
        builder.addConverterFactory(GsonConverterFactory.create());
        builder.client(clientBuilder.build());
        return builder;
    }
}
