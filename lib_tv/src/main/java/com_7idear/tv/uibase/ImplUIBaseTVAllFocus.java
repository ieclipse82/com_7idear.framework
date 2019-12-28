package com_7idear.tv.uibase;

import android.view.View;

import java.util.List;

/**
 * UI基础类接口（基类实现）
 * @author ieclipse 19-12-4
 * @description 需要实现获取全部焦点视图
 */
public interface ImplUIBaseTVAllFocus {

    /**
     * 获取可获取焦点视图列表
     * @return
     */
    List<View> getFocusViews();

}
