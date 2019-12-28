package com_7idear.framework.adapter;

/**
 * 基础页面改变接口（基类实现）
 * @author ieclipse 19-12-6
 * @description 用于 {@link androidx.viewpager.widget.PagerAdapter} 动态替换页面
 */
public interface ImplBasePageChange {

    /**
     * 页面未改变（默认值）
     */
    public static final int PAGE_UNCHANGED = -1;
    /**
     * 页面改变（需要移除或替换）
     */
    public static final int PAGE_CHANGED   = -2;

    /**
     * 获取页面是否改变
     * @return
     */
    int getPageChanged();

    /**
     * 设置页面改变
     */
    void setPageChanged();
}
