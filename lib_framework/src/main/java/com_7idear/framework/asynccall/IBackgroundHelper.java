package com_7idear.framework.asynccall;

/**
 * 异步任务帮助接口，用于取消并退出线程或执行UI进度刷新
 * @author ieclipse 19-12-18
 * @description <In> 输入对象（必须）
 */
public interface IBackgroundHelper<In> {
    /**
     * 是否取消
     * @return
     */
    boolean isCanceled();

    /**
     * 执行UI进度刷新
     * @param inEntity 输入对象
     * @param progress 进度
     */
    void runUIProgress(In inEntity, int progress);
}