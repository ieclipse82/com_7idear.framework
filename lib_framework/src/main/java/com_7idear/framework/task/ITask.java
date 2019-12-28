package com_7idear.framework.task;

/**
 * 任务接口（业务实现）
 * @author ieclipse 19-12-16
 * @description 使用任务调度需要实现此接口（包括：请求网络，下载数据，执行后台任务）
 */
public interface ITask {

    /**
     * 任务开始
     * @param action   标识
     * @param inEntity 输入对象
     */
    void onTaskBegin(String action, Object inEntity);

    /**
     * 任务进行中
     * @param action 动作标识
     * @param what   动作标识（用于Handler刷新）
     * @param obj    对象
     */
    void onTaskProgress(String action, int what, Object obj);

    /**
     * 任务完成
     * @param action    标识
     * @param inEntity  输入对象
     * @param outEntity 输出对象
     */
    void onTaskFinished(String action, Object inEntity, Object outEntity);

    /**
     * 任务出错
     * @param action   标识
     * @param inEntity 输入对象
     * @param state    状态
     */
    void onTaskError(String action, Object inEntity, int state);

}