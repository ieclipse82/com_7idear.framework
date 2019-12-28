package com_7idear.framework.core;


import com_7idear.framework.page.PageEntity;

/**
 * 标准页面接口
 * @author ieclipse 19-9-18
 * @description 需要实现创建页面对象、获取布局资源、初始化、动作执行、UI刷新
 */
public interface IActivity<D extends BaseData> {

    /**
     * 创建页面对象
     * @return
     */
    PageEntity<D> createPage();

    /**
     * 获取布局资源ID
     * @return
     */
    int getUILayoutResID();

    /**
     * 初始化视图
     */
    void initFindViews();

    /**
     * 初始化视图值
     */
    void initViewsValue();

    /**
     * 执行动作
     * @param action 动作标识
     * @param what   动作标识（用于Handler刷新）
     * @param obj    对象
     */
    boolean runAction(String action, int what, Object obj);

    /**
     * 当UI刷新
     * @param action 动作标识
     * @param what   动作标识（用于Handler刷新）
     * @param obj    对象
     */
    boolean onUIRefresh(String action, int what, Object obj);
}
