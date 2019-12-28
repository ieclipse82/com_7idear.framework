package com_7idear.tv.uibase;

import android.view.View;

/**
 * UI基础类接口（基类实现）
 * @author ieclipse 19-12-4
 * @description 需要实现获取唯一的焦点视图
 */
public interface ImplUIBaseTVSingleFocus {

    /**
     * 获取可获取焦点视图
     * @return
     */
    View getFocusView();

}
