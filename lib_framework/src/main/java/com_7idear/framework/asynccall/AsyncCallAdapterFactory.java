package com_7idear.framework.asynccall;

import android.content.Context;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Retrofit;

/**
 * 异步任务适配工厂
 * @author ieclipse 19-12-18
 * @description 实现网络请求和后台任务统一调度机制，Retrofit.Builder.addCallAdapterFactory中添加自定义适配工厂
 */
public final class AsyncCallAdapterFactory
        extends CallAdapter.Factory {

    /**
     * 创建实例
     * @return
     */
    public static CallAdapter.Factory create() {
        return new AsyncCallAdapterFactory();
    }

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (getRawType(returnType) != ImplUrlCall.class || retrofit == null) {
            return null;
        }

        final Type responseType = getParameterUpperBound(0, (ParameterizedType) returnType);
        //实例一个新的适配器
        return new AsyncCallAdapter<>(retrofit.callbackExecutor(), responseType);
    }

    /**
     * 异步任务适配器
     * @author ieclipse 19-12-18
     * @description
     */
    private static final class AsyncCallAdapter<T>
            implements CallAdapter<T, AsyncUrlCall<T>> {

        private final Executor executor; //UI执行器
        private final Type     responseType; //返回类型

        AsyncCallAdapter(Executor executor, Type responseType) {
            this.executor = executor;
            this.responseType = responseType;
        }

        @Override
        public Type responseType() {
            return responseType;
        }

        @Override
        public AsyncUrlCall<T> adapt(Call<T> call) {
            //每次网络接口调用实例一个新的异步网址任务
            return new AsyncUrlCall<>(executor, call, AsyncCallUtils.getApi().isLog());
        }
    }

    /**
     * 网址任务（基类实现）
     * @param <T> 输出对象（必须）
     */
    public interface ImplUrlCall<T>
            extends Call<T> {
        /**
         * 执行网址任务
         * @param context  环境对象
         * @param callback 回调
         * @return
         */
        Call<T> runUrlCall(Context context, IUrlCallback<T> callback);
    }

    /**
     * 后台任务（基类实现）
     * @param <In>  输入对象（必须）
     * @param <Out> 输出对象（必须）
     */
    public interface ImplBackgroundCall<In, Out>
            extends Call<Out> {
        /**
         * 执行后台任务
         * @param context  环境对象
         * @param inEntity 输入对象
         * @param callback 输出对象
         * @return
         */
        Call<Out> runBackgroundCall(Context context, In inEntity,
                IBackgroundCallback<In, Out> callback);
    }

}
