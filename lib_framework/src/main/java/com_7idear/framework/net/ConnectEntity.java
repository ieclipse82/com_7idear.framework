package com_7idear.framework.net;

import com_7idear.framework.intface.IState;

import java.io.InputStream;


/**
 * 联网实体类
 * @author ieclipse 19-12-13
 * @description 支持数据状态，请求地址，接口数据或字节流
 */
public class ConnectEntity
        implements IState {

    private int         state; //状态
    private String      url; //地址
    private String      runUrl; //执行地址
    private byte[]      sendBytes; //发送的数据
    private int         requestByteIndex; //请求的数据位置
    private int         requestLength; //请求的数据长度
    private long        contentLength; //联网返回的数据大小
    private String      contentEncoding; //联网返回的数据编码——XML：默认值，GZIP：压缩
    private String      contentString; //返回内容
    private InputStream contentStream; //返回流

    /**
     * 构造方法
     * @param url 地址
     */
    public ConnectEntity(String url) {
        reset();
        this.url = url;
    }

    /**
     * 重置数据
     */
    public void reset() {
        state = UNKNOWN;
        runUrl = null;
        sendBytes = null;
        requestByteIndex = 0;
        requestLength = 0;
        contentLength = 0;
        contentEncoding = Connect.CONTENTENCODING_XML;
        contentStream = null;
    }

    /**
     * 是否取消
     * @return
     */
    public boolean isCancelled() {
        return CANCELLED == state;
    }

    /**
     * 获取状态
     * @return
     */
    public int getState() {
        return state;
    }

    /**
     * 设置状态
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * 获取地址
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置地址
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取执行地址
     * @return
     */
    public String getRunUrl() {
        return runUrl;
    }

    /**
     * 设置执行地址
     * @param runUrl
     */
    public void setRunUrl(String runUrl) {
        this.runUrl = runUrl;
    }

    /**
     * 获取发送的数据
     * @return
     */
    public byte[] getSendBytes() {
        return sendBytes;
    }

    /**
     * 设置发送的数据
     * @param sendBytes
     */
    public void setSendBytes(byte[] sendBytes) {
        this.sendBytes = sendBytes;
    }

    /**
     * 获取请求的数据位置
     * @return
     */
    public int getRequestByteIndex() {
        return requestByteIndex;
    }

    /**
     * 设置请求的数据位置
     * @param requestByteIndex
     */
    public void setRequestByteIndex(int requestByteIndex) {
        this.requestByteIndex = requestByteIndex;
    }

    /**
     * 获取请求的数据长度
     * @return
     */
    public int getRequestLength() {
        return requestLength;
    }

    /**
     * 设置请求的数据长度
     * @param requestLength
     */
    public void setRequestLength(int requestLength) {
        this.requestLength = requestLength;
    }

    /**
     * 获取联网返回的数据大小
     * @return
     */
    public long getContentLength() {
        return contentLength;
    }

    /**
     * 设置联网返回的数据大小
     * @param contentLength
     */
    public void setContentLength(long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * 获取联网返回的数据编码——XML：默认值，GZIP：压缩
     * @return
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * 设置联网返回的数据编码——XML：默认值，GZIP：压缩
     * @param contentEncoding
     */
    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /**
     * 获取返回内容
     * @return
     */
    public String getContentString() {
        return contentString;
    }

    /**
     * 设置返回内容
     * @param contentString
     */
    public void setContentString(String contentString) {
        this.contentString = contentString;
    }

    /**
     * 获取返回流
     * @return
     */
    public InputStream getContentStream() {
        return contentStream;
    }

    /**
     * 设置返回流
     * @param contentStream
     */
    public void setContentStream(InputStream contentStream) {
        this.contentStream = contentStream;
    }

}
