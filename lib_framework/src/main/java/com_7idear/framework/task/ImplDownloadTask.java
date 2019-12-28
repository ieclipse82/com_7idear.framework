package com_7idear.framework.task;

/**
 * 下载任务接口（任务工具类实现）
 * @author ieclipse 19-12-16
 * @description 需要实现下载的开始，进行中，完成和失败方法
 */
public interface ImplDownloadTask {

    /**
     * 下载开始前
     * @param taskEntity 任务对象
     */
    void onDownloadBegin(TaskEntity taskEntity);

    /**
     * 下载开始中，UI刷新
     * @param taskEntity 任务对象
     * @param action     动作标识
     * @param what       动作标识（用于Handler刷新）
     * @param obj        对象
     */
    void onDownloadProgress(TaskEntity taskEntity, String action, int what, Object obj);

    /**
     * 下载完成
     * @param taskEntity 任务对象
     */
    void onDownloadFinished(TaskEntity taskEntity);

    /**
     * 下载出错
     * @param taskEntity 任务对象
     */
    void onDownloadError(TaskEntity taskEntity);

}