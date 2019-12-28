package com_7idear.framework.config;

import com_7idear.framework.log.LogEntity;

/**
 * 基础配置类（需要特殊配置的工具类实现）
 * @author ieclipse 19-12-12
 * @description 实现日志、初始化开关控制（先设置日志开关，然后在执行初始化方法）
 * 特殊配置的工具类需要继承此类或内部静态类实现相关方法
 */
public abstract class BaseConfig {
    private boolean isLog  = false; //输出日志开关
    private boolean isInit = false; //是否初始化完成

    /**
     * 设置是否输出日志
     * @param isLog 是否输出日志
     */
    void setLog(boolean isLog) {
        if (isLog) {
            enableLog();
        } else {
            disableLog();
        }
    }

    /**
     * 设置是否初始化完成
     * @param isInit 是否初始化完成
     */
    void setInit(boolean isInit) {
        this.isInit = isInit;
    }

    /**
     * 启用日志
     */
    final public void enableLog() {
        isLog = true;
    }

    /**
     * 停用日志
     */
    final public void disableLog() {
        isLog = false;
    }

    /**
     * 是否输出日志
     * @return
     */
    final public boolean isLog() {
        return isLog;
    }

    /**
     * 获取日志实例对象
     * @return
     */
    final protected LogEntity log() {
        return new LogEntity(isLog);
    }

    /**
     * 获取静态方法日志实例对象
     * @param isLog 是否输出日志
     * @return
     */
    final protected static LogEntity slog(boolean isLog) {
        return new LogEntity(isLog);
    }

    /**
     * 是否初始化完成
     * @return
     */
    final protected boolean isInit() {
        return isInit;
    }
}
