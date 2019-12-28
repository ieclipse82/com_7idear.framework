package com_7idear.framework.net;

import com_7idear.framework.config.ConnectConfig;
import com_7idear.framework.intface.IState;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.log.LogUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;


/**
 * 联网请求帮助类
 * @author ieclipse 19-12-13
 * @description 支持打开URL地址返回字节，支持打开封装联网实体对象并返回
 */
public class ConnectUtils
        extends ConnectConfig
        implements IConnect, IState {

    private static ConnectUtils mInstance;

    private static int     mTimeout        = 30;
    private static int     mBuffer         = 4096;
    private static int     mRetryCount     = 0;
    private static boolean isUseProxyRetry = false;
    private static String  mContentType    = CONTENT_TYPE_TEXTXML;
    private static boolean isLog           = false;

    @Override
    protected boolean init(int timeout, int buffer, int retryCount, boolean isUseProxyRetry,
            String contentType) {
        this.mTimeout = timeout < 5 ? 5 : timeout;
        this.mBuffer = buffer < 1 << 8 ? 1 << 8 : buffer > 1 << 12 ? 1 << 12 : buffer;
        this.mRetryCount = retryCount < 0 ? 0 : retryCount > 10 ? 10 : retryCount;
        this.isUseProxyRetry = isUseProxyRetry;
        if (ACCEPT_ENCODING_GZIP.equals(contentType)) {
            this.mContentType = ACCEPT_ENCODING_GZIP;
        } else {
            this.mContentType = CONTENT_TYPE_TEXTXML;
        }
        this.isLog = isLog();
        return true;
    }

    public static ConnectUtils getInstance() {
        if (mInstance == null) {
            synchronized (ConnectUtils.class) {
                if (mInstance == null) mInstance = new ConnectUtils();
            }
        }
        return mInstance;
    }


    /**
     * 建立联网实体对象
     * @param url              URL地址
     * @param sendBytes        发送数据
     * @param requestByteIndex 请求数据位置
     * @param requestLength    请求数据长度
     * @return
     */
    public static ConnectEntity createConnectEntity(String url, byte[] sendBytes,
            int requestByteIndex, int requestLength) {
        ConnectEntity entity = new ConnectEntity(url);
        if (sendBytes != null && sendBytes.length > 0) entity.setSendBytes(sendBytes);
        if (requestByteIndex > 0) entity.setRequestByteIndex(requestByteIndex);
        if (requestLength > 0) entity.setRequestLength(requestLength);
        entity.setState(UNKNOWN);
        return entity;
    }

    /**
     * 打开URL
     * @param url URL地址
     * @return
     */
    public static byte[] openGetUrl(String url) {
        return openUrl(createConnectEntity(url, null, 0, 0), GET);
    }

    /**
     * 打开URL
     * @param url   URL地址
     * @param bytes 发送数据
     * @return
     */
    public static byte[] openGetUrl(String url, byte[] bytes) {
        return openUrl(createConnectEntity(url, bytes, 0, 0), GET);
    }

    /**
     * 打开URL
     * @param url              URL地址
     * @param requestByteIndex 请求数据位置
     * @return
     */
    public static byte[] openGetUrl(String url, int requestByteIndex) {
        return openUrl(createConnectEntity(url, null, requestByteIndex, 0), GET);
    }

    /**
     * 打开URL
     * @param url              URL地址
     * @param requestByteIndex 请求数据位置
     * @param requestLength    请求数据长度
     * @return
     */
    public static byte[] openGetUrl(String url, int requestByteIndex, int requestLength) {
        return openUrl(createConnectEntity(url, null, requestByteIndex, requestLength), GET);
    }

    /**
     * 打开URL
     * @param url              URL地址
     * @param bytes            发送数据
     * @param requestByteIndex 请求数据位置
     * @param requestLength    请求数据长度
     * @return
     */
    public static byte[] openGetUrl(String url, byte[] bytes, int requestByteIndex,
            int requestLength) {
        return openUrl(createConnectEntity(url, bytes, requestByteIndex, requestLength), GET);
    }

    /**
     * 打开URL
     * @param url URL地址
     * @return
     */
    public static byte[] openPostUrl(String url) {
        return openUrl(createConnectEntity(url, null, 0, 0), POST);
    }

    /**
     * 打开URL
     * @param url   URL地址
     * @param bytes 发送数据
     * @return
     */
    public static byte[] openPostUrl(String url, byte[] bytes) {
        return openUrl(createConnectEntity(url, bytes, 0, 0), POST);
    }

    /**
     * 打开URL
     * @param url              URL地址
     * @param requestByteIndex 请求数据位置
     * @return
     */
    public static byte[] openPostUrl(String url, int requestByteIndex) {
        return openUrl(createConnectEntity(url, null, requestByteIndex, 0), POST);
    }

    /**
     * 打开URL
     * @param url              URL地址
     * @param requestByteIndex 请求数据位置
     * @param requestLength    请求数据长度
     * @return
     */
    public static byte[] openPostUrl(String url, int requestByteIndex, int requestLength) {
        return openUrl(createConnectEntity(url, null, requestByteIndex, requestLength), POST);
    }

    /**
     * 打开URL
     * @param url              URL地址
     * @param bytes            发送数据
     * @param requestByteIndex 请求数据位置
     * @param requestLength    请求数据长度
     * @return
     */
    public static byte[] openPostUrl(String url, byte[] bytes, int requestByteIndex,
            int requestLength) {
        return openUrl(createConnectEntity(url, bytes, requestByteIndex, requestLength), POST);
    }

    /**
     * 打开URL
     * @param entity 联网对象
     * @return
     */
    public static byte[] openGetUrl(ConnectEntity entity) {
        return openUrl(entity, GET);
    }

    /**
     * 打开URL
     * @param entity 联网对象
     * @return
     */
    public static byte[] openPostUrl(ConnectEntity entity) {
        return openUrl(entity, POST);
    }

    /**
     * 打开URL
     * @param entity        联网对象
     * @param requestMethod 请求方式
     * @return
     */
    private static byte[] openUrl(ConnectEntity entity, String requestMethod) {
        if (entity == null) return null;
        Connect con = new Connect(mTimeout, mRetryCount, isUseProxyRetry, mContentType, isLog);
        try {
            entity = openUrl(con, entity, requestMethod);
            if (FINISHED == entity.getState()) {
                if (CONTENTENCODING_GZIP.equalsIgnoreCase(entity.getContentEncoding())) {
                    return getBytesFromGZIPInputStream(entity);
                } else {
                    return getBytesFromInputStream(entity);
                }
            }
        } catch (IOException e) {
            LogUtils.catchException(e);
            entity.setState(ERROR);
        } finally {
            try {
                if (entity.getContentStream() != null) entity.getContentStream().close();
            } catch (IOException e) {
                LogUtils.catchException(e);
            }
            if (con != null) con.close();
            con = null;
            if (isLog) new LogEntity().append("getState", entity.getState())
                                      .append("getContentLength", entity.getContentLength())
                                      .append("getContentEncoding", entity.getContentEncoding())
                                      .append("getUrl", entity.getUrl())
                                      .toLogD();
        }
        return null;
    }

    /**
     * 打开URL
     * @param con    联网类
     * @param entity 联网对象
     * @return
     */
    public static ConnectEntity openGetUrl(Connect con, ConnectEntity entity) {
        return openUrl(con, entity, GET);
    }

    /**
     * 打开URL
     * @param con    联网类
     * @param entity 联网对象
     * @return
     */
    public static ConnectEntity openPostUrl(Connect con, ConnectEntity entity) {
        return openUrl(con, entity, POST);
    }

    /**
     * 打开URL
     * @param con           联网类
     * @param entity        联网实体对象
     * @param requestMethod 请求方式
     * @return
     */
    private static ConnectEntity openUrl(Connect con, ConnectEntity entity, String requestMethod) {
        if (CANCELLED == entity.getState()) {
            return entity;
        } else if (UNKNOWN == entity.getState()) {
            if (POST.equals(requestMethod)) {
                entity = con.open(entity, METHOD_URL_POST);
            } else {
                entity = con.open(entity, METHOD_URL_GET);
            }
        } else {
            entity.reset();
        }
        return entity;
    }

    /**
     * 获取输入流字节数组
     * @param entity 联网对象
     * @return
     * @throws IOException
     */
    private static byte[] getBytesFromInputStream(ConnectEntity entity)
            throws IOException {
        if (entity == null || entity.getContentStream() == null) return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buff = new byte[mBuffer];
        int size = 0;
        while ((size = entity.getContentStream().read(buff, 0, buff.length)) != -1) {
            if (CANCELLED == entity.getState()) {
                return null;
            }
            baos.write(buff, 0, size);
        }
        return baos.toByteArray();
    }

    /**
     * 获取输入流字节数组
     * @param entity 联网对象
     * @return
     * @throws IOException
     */
    private static byte[] getBytesFromGZIPInputStream(ConnectEntity entity)
            throws IOException {
        if (entity == null || entity.getContentStream() == null) return null;
        GZIPInputStream gzip = new GZIPInputStream(entity.getContentStream());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buff = new byte[mBuffer];
        int size = 0;
        while ((size = gzip.read(buff, 0, buff.length)) != -1) {
            if (CANCELLED == entity.getState()) {
                return null;
            }
            bos.write(buff, 0, size);
        }
        return bos.toByteArray();
    }


}
