package com_7idear.framework.config;

/**
 * @author ieclipse 19-12-13
 * @description
 */
public abstract class LogConfig
        extends BaseConfig {

    final protected void initLog(String tag, boolean isLog, boolean isLogAll, String[] logTags) {
        setLog(isLog);
        setInit(init(tag, isLogAll, logTags));
    }

    final protected void initSaveLog(boolean isSaveLog, String logPath) {
        init(isSaveLog, logPath);
    }

    protected abstract boolean init(String tag, boolean isLogAll, String[] logTags);

    protected abstract boolean init(boolean isSaveLog, String logPath);
}
