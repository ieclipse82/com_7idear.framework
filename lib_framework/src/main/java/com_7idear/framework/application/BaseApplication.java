package com_7idear.framework.application;

import android.app.Application;
import android.content.Context;

import com_7idear.framework.net.NetUtils;


/**
 * APP全局基础类
 * @author iEclipse 2019/8/14
 * @description
 */
public abstract class BaseApplication
        extends Application {

    private static volatile Context mAppContext; //APP全局环境对象

    @Override
    public void onCreate() {
        super.onCreate();

        mAppContext = this;

        preInit();

        //检查网络状态
        NetUtils.checkNetworkState(mAppContext);
        //注册APP全局生命周期监听
        registerActivityLifecycleCallbacks(AppLifecycleUtils.getInstance());
        //注册APP全局广播监听
        registerReceiver(AppReceiverUtils.getInstance(),
                AppReceiverUtils.FILTER_CONNECTIVITY_CHANGE);

        initApp();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    /**
     * 获取APP全局环境对象
     * @return
     */
    public static Context getAppContext() {
        return mAppContext;
    }

    /**
     * 预先初始化
     */
    protected abstract void preInit();

    /**
     * 初始化应用
     */
    protected abstract void initApp();


}
