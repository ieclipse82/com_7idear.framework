package com_7idear.framework.core;

/**
 * 基础页面接口（基类实现）
 * @author ieclipse 19-9-18
 * @description 需要实现动作执行、UI刷新、主题切换
 */
public interface ImplBaseActivity {

    /**
     * 执行动作
     * @param action 动作标识
     * @param what   动作标识（用于Handler刷新）
     * @param obj    对象
     */
    boolean runBaseAction(String action, int what, Object obj);

    /**
     * 当UI刷新
     * @param action 动作标识
     * @param what   动作标识（用于Handler刷新）
     * @param obj    对象
     */
    boolean onBaseUIRefresh(String action, int what, Object obj);

    /**
     * 当主题变换
     * @param themePackageName 主题包名
     * @return
     */
    boolean onBaseThemeChanged(String themePackageName);
}
