package com_7idear.framework.asynccall;

/**
 * 网址任务回调接口
 * @author ieclipse 19-12-18
 * @description <Out> 输出对象（必须）
 */
public interface IUrlCallback<Out> {
    /**
     * 任务完成
     * @param outEntity 输出对象
     */
    void onFinished(Out outEntity);

    /**
     * 任务失败
     * @param e 失败对象
     */
    void onFailure(AsyncCallException e);
}