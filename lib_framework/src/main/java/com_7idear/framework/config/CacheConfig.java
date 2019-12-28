package com_7idear.framework.config;

import android.content.Context;

/**
 * @author ieclipse 19-12-13
 * @description
 */
public abstract class CacheConfig
        extends BaseConfig {

    protected void initCache(Context appContext, String appDir, boolean isLog) {
        setLog(isLog);
        setInit(init(appContext, appDir));
    }

    protected abstract boolean init(Context appContext, String appDir);

}
