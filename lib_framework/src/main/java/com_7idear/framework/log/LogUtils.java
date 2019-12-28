package com_7idear.framework.log;

import android.util.Log;

import com_7idear.framework.async.AsyncMsgQueueThread;
import com_7idear.framework.config.LogConfig;
import com_7idear.framework.ext.FIFOLinkedQueue;
import com_7idear.framework.intface.IFormat;
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
public final class LogUtils
        extends LogConfig {

    private static final String TAG  = "LogUtils";
    private static final String LOGS = "logs";

    protected static final int LOG_V = 1; //普通级别
    protected static final int LOG_D = 2; //调试级别
    protected static final int LOG_I = 3; //消息级别
    protected static final int LOG_W = 4; //警告级别
    protected static final int LOG_E = 5; //异常级别

    private static LogUtils mInstance = new LogUtils();


    private static String          mTag     = ""; //日志标识
    private static boolean         isLog    = false; //输出日志开关
    private static boolean         isLogAll = false; //输出全部日志开关
    private static HashSet<String> mTagSet  = new HashSet<>(); //日志级别自定义队列

    private static boolean                 isSaveLog = false; //日志写入到存储设备权限
    private static String                  mLogPath; //日志路径
    private static FIFOLinkedQueue<LogMsg> mLogQueue = new FIFOLinkedQueue<>(1000); //日志缓存数据队列

    @Override
    protected boolean init(String tag, boolean isLogAll, String[] logTags) {
        mInstance.mTag = tag;
        mInstance.isLog = isLog();
        mInstance.isLogAll = isLogAll;
        mInstance.mTagSet.clear();
        if (logTags != null) {
            for (String logTag : logTags) {
                mInstance.mTagSet.add(logTag);
            }
        }
        return true;
    }

    @Override
    protected boolean init(boolean isSaveLog, String logPath) {
        mInstance.isSaveLog = isSaveLog;
        mInstance.mLogPath = logPath;
        return true;
    }

    /**
     * 获取异常处理实例
     * @return
     */
    public static LogUtils getInstance() {
        return mInstance;
    }


    /**
     * 获取日志标识
     * @param tag 标识对象
     * @return
     */
    private static String getLogTag(Object tag) {
        if (tag instanceof String) {
            return (String) tag;
        } else {
            return mTag;
        }
    }

    /**
     * 检查自定义日志标识
     * @param tag 日志标识
     * @return
     */
    private static boolean existLogTag(String tag) {
        return tag != null && mTagSet.contains(tag);
    }

    /**
     * 执行日志输出
     * @param level 日志级别
     * @param tag   标识对象
     * @param msg   信息
     */
    protected static void runShowLog(int level, Object tag, Object msg, int deep) {
        final String logTag = getLogTag(tag);
        final Thread thread = Thread.currentThread();
        final StackTraceElement[] trace = thread.getStackTrace();

        StringBuilder logMsg = new StringBuilder();
        if (trace != null && deep > 0 && deep < trace.length) {
            final StackTraceElement ste = trace[deep];
            logMsg.append("(")
                  .append(ste.getFileName())
                  .append(":")
                  .append(ste.getLineNumber())
                  .append(")")
                  .append(IFormat._4)
                  .append(ste.getMethodName());
        }
        logMsg.append(IFormat._T)
              .append("[ Thread= ")
              .append(thread.getName())
              .append(":")
              .append(thread.getId());
        if (tag != null && !(tag instanceof String)) {
            logMsg.append(IFormat._T)
                  .append("Object= ")
                  .append(tag.getClass().getSimpleName())
                  .append("@")
                  .append(Integer.toHexString(tag.hashCode()));
        }
        logMsg.append(" ]").append(IFormat._T).append(msg);

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

        if (isSaveLog) {
            runCacheLog(logTag, level, logMsg.toString(), false);
        }
    }

    /**
     * 获取异常信息
     * @param e 异常对象
     * @return
     */
    private static String getErrorInfo(Throwable e) {
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
        StringBuilder log = new StringBuilder("catchException").append(IFormat._R_N);
        if (e != null) log.append(getErrorInfo(e));
        if (isSaveLog) runCacheLog(mTag, LOG_W, log.toString(), true);
    }

    /**
     * 输出信息——APP崩溃
     * @param e 异常对象
     */
    protected static synchronized void crashException(Throwable e) {
        StringBuilder log = new StringBuilder("crashException").append(IFormat._R_N);
        if (e != null) log.append(getErrorInfo(e));
        if (isSaveLog) runCacheLog(mTag, LOG_E, log.toString(), true);
    }


    /**
     * 输出信息——1：普通级别
     * @param msg 信息
     */
    public static void v(Object msg) {
        runShowLog(LOG_V, null, msg, 4);
    }

    /**
     * 输出信息——2：调试级别
     * @param msg 信息
     */
    public static void d(Object msg) {
        runShowLog(LOG_D, null, msg, 4);
    }

    /**
     * 输出信息——3：消息级别
     * @param msg 信息
     */
    public static void i(Object msg) {
        runShowLog(LOG_I, null, msg, 4);
    }

    /**
     * 输出信息——4：警告级别
     * @param msg 信息
     */
    public static void w(Object msg) {
        runShowLog(LOG_W, null, msg, 4);
    }

    /**
     * 输出信息——5：异常级别
     * @param msg 信息
     */
    public static void e(Object msg) {
        runShowLog(LOG_E, null, msg, 4);
    }

    /**
     * 输出信息——1：普通级别
     * @param tag 标识
     * @param msg 信息
     */
    public static void v(Object tag, Object msg) {
        runShowLog(LOG_V, tag, msg, 4);
    }

    /**
     * 输出信息——2：调试级别
     * @param tag 标识
     * @param msg 信息
     */
    public static void d(Object tag, Object msg) {
        runShowLog(LOG_D, tag, msg, 4);
    }

    /**
     * 输出信息——3：消息级别
     * @param tag 标识
     * @param msg 信息
     */
    public static void i(Object tag, Object msg) {
        runShowLog(LOG_I, tag, msg, 4);
    }

    /**
     * 输出信息——4：警告级别
     * @param tag 标识
     * @param msg 信息
     */
    public static void w(Object tag, Object msg) {
        runShowLog(LOG_W, tag, msg, 4);
    }

    /**
     * 输出信息——5：异常级别
     * @param tag 标识
     * @param msg 信息
     */
    public static void e(Object tag, Object msg) {
        runShowLog(LOG_E, tag, msg, 4);
    }


    /**
     * 执行添加日志信息
     * @param tag    日志标识
     * @param level  日志级别
     * @param msg    日志信息
     * @param runNow 是否立即执行
     */
    private static void runCacheLog(String tag, int level, String msg, boolean runNow) {
        synchronized (mLogQueue) {
            mLogQueue.offerWithPoll(new LogMsg(tag, level, msg));
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
     * @param logMsg 日志信息
     */
    private static void runOutputLog(LogMsg logMsg, long delayMillis) {
        mLogHandler.removeMsg();
        mLogHandler.sendMsg(logMsg, delayMillis);
    }

    /**
     * 输出全部日志
     */
    public static void flushLog() {
        runOutputLog(null, 0);
    }

    private static AsyncMsgQueueThread<LogMsg> mLogHandler = new AsyncMsgQueueThread<LogMsg>(TAG) {

        long count;

        @Override
        protected void onAsyncMessage(int action, LogMsg msg) {
            StringBuilder outLog = new StringBuilder();
            synchronized (mLogQueue) {
                Iterator<LogMsg> it = mLogQueue.iterator();
                while (it.hasNext()) {
                    outLog.append(getMsg(it.next()));
                }
                mLogQueue.clear();
            }
            if (!TxtUtils.isEmpty(outLog)) {
                outLog.append(IFormat._Line)
                      .append(++count)
                      .append(IFormat._Line)
                      .append(IFormat._L2)
                      .append(FormatUtils.formatDate(FormatUtils.DATE_10))
                      .append(IFormat._R2)
                      .append(IFormat._R_N);
                String filePath = mLogPath + File.separator + FormatUtils.formatDate(
                        FormatUtils.DATE_53) + ".log";
                FileUtils.writeToFile(outLog.toString(), filePath);
            }
        }

        /**获取信息
         * @param logMsg 日志信息对象
         * @return
         */
        private String getMsg(LogMsg logMsg) {
            if (logMsg == null) return "";
            StringBuilder sb = new StringBuilder(
                    FormatUtils.formatDate(FormatUtils.DATE_10, logMsg.time)).append(IFormat._L4);
            switch (logMsg.level) {
                case LOG_D:
                    sb.append("D");
                    break;
                case LOG_I:
                    sb.append("I");
                    break;
                case LOG_W:
                    sb.append("W");
                    break;
                case LOG_E:
                    sb.append("E");
                    break;
                default:
                    sb.append("V");
                    break;
            }
            sb.append(IFormat._R4)
              .append(logMsg.tag)
              .append(IFormat._T)
              .append(logMsg.msg)
              .append(IFormat._R_N);
            return sb.toString();
        }
    };

    private static class LogMsg {
        private final String tag;
        private final int    level;
        private final String msg;
        private final long   time;

        LogMsg(String tag, int level, String msg) {
            this.tag = tag;
            this.level = level;
            this.msg = msg;
            this.time = System.currentTimeMillis();
        }
    }
}
