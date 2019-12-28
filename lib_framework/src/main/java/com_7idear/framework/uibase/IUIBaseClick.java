package com_7idear.framework.uibase;

import android.view.View;

/**
 * UI点击事件监听器接口
 * @author ieclipse 19-11-29
 * @description
 */
public interface IUIBaseClick {

    /**
     * 获取UI单击事件监听器
     * @return
     */
    View.OnClickListener getUIClickListener();

    /**
     * 设置UI单击事件监听器
     * @param clickListener 监听器
     */
    void setUIClickListener(View.OnClickListener clickListener);

    /**
     * 获取UI长按事件监听器
     * @return
     */
    View.OnLongClickListener getUILongClickListener();

    /**
     * 设置UI长按事件监听器
     * @param longClickListener 监听器
     */
    void setUILongClickListener(View.OnLongClickListener longClickListener);
}
