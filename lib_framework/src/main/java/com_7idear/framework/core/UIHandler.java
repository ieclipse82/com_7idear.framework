package com_7idear.framework.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * UI刷新辅助类
 * @author ieclipse 19-11-29
 * @description 通过runUIMessage实现UI刷新
 */
public class UIHandler {

    private ImplBaseActivity mListener; //基础界面接口
    private Handler          mHandler; //UI刷新对象

    public UIHandler(ImplBaseActivity listener) {
        mListener = listener;
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                if (mListener != null) mListener.onBaseUIRefresh(null, msg.what, msg.obj);
            }
        };
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        if (mHandler != null) mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        mListener = null;
    }

    /**
     * 执行UI刷新
     * @param what 标识
     */
    public void runUIMessage(int what) {
        runUIMessage(what, null, 0);
    }

    /**
     * 执行UI刷新
     * @param what        标识
     * @param delayMillis 延时（毫秒）
     */
    public void runUIMessage(int what, long delayMillis) {
        runUIMessage(what, null, delayMillis);
    }

    /**
     * 执行UI刷新
     * @param what 标识
     * @param obj  对象
     */
    public void runUIMessage(int what, Object obj) {
        runUIMessage(what, obj, 0);
    }

    /**
     * 执行UI刷新
     * @param what        标识
     * @param obj         对象
     * @param delayMillis 延时（毫秒）
     */
    public void runUIMessage(int what, Object obj, long delayMillis) {
        runUIMessage(mHandler == null ? null : mHandler.obtainMessage(what, obj), delayMillis);
    }

    /**
     * 执行UI刷新
     * @param msg 消息
     */
    public void runUIMessage(Message msg) {
        runUIMessage(msg, 0);
    }

    /**
     * 执行UI刷新
     * @param msg         消息
     * @param delayMillis 延时（毫秒）
     */
    public void runUIMessage(Message msg, long delayMillis) {
        if (msg == null || mHandler == null) return;
        mHandler.removeMessages(msg.what);
        mHandler.sendMessageDelayed(msg, delayMillis);
    }

    /**
     * 移除UI消息
     * @param what 标识
     */
    public void removeUIMessages(int what) {
        if (mHandler != null) mHandler.removeMessages(what);
    }

    /**
     * UI刷新接口
     */
    public interface ImplUIHandlerListener {
        /**
         * 获取UI刷新对象
         * @return
         */
        UIHandler getUIHandler();
    }
}
