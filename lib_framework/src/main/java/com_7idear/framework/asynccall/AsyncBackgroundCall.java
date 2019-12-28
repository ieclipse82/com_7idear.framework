package com_7idear.framework.asynccall;

import android.content.Context;

import com_7idear.framework.log.TimerFrameUtils;

import java.util.concurrent.Executor;

import retrofit2.Call;

/**
 * 异步后台任务类（异步任务工具类直接调用）
 * @author ieclipse 19-12-18
 * @description 支持后台任务，进度刷新，线程取消检查
 */
public class AsyncBackgroundCall<In, Out>
        extends AsyncBaseCall<Out>
        implements AsyncCallAdapterFactory.ImplBackgroundCall<In, Out> {

    private IBackgroundHelper<In> mHelper;

    AsyncBackgroundCall(Executor executor, Executor threadExecutor, Call call, boolean isLog) {
        super(executor, threadExecutor, call, isLog);
    }

    @Override
    public Call<Out> runBackgroundCall(final Context context, final In inEntity,
            final IBackgroundCallback<In, Out> callback) {
        TimerFrameUtils.timerFrame();
        log().append("start").append("callback", callback).toLogD("runBackgroundCall");
        AsyncCallUtils.getApi().addCall(context, this);

        //后台任务执行时的帮助对象，用于取消并退出线程或执行UI进度刷新
        mHelper = new IBackgroundHelper<In>() {
            @Override
            public boolean isCanceled() {
                return isCanceled;
            }

            @Override
            public void runUIProgress(final In inEntity, final int progress) {
                if (callback != null) {
                    log().append("refresh")
                         .append("callback", callback)
                         .append("inEntity", inEntity)
                         .append("progress", progress)
                         .toLogD("runBackgroundCall");
                    mUIExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            if (callback != null) callback.onUIProgress(inEntity, progress);
                        }
                    });
                }
            }
        };

        mThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                TimerFrameUtils.timerFrame();
                log().append("doing").append("callback", callback).toLogD("runBackgroundCall");

                if (callback != null) {
                    final Out outEntity = callback.onBackground(inEntity, mHelper);
                    if (mUIExecutor == null) {
                        log().append("error")
                             .append("callback", callback)
                             .append("mUIExecutor == null")
                             .toLogD("runBackgroundCall");
                        return;
                    }
                    mUIExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            TimerFrameUtils.timerFrame();
                            log().append("end")
                                 .append("callback", callback)
                                 .toLogD("runBackgroundCall");

                            AsyncCallUtils.getApi().removeCall(context, AsyncBackgroundCall.this);
                            if (callback != null) callback.onFinished(outEntity);
                        }
                    });
                }
            }
        });
        return this;
    }

}
