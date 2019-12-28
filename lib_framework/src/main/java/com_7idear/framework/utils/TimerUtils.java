package com_7idear.framework.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 计时器工具类
 * @author ieclipse 19-12-10
 * @description
 */
public class TimerUtils {

    /**
     * 类型——添加
     */
    private static final int TYPE_ADD        = 0;
    /**
     * 类型——删除
     */
    private static final int TYPE_REMOVE     = 1;
    /**
     * 类型——删除全部
     */
    private static final int TYPE_REMOVE_ALL = 2;

    private static TimerUtils mInstance;

    private SparseArray<Timer>                mDelayTimer; //延时计时器列表
    private SparseArray<Timer>                mPeriodTimer; //重复计时器列表
    private SparseArray<List<ITimerListener>> mDelayTimerListener; //延时计时器监听器列表
    private SparseArray<List<ITimerListener>> mPeriodTimerListener; //重复计时器监听器列表

    public TimerUtils() {
        init();
    }

    public static TimerUtils getInstance() {
        if (mInstance == null) {
            synchronized (TimerUtils.class) {
                if (mInstance == null) mInstance = new TimerUtils();
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     */
    public void init() {
        if (mDelayTimer == null) mDelayTimer = new SparseArray<Timer>();
        if (mPeriodTimer == null) mPeriodTimer = new SparseArray<Timer>();
        if (mDelayTimerListener == null)
            mDelayTimerListener = new SparseArray<List<ITimerListener>>();
        if (mPeriodTimerListener == null)
            mPeriodTimerListener = new SparseArray<List<ITimerListener>>();
    }

    /**
     * 添加延时计时器监听器
     * @param delaySecond 延时秒
     * @param listener    监听器
     */
    public void addDelayTimer(final int delaySecond, final ITimerListener listener) {
        if (addOrRemoveTimerListener(mDelayTimerListener, delaySecond, listener, TYPE_ADD)) {
            Timer timer = mDelayTimer.get(delaySecond);
            if (timer == null) {
                timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        mDelayHandler.sendEmptyMessage(delaySecond);

                        stopTimer(mDelayTimer.get(delaySecond));
                        mDelayTimer.delete(delaySecond);
                    }
                }, delaySecond * 1000);
                mDelayTimer.put(delaySecond, timer);
            }
        }
    }

    /**
     * 添加重复计时器监听器
     * @param periodSecond 重复秒
     * @param listener     监听器
     */
    public void addPeriodTimer(final int periodSecond, final ITimerListener listener) {
        if (addOrRemoveTimerListener(mPeriodTimerListener, periodSecond, listener, TYPE_ADD)) {
            Timer timer = mPeriodTimer.get(periodSecond);
            if (timer == null) {
                timer = new Timer();
                timer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        mPeriodHandler.sendEmptyMessage(periodSecond);
                    }
                }, 0, periodSecond * 1000);
                mPeriodTimer.put(periodSecond, timer);
            }
        }
    }

    /**
     * 移除延时计时器监听器
     * @param delaySecond 延时秒
     * @param listener    监听器
     */
    public void removeDelayTimer(int delaySecond, ITimerListener listener) {
        if (addOrRemoveTimerListener(mDelayTimerListener, delaySecond, listener, TYPE_REMOVE)) {
            if (mDelayTimerListener.get(delaySecond).size() == 0) {
                stopTimer(mDelayTimer.get(delaySecond));
                mDelayTimer.delete(delaySecond);
            }
        }
    }

    /**
     * 移除重复计时器监听器
     * @param periodSecond 重复秒
     * @param listener     监听器
     */
    public void removePeriodTimer(int periodSecond, ITimerListener listener) {
        if (addOrRemoveTimerListener(mPeriodTimerListener, periodSecond, listener, TYPE_REMOVE)) {
            if (mPeriodTimerListener.get(periodSecond).size() == 0) {
                stopTimer(mPeriodTimer.get(periodSecond));
                mPeriodTimer.delete(periodSecond);
            }
        }
    }

    /**
     * 移除全部重复（秒）计时器监听器
     * @param periodSecond 重复秒
     */
    public void removePeriodTimer(int periodSecond) {
        if (addOrRemoveTimerListener(mPeriodTimerListener, periodSecond, null, TYPE_REMOVE_ALL)) {
            if (mPeriodTimerListener.get(periodSecond).size() == 0) {
                stopTimer(mPeriodTimer.get(periodSecond));
                mPeriodTimer.delete(periodSecond);
            }
        }
    }

    /**
     * 移除全部延时计时器
     */
    public void removeDelayTimer() {
        removeTimer(mDelayTimer);
        removeTimerListener(mDelayTimerListener);
    }

    /**
     * 移除全部重复计时器
     */
    public void removePeriodTimer() {
        removeTimer(mPeriodTimer);
        removeTimerListener(mPeriodTimerListener);
    }

    /**
     * 移除全部计时器
     */
    public void removeAllTimer() {
        removeTimer(mDelayTimer);
        removeTimer(mPeriodTimer);
        removeTimerListener(mDelayTimerListener);
        removeTimerListener(mPeriodTimerListener);
    }

    /**
     * 添加或移除计时器监听器
     * @param timerListener 计时器
     * @param second        秒
     * @param listener      监听器
     * @param type          类型
     * @return
     */
    private boolean addOrRemoveTimerListener(SparseArray<List<ITimerListener>> timerListener,
            final int second, ITimerListener listener, int type) {
        if (second <= 0 || timerListener == null) {
            return false;
        }
        List<ITimerListener> list = timerListener.get(second);
        if (list == null) {
            list = new ArrayList<ITimerListener>();
        }
        synchronized (list) {
            switch (type) {
                case TYPE_ADD:
                    if (list.contains(listener)) {
                    } else {
                        list.add(listener);
                    }
                    break;
                case TYPE_REMOVE:
                    list.remove(listener);
                    break;
                case TYPE_REMOVE_ALL:
                    list.clear();
                    break;
            }
        }

        timerListener.put(second, list);
        return true;
    }

    /**
     * 移除计时器
     * @param timerArray 计时器列表
     */
    private void removeTimer(SparseArray<Timer> timerArray) {
        if (timerArray != null) {
            for (int i = 0, c = timerArray.size(); i < c; i++) {
                Timer tmp = timerArray.valueAt(i);
                stopTimer(tmp);
            }
            timerArray.clear();
        }
    }

    /**
     * 移除计时器监听器
     * @param timerListener 计时器监听器列表
     */
    private void removeTimerListener(SparseArray<List<ITimerListener>> timerListener) {
        if (timerListener != null) {
            timerListener.clear();
        }
    }

    /**
     * 停止计时器
     * @param timer 计时器
     */
    private void stopTimer(Timer timer) {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    /**
     * 延时刷新对象
     */
    private Handler mDelayHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (mDelayTimerListener != null) {
                List<ITimerListener> list = mDelayTimerListener.get(msg.what);
                if (EntityUtils.isNotEmpty(list)) {
                    List<ITimerListener> timerList = new ArrayList<ITimerListener>();
                    timerList.addAll(list);
                    mDelayTimerListener.get(msg.what).clear();
                    for (ITimerListener timer : timerList) {
                        timer.onTimer();
                    }
                }
            }
        }

    };

    /**
     * 重复刷新对象
     */
    private Handler mPeriodHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            if (mPeriodTimerListener != null) {
                List<ITimerListener> list = mPeriodTimerListener.get(msg.what);
                if (EntityUtils.isNotEmpty(list)) {
                    List<ITimerListener> timerList = new ArrayList<ITimerListener>();
                    timerList.addAll(list);
                    for (ITimerListener timer : timerList) {
                        timer.onTimer();
                    }
                }
            }
        }

    };

    /**
     * 计时器接口
     * @author DZH 2015年9月22日
     * @description
     */
    public interface ITimerListener {
        /**
         * 时间到
         */
        void onTimer();
    }
}
