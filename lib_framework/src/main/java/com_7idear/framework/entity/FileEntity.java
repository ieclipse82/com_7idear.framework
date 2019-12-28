package com_7idear.framework.entity;

import com_7idear.framework.intface.IFile;
import com_7idear.framework.utils.TxtUtils;

import java.io.Serializable;


/**
 * 文件实体类
 * @author ieclipse 19-12-10
 * @description 实现文件信息存储，状态和操作状态
 */
public class FileEntity
        implements IFile, Serializable {
    private static final long serialVersionUID = 1L;

    private int    state     = STATE_UNDOWNLOAD; //文件状态标示—— 0：未下载，1：下载中，2：下载出错，3：下载完成，4：安装中，5：安装出错，6：安装完成
    private int    operation = OPERATION_STOP; //文件操作标示——0：停止，1：开始，2：暂停，3：重试，4：等待
    private int    hashCode; //文件HASHCODE值
    private String url; //文件下载地址
    private String params; //文件下载参数
    private String path; //文件夹路径
    private String cache; //文件缓存路径
    private String name; //文件名称
    private String extName; //文件扩展名
    private String mime; //文件MIME类型
    private int    size; //文件大小
    private long   downloadSize; //文件下载大小
    private long   downloadingSpeed; //文件下载速度

    public FileEntity() {

    }

    public FileEntity(String path, String name) {
        this.path = path;
        this.name = name;
    }

    public FileEntity(String url, String path, String name, String extName, int state) {
        this.url = url;
        this.path = path;
        this.name = name;
        this.extName = extName;
        this.state = state;
    }

    /**
     * 获取文件路径
     * @return
     */
    public String getFilePath() {
        return path + "/" + name + (TxtUtils.isEmpty(extName) ? "" : extName);
    }

    /**
     * 获取缓存路径
     * @return
     */
    public String getCachePath() {
        return cache + "/" + name + (TxtUtils.isEmpty(extName) ? "" : extName);
    }

    /**
     * 获取文件状态标示——0：未下载，1：下载中，2：下载出错，3：下载完成，4：安装中，5：安装出错，6：安装完成
     * @return
     */
    public int getState() {
        return state;
    }

    /**
     * 设置文件状态标示——0：未下载，1：下载中，2：下载出错，3：下载完成，4：安装中，5：安装出错，6：安装完成
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * 获取文件操作标示——0：停止，1：开始，2：暂停，3：重试，4：等待
     * @return
     */
    public int getOperation() {
        return operation;
    }

    /**
     * 设置文件操作标示——0：停止，1：开始，2：暂停，3：重试，4：等待
     * @param operation
     */
    public void setOperation(int operation) {
        this.operation = operation;
    }

    /**
     * 获取文件HASHCODE值
     * @return
     */
    public int getHashCode() {
        return hashCode;
    }

    /**
     * 设置文件HASHCODE值
     * @param hashCode
     */
    public void setHashCode(int hashCode) {
        this.hashCode = hashCode;
    }

    /**
     * 获取文件下载地址
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置文件下载地址
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 获取文件下载参数
     * @return
     */
    public String getParams() {
        return params;
    }

    /**
     * 设置文件下载参数
     * @param params
     */
    public void setParams(String params) {
        this.params = params;
    }

    /**
     * 获取文件保存路径
     * @return
     */
    public String getPath() {
        return path;
    }

    /**
     * 设置文件保存路径
     * @param path
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * 获取文件缓存路径
     * @return
     */
    public String getCache() {
        return cache;
    }

    /**
     * 设置文件缓存路径
     * @param cache
     */
    public void setCache(String cache) {
        this.cache = cache;
    }

    /**
     * 获取文件名称
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * 设置文件名称
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取文件扩展名
     * @return
     */
    public String getExtName() {
        return extName;
    }

    /**
     * 设置文件扩展名
     * @param extName
     */
    public void setExtName(String extName) {
        this.extName = extName;
    }

    /**
     * 获取文件MIME类型
     * @return
     */
    public String getMime() {
        return mime;
    }

    /**
     * 设置文件MIME类型
     * @param mime
     */
    public void setMime(String mime) {
        this.mime = mime;
    }

    /**
     * 获取文件大小
     * @return
     */
    public int getSize() {
        return size;
    }

    /**
     * 设置文件大小
     * @param size
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 获取文件下载大小
     * @return
     */
    public long getDownloadSize() {
        return downloadSize;
    }

    /**
     * 设置文件下载大小
     * @param downloadSize
     */
    public void setDownloadSize(long downloadSize) {
        this.downloadSize = downloadSize;
    }

    /**
     * 获取文件下载速度
     * @return
     */
    public long getDownloadingSpeed() {
        return downloadingSpeed;
    }

    /**
     * 设置文件下载速度
     * @param downloadingSpeed
     */
    public void setDownloadingSpeed(long downloadingSpeed) {
        this.downloadingSpeed = downloadingSpeed;
    }

}
