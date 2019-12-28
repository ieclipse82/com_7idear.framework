package com_7idear.framework.application;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com_7idear.framework.log.LogEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * APP全局生命周期工具类
 * @author ieclipse 19-8-14
 * @description
 */
public class AppLifecycleUtils
        implements Application.ActivityLifecycleCallbacks {

    private static volatile AppLifecycleUtils mInstance;

    private List<IAppLifecycle> mAppLifecycleList     = new ArrayList<>(); //APP生命周期监听器列表
    private int                 mCreatedActivityCount = 0; //创建的ACTIVITY数量
    private int                 mStartedActivityCount = 0; //启动的ACTIVITY数量

    public static AppLifecycleUtils getInstance() {
        if (mInstance == null) {
            synchronized (AppLifecycleUtils.class) {
                if (mInstance == null) mInstance = new AppLifecycleUtils();
            }
        }
        return mInstance;
    }

    /**
     * 注册APP生命周期监听器
     * @param callback 监听器
     */
    public void registerAppLifecycle(IAppLifecycle callback) {
        synchronized (mAppLifecycleList) {
            if (!mAppLifecycleList.contains(callback)) mAppLifecycleList.add(callback);
        }
    }

    /**
     * 注销APP生命周期监听器
     * @param callback 监听器
     */
    public void unregisterAppLifecycle(IAppLifecycle callback) {
        synchronized (mAppLifecycleList) {
            if (callback != null) mAppLifecycleList.remove(callback);
        }
    }

    /**
     * 清除全部APP生命周期监听器
     */
    public void clearAppLifecycle() {
        synchronized (mAppLifecycleList) {
            mAppLifecycleList.clear();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        mCreatedActivityCount++;
        if (mCreatedActivityCount == 1) {
            new LogEntity().append("onAppStart")
                           .append("count", mCreatedActivityCount)
                           .append("activity", activity)
                           .toLogD();
            for (IAppLifecycle callback : mAppLifecycleList) {
                callback.onAppStart();
            }
        } else {
            new LogEntity().append("count", mCreatedActivityCount)
                           .append("activity", activity)
                           .toLogD();
        }
    }

    @Override
    public void onActivityStarted(Activity activity) {
        mStartedActivityCount++;
        if (mStartedActivityCount == 1) {
            new LogEntity().append("onAppForeground")
                           .append("count", mStartedActivityCount)
                           .append("activity", activity)
                           .toLogD();
            for (IAppLifecycle callback : mAppLifecycleList) {
                callback.onAppForeground();
            }
        } else {
            new LogEntity().append("count", mStartedActivityCount)
                           .append("activity", activity)
                           .toLogD();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        mStartedActivityCount--;
        if (mStartedActivityCount <= 0) {
            mStartedActivityCount = 0;
            new LogEntity().append("onAppBackground")
                           .append("count", mStartedActivityCount)
                           .append("activity", activity)
                           .toLogD();
            for (IAppLifecycle callback : mAppLifecycleList) {
                callback.onAppBackground();
            }
        } else {
            new LogEntity().append("count", mStartedActivityCount)
                           .append("activity", activity)
                           .toLogD();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mCreatedActivityCount--;
        if (mCreatedActivityCount <= 0) {
            mCreatedActivityCount = 0;
            new LogEntity().append("onAppStop")
                           .append("count", mCreatedActivityCount)
                           .append("activity", activity)
                           .toLogD();
            for (IAppLifecycle callback : mAppLifecycleList) {
                callback.onAppStop();
            }
        } else {
            new LogEntity().append("count", mCreatedActivityCount)
                           .append("activity", activity)
                           .toLogD();
        }
    }


    /**
     * APP生命周期接口
     */
    public interface IAppLifecycle {
        /**
         * 当APP启动
         */
        void onAppStart();

        /**
         * 当APP退出
         */
        void onAppStop();

        /**
         * 当APP进入前台
         */
        void onAppForeground();

        /**
         * 当APP退入后台
         */
        void onAppBackground();
    }
}
