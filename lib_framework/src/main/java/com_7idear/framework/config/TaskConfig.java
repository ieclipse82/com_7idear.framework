package com_7idear.framework.config;

/**
 * @author ieclipse 19-12-16
 * @description
 */
public abstract class TaskConfig
        extends BaseConfig {

    protected void initTask(int taskLevel, String cachePath, boolean isLog) {
        setLog(isLog);
        setInit(init(taskLevel, cachePath));
    }

    protected abstract boolean init(int taskLevel, String cachePath);

}
