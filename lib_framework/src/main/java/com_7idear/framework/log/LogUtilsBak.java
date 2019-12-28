package com_7idear.framework.log;

import android.util.Log;

import com_7idear.framework.async.AsyncMsgQueueThread;
import com_7idear.framework.ext.FIFOLinkedQueue;
import com_7idear.framework.intface.IFormat;
import com_7idear.framework.utils.CacheUtils;
import com_7idear.framework.utils.FileUtils;
import com_7idear.framework.utils.FormatUtils;
import com_7idear.framework.utils.TxtUtils;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Iterator;


/**
 * 日志工具类
 * @author iEclipse 2019/7/2
 * @description 在项目运行中输出调试信息，禁止使用error级别输出log，正式发版时请把日志级别设置为关闭
 */
public final class LogUtilsBak
        implements IFormat {

    private static final String TAG  = "LogUtils";
    private static final String LOGS = "logs";

    private static final int LOG_V = 1; //普通级别
    private static final int LOG_D = 2; //调试级别
    private static final int LOG_I = 3; //消息级别
    private static final int LOG_W = 4; //警告级别
    private static final int LOG_E = 5; //异常级别

    private static String                  mTag                = TAG; //日志标识
    private static boolean                 isLog               = false; //输出日志开关
    private static boolean                 isLogAll            = false; //输出全部日志开关
    private static boolean                 isStoragePermission = false; //日志写入到存储设备权限
    private static String                  logPath; //日志路径
    private static HashSet<String>         mTagSet             = new HashSet<>(); //日志级别自定义队列
    private static FIFOLinkedQueue<String> mLogQueue           = new FIFOLinkedQueue<>(
            1000); //日志缓存数据队列


    public static void setTag(String tag) {
        LogUtilsBak.mTag = TxtUtils.isEmpty(tag, TAG);
    }

    public static void setLog(boolean isLog) {
        LogUtilsBak.isLog = isLog;
    }

    public static void setLogAll(boolean isLogAll) {
        LogUtilsBak.isLogAll = isLogAll;
    }

    public static void setLogPath(String logPath) {
        LogUtilsBak.logPath = TxtUtils.isEmpty(logPath) ? CacheUtils.getFilePath(LOGS) : logPath;
    }

    public static boolean addTag(String tag) {
        return mTagSet.add(tag);
    }

    /**
     * 获取日志标识
     * @param tag 标识对象
     * @return
     */
    private static String getLogTag(Object tag) {
        return tag == null
                ? null
                : tag instanceof String ? (String) tag : tag.getClass().getSimpleName();
    }

    /**
     * 检查自定义日志标识
     * @param tag 日志标识
     * @return
     */
    private static boolean existLogTag(String tag) {
        return mTagSet.contains(tag);
    }

    /**
     * 执行日志输出
     * @param level 日志级别
     * @param tag   标识对象
     * @param event 事件
     * @param msg   信息
     */
    private static void runShowLog(int level, Object tag, String event, Object msg) {
        String logTag = TxtUtils.isEmpty(getLogTag(tag), mTag);
        StringBuilder logMsg = new StringBuilder();
        if (!TxtUtils.isEmpty(event)) logMsg.append(_L2).append(event).append(_R2);
        logMsg.append(msg);
        if (isLog && (isLogAll || existLogTag(logTag))) {
            switch (level) {
                case LOG_D:
                    Log.d(logTag, logMsg.toString());
                    break;
                case LOG_I:
                    Log.i(logTag, logMsg.toString());
                    break;
                case LOG_W:
                    Log.w(logTag, logMsg.toString());
                    break;
                case LOG_E:
                    Log.e(logTag, logMsg.toString());
                    break;
                default:
                    Log.v(logTag, logMsg.toString());
                    break;
            }
        }

        runCacheLog(logTag, level, logMsg.toString(), false);
    }

    /**
     * 获取异常信息
     * @param e 异常对象
     * @return
     */
    public static String getErrorInfo(Throwable e) {
        Writer w = new StringWriter();
        PrintWriter pw = new PrintWriter(w);
        e.printStackTrace(pw);
        pw.close();
        return w.toString();
    }

    /**
     * 输出信息——捕获的异常
     * @param e 异常对象
     */
    public static synchronized void catchException(Throwable e) {
        StringBuilder log = new StringBuilder("catchException").append(_R_N);
        if (e != null) log.append(getErrorInfo(e));
        runCacheLog(mTag, LOG_W, log.toString(), true);
    }

    /**
     * 输出信息——APP崩溃
     * @param e 异常对象
     */
    protected static synchronized void crashException(Throwable e) {
        StringBuilder log = new StringBuilder("crashException").append(_R_N);
        if (e != null) log.append(getErrorInfo(e));
        runCacheLog(mTag, LOG_E, log.toString(), true);
    }

    /**
     * 执行添加日志信息
     * @param logTag   日志标识
     * @param logLevel 日志级别
     * @param logMsg   日志信息
     * @param runNow   是否立即执行
     */
    private static void runCacheLog(String logTag, int logLevel, String logMsg, boolean runNow) {
        if (!isStoragePermission) return;
        StringBuilder log = new StringBuilder(FormatUtils.formatDate(FormatUtils.DATE_10)).append(
                _L4);
        switch (logLevel) {
            case LOG_D:
                log.append("D");
                break;
            case LOG_I:
                log.append("I");
                break;
            case LOG_W:
                log.append("W");
                break;
            case LOG_E:
                log.append("E");
                break;
            default:
                log.append("V");
                break;
        }
        log.append(_R4).append(logTag).append(_T).append(logMsg).append(_R_N);
        synchronized (mLogQueue) {
            mLogQueue.offerWithPoll(log.toString());
            if (runNow) {
                runOutputLog(null, 0);
            } else if (isLog) {
                if (mLogQueue.size() > mLogQueue.remainingCapacity()) {
                    runOutputLog(null, 0);
                } else {
                    runOutputLog(null, 1000 * 60);
                }
            }
        }
    }

    /**
     * 执行输出日志到本地
     * @param msg 日志信息
     */
    private static void runOutputLog(String msg, long delayMillis) {
        mLogHandler.removeMsg();
        mLogHandler.sendMsg(msg, delayMillis);
    }

    private static AsyncMsgQueueThread<String> mLogHandler = new AsyncMsgQueueThread<String>(TAG) {

        long count;

        @Override
        protected void onAsyncMessage(int action, String msg) {
            StringBuilder outLog = new StringBuilder();
            synchronized (mLogQueue) {
                Iterator<String> it = mLogQueue.iterator();
                while (it.hasNext()) {
                    outLog.append(it.next());
                }
                mLogQueue.clear();
            }
            if (!TxtUtils.isEmpty(outLog)) {
                outLog.append(_Line)
                      .append(++count)
                      .append(_Line)
                      .append(_L2)
                      .append(FormatUtils.formatDate(FormatUtils.DATE_10))
                      .append(_R2)
                      .append(_R_N);
                String filePath = logPath + File.separator + FormatUtils.formatDate(
                        FormatUtils.DATE_53) + ".log";
                FileUtils.writeToFile(outLog.toString(), filePath);
            }
        }
    };

    /**
     * 输出信息——1：普通级别
     * @param msg 信息
     */
    public static void v(String msg) {
        runShowLog(LOG_V, mTag, null, msg);
    }

    /**
     * 输出信息——2：调试级别
     * @param msg 信息
     */
    public static void d(String msg) {
        runShowLog(LOG_D, mTag, null, msg);
    }

    /**
     * 输出信息——3：消息级别
     * @param msg 信息
     */
    public static void i(String msg) {
        runShowLog(LOG_I, mTag, null, msg);
    }

    /**
     * 输出信息——4：警告级别
     * @param msg 信息
     */
    public static void w(String msg) {
        runShowLog(LOG_W, mTag, null, msg);
    }

    /**
     * 输出信息——5：异常级别
     * @param msg 信息
     */
    public static void e(String msg) {
        runShowLog(LOG_E, mTag, null, msg);
    }

    /**
     * 输出信息——1：普通级别
     * @param tag 标识
     * @param msg 信息
     */
    public static void v(Object tag, Object msg) {
        runShowLog(LOG_V, tag, null, msg);
    }

    /**
     * 输出信息——2：调试级别
     * @param tag 标识
     * @param msg 信息
     */
    public static void d(Object tag, Object msg) {
        runShowLog(LOG_D, tag, null, msg);
    }

    /**
     * 输出信息——3：消息级别
     * @param tag 标识
     * @param msg 信息
     */
    public static void i(Object tag, Object msg) {
        runShowLog(LOG_I, tag, null, msg);
    }

    /**
     * 输出信息——4：警告级别
     * @param tag 标识
     * @param msg 信息
     */
    public static void w(Object tag, Object msg) {
        runShowLog(LOG_W, tag, null, msg);
    }

    /**
     * 输出信息——5：异常级别
     * @param tag 标识
     * @param msg 信息
     */
    public static void e(Object tag, Object msg) {
        runShowLog(LOG_E, tag, null, msg);
    }

    /**
     * 输出信息——1：普通级别
     * @param tag   标识
     * @param event 事件标识
     * @param msg   信息
     */
    public static void v(Object tag, String event, Object msg) {
        runShowLog(LOG_V, tag, event, msg);
    }

    /**
     * 输出信息——2：调试级别
     * @param tag   标识
     * @param event 事件标识
     * @param msg   信息
     */
    public static void d(Object tag, String event, Object msg) {
        runShowLog(LOG_D, tag, event, msg);
    }

    /**
     * 输出信息——3：消息级别
     * @param tag   标识
     * @param event 事件标识
     * @param msg   信息
     */
    public static void i(Object tag, String event, Object msg) {
        runShowLog(LOG_I, tag, event, msg);
    }

    /**
     * 输出信息——4：警告级别
     * @param tag   标识
     * @param event 事件标识
     * @param msg   信息
     */
    public static void w(Object tag, String event, Object msg) {
        runShowLog(LOG_W, tag, event, msg);
    }

    /**
     * 输出信息——5：异常级别
     * @param tag   标识
     * @param event 事件标识
     * @param msg   信息
     */
    public static void e(Object tag, String event, Object msg) {
        runShowLog(LOG_E, tag, event, msg);
    }

}
