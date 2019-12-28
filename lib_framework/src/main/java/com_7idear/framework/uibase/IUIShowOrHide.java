package com_7idear.framework.uibase;

/**
 * UI显示或隐藏接口
 * @author ieclipse 19-12-4
 * @description 需要实现手动调用UI显示、UI隐藏的方法
 */
public interface IUIShowOrHide {

    /**
     * 显示UI
     * @param action 动作标识
     */
    void onUIShow(String action);

    /**
     * 隐藏UI
     * @param action 动作标识
     */
    void onUIHide(String action);


}
