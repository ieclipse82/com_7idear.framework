package com_7idear.framework.task;

/**
 * 后台任务接口监听器
 * @author ieclipse 19-12-16
 * @description
 */
public interface IBackgroundToDo<In, Out>
        extends ITaskToDo {

    /**
     * 执行后台任务
     * @param action   动作标识
     * @param inEntity 输入对象
     * @param helper   任务帮助对象
     * @return
     */
    Out runBackground(String action, In inEntity, ImplTaskHelper helper);

}
