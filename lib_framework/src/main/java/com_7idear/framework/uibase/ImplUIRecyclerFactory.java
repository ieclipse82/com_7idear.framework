package com_7idear.framework.uibase;

import android.content.Context;
import android.view.ViewGroup;

/**
 * UI基础工厂类接口（基类实现）
 * @author ieclipse 19-12-4
 * @description 需要实现getUIFactoryView方法完成UI工厂的视图创建
 */
public interface ImplUIRecyclerFactory {

    /**
     * 获取UI视图
     * @param context    环境对象
     * @param layoutType 布局类型
     * @param parent     父容器
     * @return
     */
    UIRecyclerContainerLayout getUIFactoryView(Context context, int layoutType, ViewGroup parent);

}
