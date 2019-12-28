package com_7idear.framework.utils;

import android.view.View;

import com_7idear.framework.R;


/**
 * 事件工具类
 * @author ieclipse 19-12-10
 * @description
 */
public class EventUtils {

    /**
     * 间隔时间——100毫秒
     */
    public static final long INTERVAL_100MS  = 100;
    /**
     * 间隔时间——200毫秒
     */
    public static final long INTERVAL_200MS  = 200;
    /**
     * 间隔时间——400毫秒
     */
    public static final long INTERVAL_400MS  = 400;
    /**
     * 间隔时间——600毫秒
     */
    public static final long INTERVAL_600MS  = 600;
    /**
     * 间隔时间——800毫秒
     */
    public static final long INTERVAL_800MS  = 800;
    /**
     * 间隔时间——1000毫秒
     */
    public static final long INTERVAL_1000MS = 1000;
    /**
     * 间隔时间——2500毫秒
     */
    public static final long INTERVAL_2500MS = 2500;

    private static long mLastTime; //时间

    /**
     * 是否在点击时间间隔区内
     * @return
     */
    public static boolean isClickTimeInterval() {
        return isTimeInterval(INTERVAL_600MS);
    }

    /**
     * 是否在时间间隔区内
     * @param intervalTime 间隔时间
     * @return
     */
    public static boolean isTimeInterval(long intervalTime) {
        long tmpTime = System.currentTimeMillis();
        if (mLastTime > tmpTime - intervalTime) {
            return true;
        }
        mLastTime = tmpTime;
        return false;
    }

    /**
     * 设置时间间隔区起始时间
     */
    public static void setTimeInterval() {
        mLastTime = System.currentTimeMillis();
    }


    /**
     * 获取多次点击任务监听器（多次点击任务时间间隔6秒）
     * @param totalCount 点击总次数
     * @param listener   监听器
     * @return
     */
    public static View.OnClickListener getClicksTaskListener(final int totalCount,
            final IEventClickCompleted listener) {

        View.OnClickListener clickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == null) {
                    listener.onClickCompleted();
                } else {
                    long time = 0;
                    int tCount = totalCount < 1 ? 1 : totalCount;
                    int cout = 0;
                    Object t = v.getTag(R.id.event_clicks_time);
                    Object c = v.getTag(R.id.event_clicks_count);
                    if (c instanceof Integer) {
                        cout = (int) c;
                    }
                    if (t == null) {
                        time = System.currentTimeMillis();
                        cout = 0;
                    } else if (t instanceof Long
                            && (long) t < System.currentTimeMillis() - 1000 * 6) {
                        time = System.currentTimeMillis();
                        cout = 0;
                    }
                    cout++;

                    if (time > 0) v.setTag(R.id.event_clicks_time, time);
                    if (cout >= tCount) {
                        v.setTag(R.id.event_clicks_time, null);
                        v.setTag(R.id.event_clicks_count, null);
                        if (listener != null) listener.onClickCompleted();
                    } else {
                        v.setTag(R.id.event_clicks_count, cout);
                    }

                }
            }
        };
        return clickListener;
    }

    public interface IEventClickCompleted {
        void onClickCompleted();
    }
}
