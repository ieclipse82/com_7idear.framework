package com_7idear.framework.theme;

import android.view.View;

/**
 * 基础主题接口（基类实现）
 * @author ieclipse 19-12-3
 * @description 需要实现设置布局、查找视图方法
 */
public interface ImplBaseTheme {

    /**
     * 设置视图
     * @param layoutResID 布局资源ID
     */
    void setContentView(int layoutResID);

    /**
     * 查找资源布局
     * @param id 资源ID
     * @return
     */
    View findViewById(int id);
}
