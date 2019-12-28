package com_7idear.framework.log;

import com_7idear.framework.intface.IFormat;

/**
 * 耗时统计工具类
 * @author iEclipse 2019/11/2
 * @description 使用timeFrame方法统计页面和方法耗时
 */
public class TimerFrameUtils
        implements IFormat {

    private static final String TAG = "TimerFrameUtils";

    private static boolean isLog = false; //是否开启

    private static final String TOTAL_TIME = "total time = ";
    private static final String SPAN_TIME  = "span time = ";

    private static volatile long   mStartTimeMillis; //开始时间（毫秒）
    private static volatile long   mLastTimeMillis; //最后计时（毫秒）
    private static volatile String mLineNumber; //行号
    private static volatile String mLastLineNumber; //最后行号

    public static void enableLog() {
        isLog = true;
    }

    public static void disableLog() {
        isLog = false;
    }

    /**
     * 记录时间帧
     */
    public static synchronized void timerFrame() {
        timerFrameToLog(null, false);
    }

    /**
     * 记录时间帧
     * @param tag 标识
     */
    public static synchronized void timerFrame(String tag) {
        timerFrameToLog(tag, false);
    }

    /**
     * 记录时间帧（重新计时）
     */
    public static synchronized void timerFrameRestart() {
        timerFrameToLog(null, true);
    }

    /**
     * 记录时间帧（重新计时）
     * @param tag 标识
     */
    public static synchronized void timerFrameRestart(String tag) {
        timerFrameToLog(tag, true);
    }

    /**
     * 记录时间帧
     * @param tag 标识
     */
    private static void timerFrameToLog(String tag, boolean restart) {
        if (!isLog) return;
        long t = System.currentTimeMillis();
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        StackTraceElement e = trace.length < 5 ? null : trace[4];
        if (e == null) return;
        if (restart || mStartTimeMillis <= 0) {
            mStartTimeMillis = t;
            mLastLineNumber = null;
            mLastTimeMillis = t;
        }
        mLineNumber = "" + e.getLineNumber();
        LogUtils.runShowLog(LogUtils.LOG_D, tag, new StringBuilder().append(TOTAL_TIME)
                                                                    .append(t - mStartTimeMillis)
                                                                    .append(_T)
                                                                    .append(_L4)
                                                                    .append(mLastLineNumber)
                                                                    .append(_Minus)
                                                                    .append(mLineNumber)
                                                                    .append(_R4)
                                                                    .append(SPAN_TIME)
                                                                    .append(t - mLastTimeMillis)
                                                                    .toString(), 5);
        mLastTimeMillis = t;
        mLastLineNumber = mLineNumber;
    }
}
