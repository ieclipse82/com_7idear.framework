package com_7idear.framework.config;

import android.content.Context;
import android.os.StrictMode;

import com_7idear.framework.asynccall.AsyncCallUtils;
import com_7idear.framework.log.CrashUtils;
import com_7idear.framework.log.LogUtils;
import com_7idear.framework.log.TimerFrameUtils;
import com_7idear.framework.net.ConnectUtils;
import com_7idear.framework.preference.PreferencesUtils;
import com_7idear.framework.task.TaskUtils;
import com_7idear.framework.theme.ThemeUtils;
import com_7idear.framework.utils.CacheUtils;
import com_7idear.framework.utils.SDKUtils;
import com_7idear.framework.utils.TxtUtils;

import retrofit2.Retrofit;

/**
 * 全局配置类
 * @author ieclipse 19-8-8
 * @description
 */
public final class FrameworkConfig {

    private static volatile FrameworkConfig mInstance;

    private Context appContext; //APP全局环境对象
    private String  appDir; //应用文件夹

    private String themeId = ""; //主题唯一标识

    public static FrameworkConfig getInstance() {
        if (mInstance == null) {
            synchronized (FrameworkConfig.class) {
                if (mInstance == null) mInstance = new FrameworkConfig();
            }
        }
        return mInstance;
    }


    public FrameworkConfig initBase(Context appContext, String appDir, boolean is) {
        this.appContext = appContext;
        this.appDir = appDir;


        enableStrictMode();


        return this;
    }

    public FrameworkConfig initCache(Context appContext, String appDir, boolean isLog) {
        CacheUtils.getInstance().initCache(appContext, appDir, isLog);
        return this;
    }

    public FrameworkConfig initLog(String tag, boolean isLog, boolean isLogAll, String[] logTags,
            boolean isSaveLog, String logPath, boolean isTimerFrameLog) {
        LogUtils.getInstance().initLog(tag, isLog, isLogAll, logTags);
        LogUtils.getInstance().initSaveLog(isSaveLog, logPath);
        if (isTimerFrameLog) {
            TimerFrameUtils.enableLog();
        } else {
            TimerFrameUtils.disableLog();
        }
        return this;
    }

    public FrameworkConfig initCrash() {
        CrashUtils.getInstance();
        return this;
    }

    public FrameworkConfig initTheme(Context appContext, String themeID, String themePackageName) {
        ThemeUtils.getInstance().init(appContext, themeID);
        if (TxtUtils.isEmpty(themePackageName)) {
            ThemeUtils.getInstance()
                      .enableTheme(PreferencesUtils.getInstance().getThemePackageName());
        } else {
            ThemeUtils.getInstance().enableTheme(themePackageName);
        }
        return this;
    }

    public FrameworkConfig initConnect(int timeout, int buffer, int retryCount,
            boolean isUseProxyRetry, String contentType, boolean isLog) {
        ConnectUtils.getInstance()
                    .initConnect(timeout, buffer, retryCount, isUseProxyRetry, contentType, isLog);
        return this;
    }

    public FrameworkConfig initTask(int taskLevel, String cachePath, boolean isLog) {
        TaskUtils.getInstance().initTask(taskLevel, cachePath, isLog);
        return this;
    }

    public FrameworkConfig initAsyncCall(Retrofit.Builder builder, boolean isLog) {
        AsyncCallUtils.getApi().initAsyncCall(builder, isLog);
        return this;
    }

    /**
     * 执行严格模式
     * @return
     */
    public FrameworkConfig enableStrictMode() {
        if (SDKUtils.equalAPI_9_Gingerbread()) {
            if (SDKUtils.equalAPI_11_Honeycomb()) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll()
                                                                                .penaltyFlashScreen()
                                                                                .penaltyLog()
                                                                                .build());
            } else {
                StrictMode.setThreadPolicy(
                        new StrictMode.ThreadPolicy.Builder().detectAll().penaltyLog().build());
            }
            StrictMode.setVmPolicy(
                    new StrictMode.VmPolicy.Builder().detectAll().penaltyLog().build());
        }
        return this;
    }

    public Context getAppContext() {
        return appContext;
    }
}
