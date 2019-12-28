package com_7idear.framework.statistics;

/**
 * 统计接口
 * @author ieclipse 19-12-16
 * @description
 */
public interface IStatistics {

    /**
     * 类型——未知
     */
    int TYPE_UNKOWN     = 0;
    /**
     * 类型——曝光
     */
    int TYPE_VIEW       = 1 << 0;
    /**
     * 类型——点击
     */
    int TYPE_CLICK      = 1 << 1;
    /**
     * 类型——事件（自定义）
     */
    int TYPE_EVENT_KEY  = 1 << 2;
    /**
     * 类型——事件队列（数据内容）
     */
    int TYPE_EVENT_LIST = 1 << 3;
}
