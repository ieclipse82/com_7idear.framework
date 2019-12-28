package com_7idear.framework.net;

import android.content.Context;
import android.webkit.URLUtil;

import com_7idear.framework.config.FrameworkConfig;
import com_7idear.framework.intface.IState;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.log.LogUtils;
import com_7idear.framework.utils.SDKUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


/**
 * 联网操作类
 * @author ieclipse 19-12-13
 * @description 网络连接，返回联网对象
 */
public class Connect
        implements IConnect, IState {

    private HttpURLConnection mHttpURLConnection; //联网操作对象
    private boolean           isUseProxyRetried; //是否重试过切换使用代理

    private int     mTimeout; //联网超时时间
    private int     mRetryCount; //联网失败重试次数
    private boolean mUseProxy; //是否用代理
    private String  mContentType; //联网类型
    private boolean isLog; //是否输出日志

    /**
     * 构造方法
     */
    public Connect() {
        this(30, 0, false, CONTENT_TYPE_TEXTXML, false);
    }

    /**
     * 构造方法
     * @param timeout     超时（秒）
     * @param retryCount  重试次数
     * @param useProxy    是否使用代理
     * @param contentType 联网类型
     */
    public Connect(int timeout, int retryCount, boolean useProxy, String contentType,
            boolean isLog) {
        this.mTimeout = timeout < 5 ? 5 * 1000 : timeout * 1000;
        this.mRetryCount = retryCount;
        this.isUseProxyRetried = useProxy;
        this.mContentType = contentType;
        this.isLog = isLog;
    }

    /**
     * 关闭网络连接
     */
    public void close() {
        if (mHttpURLConnection != null) {
            mHttpURLConnection.disconnect();
            mHttpURLConnection = null;
        }
    }

    /**
     * 打开网址
     * @param entity        联网对象
     * @param requestMethod 请求方式
     * @return
     */
    public ConnectEntity open(ConnectEntity entity, int requestMethod) {
        if (entity == null) {
            entity = new ConnectEntity(null);
            entity.setState(ERROR);
        } else if (!NetUtils.isNetworkConnected(FrameworkConfig.getInstance().getAppContext())) {
            entity.setState(ERROR_NETWORK_UNKNOWN);
        } else if (!URLUtil.isNetworkUrl(entity.getUrl())) {
            entity.setState(ERROR_URL_UNKNOWN);
        } else if (METHOD_URL_GET == requestMethod || METHOD_URL_POST == requestMethod) {
            entity.setRunUrl(entity.getUrl());
            return openUrl(entity, requestMethod);
        }

        return entity;
    }

    /**
     * 打开网址
     * @param entity        联网对象
     * @param requestMethod 请求方式
     * @return
     */
    private ConnectEntity openUrl(ConnectEntity entity, int requestMethod) {
        String tag = METHOD_URL_GET == requestMethod ? "METHOD_URL_GET" : "METHOD_URL_POST";
        try {
            if (isLog) new LogEntity().append("getRunUrl", entity.getRunUrl()).toLogD();
            if (RETRY != entity.getState()) entity.setState(DOING);

            if (entity.getRunUrl().toLowerCase().startsWith(HTTPS)) {
                mHttpURLConnection = getHttpsURLConnection(new URL(entity.getRunUrl()));
                //TODO FIX
                //                ((HttpsURLConnection) mHttpURLConnection).setSSLSocketFactory(SSLSocketFactoryManager
                //                        .getCAFactory());
                //                ((HttpsURLConnection) mHttpURLConnection).setHostnameVerifier(new TrustAllHostNameVerifier());
            } else {
                mHttpURLConnection = getHttpURLConnection(new URL(entity.getRunUrl()));
            }

            disableConnectionReuseIfNecessary();
            mHttpURLConnection.setConnectTimeout(mTimeout);
            if (METHOD_URL_GET == requestMethod) {
                mHttpURLConnection.setDoOutput(false);
                mHttpURLConnection.setRequestMethod(GET);
            }
            if (METHOD_URL_POST == requestMethod) {
                mHttpURLConnection.setDoInput(true);
                mHttpURLConnection.setRequestMethod(POST);
            }
            mHttpURLConnection.setReadTimeout(mTimeout);
            mHttpURLConnection.setRequestProperty(HEADER_CONTENT_TYPE, mContentType);
            mHttpURLConnection.setRequestProperty(HEADER_ACCEPT_ENCODING, ACCEPT_ENCODING_GZIP);
            mHttpURLConnection.setUseCaches(false);
            if (entity.getRequestByteIndex() > 0 && entity.getRequestLength() > 0) {
                mHttpURLConnection.setRequestProperty(HEADER_RANGE, "bytes="
                        + entity.getRequestByteIndex()
                        + "-"
                        + (entity.getRequestByteIndex() > +entity.getRequestLength()));
            } else if (entity.getRequestByteIndex() > 0) {
                mHttpURLConnection.setRequestProperty(HEADER_RANGE,
                        "bytes=" + entity.getRequestByteIndex() + "-");
            }

            if (entity.getSendBytes() != null && entity.getSendBytes().length > 0) {
                try {
                    OutputStream output = mHttpURLConnection.getOutputStream();
                    output.write(entity.getSendBytes());
                    output.flush();
                    output.close();
                } catch (IOException e) {
                    LogUtils.catchException(e);
                }
            }

            mHttpURLConnection.connect();
            int responseCode = mHttpURLConnection.getResponseCode();
            String cookie = mHttpURLConnection.getHeaderField("Set-Cookie");
            if (isLog) new LogEntity().append("responseCode", responseCode)
                                      .append("cookie", cookie)
                                      .toLogD();
            if (CODE_302 == responseCode) {
                entity.setRunUrl(mHttpURLConnection.getHeaderField("Location"));
                return openUrl(entity, requestMethod);
            } else if (CODE_404 == responseCode) {
                entity.setState(ERROR_URL_UNKNOWN);
            } else if (CODE_200 == responseCode
                    || CODE_201 == responseCode
                    || CODE_206 == responseCode) {
                entity.setContentLength(getContentLength(mHttpURLConnection));
                entity.setContentStream(mHttpURLConnection.getInputStream());
                entity.setContentEncoding(mHttpURLConnection.getContentEncoding());
                entity.setState(FINISHED);
            } else {
                entity.setState(FAILED);
            }
        } catch (MalformedURLException e) {
            LogUtils.catchException(e);
            entity.setState(ERROR_SERVER_UNKNOWN);
        } catch (SocketTimeoutException e) {
            LogUtils.catchException(e);
            entity.setState(ERROR_SERVER_TIMEOUT);
        } catch (ProtocolException e) {
            LogUtils.catchException(e);
            entity.setState(ERROR_MAX_PROTOCOL);
        } catch (IOException e) {
            LogUtils.catchException(e);
            entity.setState(ERROR);
        } finally {
            if (FAILED <= entity.getState()) {
                if (isLog) new LogEntity().append(FAILED)
                                          .append("getState", entity.getState())
                                          .append("mRetryCount", mRetryCount)
                                          .append("isUseProxyRetried", isUseProxyRetried)
                                          .toLogD();
                if (mRetryCount > 0) {
                    mRetryCount--;
                    entity.setState(RETRY);
                    return openUrl(entity, requestMethod);
                } else if (isUseProxyRetried) {
                    isUseProxyRetried = false;
                    mUseProxy = true;
                    entity.setState(RETRY);
                    return openUrl(entity, requestMethod);
                }
            }
        }
        return entity;
    }

    /**
     * 获得网络连接类型（是否设置用代理）
     * @param url 连接地址
     * @return
     * @throws IOException
     */
    private HttpURLConnection getHttpURLConnection(URL url)
            throws IOException {
        if (mUseProxy) {
            String proxyHost = android.net.Proxy.getDefaultHost();
            Proxy proxy = null;
            if (proxyHost != null) {
                proxy = new Proxy(Proxy.Type.HTTP,
                        new InetSocketAddress(android.net.Proxy.getDefaultHost(),
                                android.net.Proxy.getDefaultPort()));
                if (isLog) new LogEntity().append("正常使用代理")
                                          .append("mUseProxy", mUseProxy)
                                          .append("proxyHost", proxyHost)
                                          .append("proxy", proxy)
                                          .toLogD();
            } else {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
                if (isLog) new LogEntity().append("非正常使用代理")
                                          .append("mUseProxy", mUseProxy)
                                          .append("proxyHost", proxyHost)
                                          .append("proxy", proxy)
                                          .toLogD();
            }
            return (HttpURLConnection) url.openConnection(proxy);
        } else {
            if (isLog) new LogEntity().append("不使用代理").append("mUseProxy", mUseProxy).toLogD();
            return (HttpURLConnection) url.openConnection();
        }
    }

    private HttpsURLConnection getHttpsURLConnection(URL url)
            throws IOException {
        if (mUseProxy) {
            String proxyHost = android.net.Proxy.getDefaultHost();
            Proxy proxy = null;
            if (proxyHost != null) {
                proxy = new Proxy(Proxy.Type.HTTP,
                        new InetSocketAddress(android.net.Proxy.getDefaultHost(),
                                android.net.Proxy.getDefaultPort()));
                if (isLog) new LogEntity().append("正常使用代理")
                                          .append("mUseProxy", mUseProxy)
                                          .append("proxyHost", proxyHost)
                                          .append("proxy", proxy)
                                          .toLogD();
            } else {
                proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("10.0.0.172", 80));
                if (isLog) new LogEntity().append("非正常使用代理")
                                          .append("mUseProxy", mUseProxy)
                                          .append("proxyHost", proxyHost)
                                          .append("proxy", proxy)
                                          .toLogD();
            }
            return (HttpsURLConnection) url.openConnection(proxy);
        } else {
            if (isLog) new LogEntity().append("不使用代理").append("mUseProxy", mUseProxy).toLogD();
            return (HttpsURLConnection) url.openConnection();
        }
    }

    /**
     * 获得接收的数据大小
     * @param httpURLConnection 当前连接对象
     * @return
     */
    private long getContentLength(HttpURLConnection httpURLConnection) {
        if (NetUtils.isWapNetwork()) {
            String key = httpURLConnection.getHeaderField("Content-Range");
            if (key == null) {
                Map<String, List<String>> map = httpURLConnection.getHeaderFields();
                if (map != null) {
                    for (int i = 0; i < map.size(); i++) {
                        key = httpURLConnection.getHeaderField(i);
                        if (key != null
                                && key.indexOf("bytes") != -1
                                && key.indexOf("-") != -1
                                && key.indexOf("/") != -1) {
                            String s = key.substring(key.indexOf("/") + 1);
                            return Integer.parseInt(s);
                        }
                    }
                }
            } else {
                String s = key.substring(key.indexOf("/") + 1);
                return Long.parseLong(s);
            }
        } else {
            return mHttpURLConnection.getContentLength();
        }
        return 0;
    }

    /**
     * 解决Android V2.3以前版本的BUG
     */
    private void disableConnectionReuseIfNecessary() {
        if (!SDKUtils.equalAPI_8_Froyo()) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * Android V3.0启用HTTP缓存
     * @param context 环境对象
     */
    private void enableHttpResponseCache(Context context) {
        try {
            long httpCacheSize = 10 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(context.getCacheDir(), "http");
            Class.forName("android.net.http.HttpResponseCache")
                 .getMethod("install", File.class, long.class)
                 .invoke(null, httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
        }
    }


}
