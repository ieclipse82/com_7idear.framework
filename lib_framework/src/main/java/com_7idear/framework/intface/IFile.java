package com_7idear.framework.intface;

/**
 * 文件状态和操作状态接口
 * @author ieclipse 19-12-16
 * @description
 */
public interface IFile {

    /**
     * 文件状态——0：未下载
     */
    int STATE_UNDOWNLOAD        = 0;
    /**
     * 文件状态——1：下载中
     */
    int STATE_DOWNLOADING       = 1;
    /**
     * 文件状态——2：下载出错
     */
    int STATE_DOWNLOAD_ERROR    = 2;
    /**
     * 文件状态——3：下载完成
     */
    int STATE_DOWNLOAD_FINISHED = 3;
    /**
     * 文件状态——4：安装中
     */
    int STATE_INSTALLING        = 4;
    /**
     * 文件状态——5：安装出错
     */
    int STATE_INSTALL_ERROR     = 5;
    /**
     * 文件状态——6：安装完成
     */
    int STATE_INSTALL_FINISHED  = 6;

    /**
     * 文件操作状态——0：停止
     */
    int OPERATION_STOP  = 0;
    /**
     * 文件操作状态——1：开始
     */
    int OPERATION_START = 1;
    /**
     * 文件操作状态——2：暂停
     */
    int OPERATION_PAUSE = 2;
    /**
     * 文件操作状态——3：重试
     */
    int OPERATION_RETRY = 3;
    /**
     * 文件操作状态——4：等待
     */
    int OPERATION_WAIT  = 4;
}
