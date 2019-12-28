package com_7idear.framework.async;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

/**
 * 异步单一线程消息队列类
 * @author iEclipse 2019/7/3
 * @description
 */
public abstract class AsyncMsgQueueThread<T> {

    private HandlerThread mHandlerThread;
    private Handler       mHandler;

    public AsyncMsgQueueThread(String tag) {
        mHandlerThread = new HandlerThread(tag, Process.THREAD_PRIORITY_LOWEST);
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                onAsyncMessage(msg.what, msg.obj == null ? null : (T) msg.obj);
            }
        };
    }

    /**
     * 销毁
     */
    public void onDestory() {
        mHandler.removeCallbacksAndMessages(this);
        mHandler = null;
        mHandlerThread.quit();
        mHandlerThread = null;
    }

    /**
     * 发送消息（默认）
     * @param msg 消息
     * @return
     */
    public boolean sendMsg(T msg) {
        return sendMsg(0, msg, 0);
    }

    /**
     * 发送消息
     * @param action 标识
     * @param msg    消息
     * @return
     */
    public boolean sendMsg(int action, T msg) {
        return sendMsg(action, msg, 0);
    }

    /**
     * 发送消息
     * @param msg         消息
     * @param delayMillis 延时（毫秒）
     * @return
     */
    public boolean sendMsg(T msg, long delayMillis) {
        return sendMsg(0, msg, delayMillis);
    }

    /**
     * 发送消息
     * @param action      标识
     * @param msg         消息
     * @param delayMillis 延时（毫秒）
     * @return
     */
    public boolean sendMsg(int action, T msg, long delayMillis) {
        if (mHandler == null) return false;
        Message m = mHandler.obtainMessage();
        m.what = action;
        m.obj = msg;
        mHandler.sendMessageDelayed(m, delayMillis);
        return true;
    }

    /**
     * 移除消息（默认）
     */
    public void removeMsg() {
        removeMsg(0);
    }

    /**
     * 移除消息
     * @param action 标识
     */
    public void removeMsg(int action) {
        if (mHandler == null) return;
        mHandler.removeMessages(action);
    }

    /**
     * 异步消息
     * @param action 标识
     * @param msg    消息
     */
    protected abstract void onAsyncMessage(int action, T msg);
}
