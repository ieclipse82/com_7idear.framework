package com_7idear.framework.asynccall;

import com_7idear.framework.log.LogEntity;

import java.io.IOException;
import java.util.concurrent.Executor;

import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 异步基础任务类
 * @author ieclipse 19-12-18
 * @description
 */
public abstract class AsyncBaseCall<T>
        implements Call<T> {

    protected final Executor mUIExecutor; //UI线程池
    protected final Executor mThreadExecutor; //异步线程池
    protected final Call<T>  mCall; //任务对象

    protected boolean isCanceled; //是否取消
    protected boolean isLog; //是否输出日志

    AsyncBaseCall(Executor executor, Call<T> call, boolean isLog) {
        this(executor, null, call, isLog);
    }

    AsyncBaseCall(Executor executor, Executor threadExecutor, Call call, boolean isLog) {
        this.mUIExecutor = executor;
        this.mThreadExecutor = threadExecutor;
        this.mCall = call;
        this.isLog = isLog;
    }

    @Override
    public Response<T> execute()
            throws IOException {
        return mCall != null ? mCall.execute() : null;
    }

    @Override
    public void enqueue(Callback<T> callback) {
    }

    @Override
    public boolean isExecuted() {
        return mCall != null ? mCall.isExecuted() : false;
    }

    @Override
    public void cancel() {
        if (mCall != null) mCall.cancel();
        isCanceled = true;
    }

    @Override
    public boolean isCanceled() {
        return mCall != null ? mCall.isCanceled() : isCanceled;
    }

    @Override
    public Call<T> clone() {
        return mCall != null ? mCall.clone() : null;
    }

    @Override
    public Request request() {
        return mCall != null ? mCall.request() : null;
    }

    protected LogEntity log() {
        return new LogEntity(isLog);
    }

}
