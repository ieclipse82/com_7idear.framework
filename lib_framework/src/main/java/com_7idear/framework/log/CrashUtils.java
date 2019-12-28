package com_7idear.framework.log;

/**
 * 崩溃日志工具类
 * @author iEclipse 2019/7/21
 * @description 如果使用需要FrameworkConfig初始化initCrash
 */
public class CrashUtils
        implements Thread.UncaughtExceptionHandler {


    /** 实例对象 */
    private static CrashUtils mInstance;

    /**
     * 构造方法
     */
    public CrashUtils() {
        // 设置该CrashUtils为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * 获取异常处理实例
     * @return
     */
    public static CrashUtils getInstance() {
        if (mInstance == null) {
            synchronized (CrashUtils.class) {
                if (mInstance == null) mInstance = new CrashUtils();
            }
        }
        return mInstance;
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        LogUtils.crashException(e);
        //        try {
        //            Thread.sleep(3000);
        //        } catch (InterruptedException ex) {
        //        }
        //        Process.killProcess(Process.myPid());
    }
}
