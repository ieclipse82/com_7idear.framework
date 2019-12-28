package com_7idear.framework.uibase;

/**
 * UI基础接口
 * @author ieclipse 19-11-29
 * @description 需要实现UI初始化、绑定，解绑等能力
 */
public interface IUIBase {

    /**
     * 动作——绑定数据
     */
    String ACTION_BIND_VALUE = "ACTION_BIND_VALUE";

    /**
     * 获取UI布局资源
     * @return
     */
    int getUILayoutResID();

    /**
     * 初始化UI
     */
    void initUI();

    /**
     * 绑定UI
     * @param action   动作标识
     * @param obj      数据
     * @param position 索引
     * @param isFirst  是否是第一项
     * @param isLast   是否是最后一项
     * @return
     */
    boolean onUIBind(String action, Object obj, int position, boolean isFirst, boolean isLast);

    /**
     * 解绑UI
     * @return
     */
    boolean onUIUnBind();

    /**
     * UI添加到展示区（可能还未显示出来）
     * {@link UIRecyclerAdapter onViewAttachedToWindow()}
     */
    void onUIAttached();

    /**
     * UI从展示区移除
     * {@link UIRecyclerAdapter onViewDetachedFromWindow()}
     */
    void onUIDetached();
}
