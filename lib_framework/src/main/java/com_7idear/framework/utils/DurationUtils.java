package com_7idear.framework.utils;

import android.util.Log;

import java.util.HashMap;

/**
 * 时长检查工具类
 * @author iEclipse 2019/11/2
 * @description 帮助计算APP内方法调用时长，可以按TAG和事件计时打点，也可以按行号计时打点
 */
public class DurationUtils {
    private static final String                          TAG    = "DurationUtils";
    private static       HashMap<String, DurationEntity> mMap   = new HashMap<>();
    private static       int                             mCount = 0;

    /**
     * 开始计时
     */
    public static void startDuration() {
        startDuration(TAG, "" + mCount++);
    }

    /**
     * 开始计时
     * @param line 行号
     */
    public static void startDuration(int line) {
        startDuration(TAG, "" + line);
    }

    /**
     * 开始计时
     * @param event 事件
     */
    public static void startDuration(String event) {
        startDuration(TAG, event);
    }

    /**
     * 开始计时
     * @param tag   标识
     * @param event 事件
     */
    public static void startDuration(String tag, String event) {

        long t = System.currentTimeMillis();
        DurationEntity entity = new DurationEntity();
        entity.endTime = entity.lastTime = entity.startTime = t;
        entity.lastEvent = event;
        mMap.put(tag, entity);

        toLog(tag, entity.endTime - entity.startTime, "", t - entity.lastTime);
    }

    /**
     * 添加计时
     */
    public static void appendDuration() {
        appendDuration(TAG, "" + mCount++);
    }

    /**
     * 添加计时
     * @param line 行号
     */
    public static void appendDuration(int line) {
        appendDuration(TAG, "" + line);
    }

    /**
     * 添加计时
     * @param event 事件
     */
    public static void appendDuration(String event) {
        appendDuration(TAG, event);
    }

    /**
     * 添加计时
     * @param tag   标识
     * @param event 事件
     */
    public static void appendDuration(String tag, String event) {
        long t = System.currentTimeMillis();
        DurationEntity entity = mMap.get(tag);
        if (entity == null) {
            startDuration(tag, event);
        } else {
            entity.endTime = t;
            toLog(tag, entity.endTime - entity.startTime, entity.lastEvent, t - entity.lastTime);
            entity.lastTime = t;
            entity.lastEvent = event;
        }
    }

    /**
     * 结束计时
     */
    public static void endDuration() {
        endDuration(TAG, "" + mCount++);
        mCount = 0;
    }

    /**
     * 结束计时
     * @param line 行号
     */
    public static void endDuration(int line) {
        endDuration(TAG, "" + line);
    }

    /**
     * 结束计时
     * @param event 事件
     */
    public static void endDuration(String event) {
        endDuration(TAG, event);
    }

    /**
     * 结束计时
     * @param tag   标识
     * @param event 事件
     */
    public static void endDuration(String tag, String event) {
        appendDuration(tag, event);
        mMap.remove(tag);
    }

    /**
     * 清空计时
     */
    public static void clearDuration() {
        mMap.clear();
        mCount = 0;
    }

    private static void toLog(String tag, long totalTime, String lastEvent, long lastTime) {
        Log.d(TAG, "[ " + tag + " ] totalTime=" + totalTime + "  lastEvent=" + lastEvent + "  lastTime=" + lastTime);
    }

    private static class DurationEntity {
        public long   startTime;
        public long   lastTime;
        public long   endTime;
        public String lastEvent;
    }
}
