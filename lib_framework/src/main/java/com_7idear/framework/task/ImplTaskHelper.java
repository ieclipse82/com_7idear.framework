package com_7idear.framework.task;

/**
 * 任务帮助接口（下载类实现）
 * @author ieclipse 19-12-16
 * @description 需要实现任务是否取消和UI刷新方法
 */
public interface ImplTaskHelper {

    /**
     * 任务是否取消
     * @return
     */
    boolean isTaskCancel();

    /**
     * 当UI刷新
     * @param action 动作标识
     * @param what   动作标识（用于Handler刷新）
     * @param obj    对象
     */
    void onUIRefresh(String action, int what, Object obj);
}
