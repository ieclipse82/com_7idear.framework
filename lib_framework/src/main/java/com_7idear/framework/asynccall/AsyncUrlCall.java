package com_7idear.framework.asynccall;

import android.content.Context;

import com_7idear.framework.log.TimerFrameUtils;

import java.util.concurrent.Executor;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 异步网络请求任务类
 * @author ieclipse 19-12-18
 * @description
 */
public class AsyncUrlCall<T>
        extends AsyncBaseCall<T>
        implements AsyncCallAdapterFactory.ImplUrlCall<T> {

    AsyncUrlCall(Executor executor, Call call, boolean isLog) {
        super(executor, call, isLog);
    }

    @Override
    public Call<T> runUrlCall(final Context context, final IUrlCallback<T> callback) {
        TimerFrameUtils.timerFrame();
        log().append("start").append("callback", callback).toLogD("runUrlCall");
        AsyncCallUtils.getApi().addCall(context, this);

        mCall.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, final Response<T> response) {
                mUIExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        TimerFrameUtils.timerFrame();
                        log().append("end")
                             .append("onFinished")
                             .append("callback", callback)
                             .toLogD("runUrlCall");

                        AsyncCallUtils.getApi().removeCall(context, AsyncUrlCall.this);
                        if (callback != null) callback.onFinished(response.body());
                    }
                });
            }

            @Override
            public void onFailure(Call<T> call, final Throwable t) {
                mUIExecutor.execute(new Runnable() {
                    @Override
                    public void run() {
                        TimerFrameUtils.timerFrame();
                        log().append("end")
                             .append("onFailure")
                             .append("callback", callback)
                             .append("Throwable", t)
                             .toLogD("runUrlCall");

                        AsyncCallUtils.getApi().removeCall(context, AsyncUrlCall.this);
                        if (callback != null) {
                            if (t instanceof AsyncCallException) {
                                callback.onFailure((AsyncCallException) t);
                            } else if ("Canceled".equalsIgnoreCase(t.getMessage())) {
                                callback.onFailure(new AsyncCallException(
                                        AsyncCallException.ERRCODE_CANCELED));
                            } else {
                                callback.onFailure(new AsyncCallException(
                                        AsyncCallException.ERRCODE_IOEXCEPTION));
                            }
                        }
                    }
                });
            }
        });
        return this;
    }
}
