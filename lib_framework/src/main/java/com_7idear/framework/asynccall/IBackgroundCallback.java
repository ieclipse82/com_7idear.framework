package com_7idear.framework.asynccall;

/**
 * 后台任务回调接口
 * @author ieclipse 19-12-18
 * @description <In>  输入对象（必须） <Out> 输出对象（必须）
 */
public interface IBackgroundCallback<In, Out> {
    /**
     * 当前后台线程
     * @param inEntity 输入对象
     * @return
     */
    Out onBackground(In inEntity, IBackgroundHelper<In> helper);

    /**
     * 当前UI线程进度刷新
     * @param inEntity 输入对象
     * @param progress 进度
     */
    void onUIProgress(In inEntity, int progress);

    /**
     * 任务完成
     * @param outEntity 输出对象
     */
    void onFinished(Out outEntity);
}