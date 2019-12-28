package com_7idear.framework.config;

/**
 * @author ieclipse 19-12-13
 * @description
 */
public abstract class ConnectConfig
        extends BaseConfig {
    protected void initConnect(int timeout, int buffer, int retryCount, boolean isUseProxyRetry,
            String contentType, boolean isLog) {
        setLog(isLog);
        setInit(init(timeout, buffer, retryCount, isUseProxyRetry, contentType));
    }

    protected abstract boolean init(int timeout, int buffer, int retryCount,
            boolean isUseProxyRetry, String contentType);
}