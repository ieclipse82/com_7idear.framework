package com_7idear.framework.statistics;

import com_7idear.framework.router.RouterEntity;

import java.util.Map;


/**
 * 统计客户端（抽象父类）
 * @author ieclipse 19-12-16
 * @description 根据构造的统计类型实现客户端各事件统计 {@link IStatistics}
 * 1：type——统计类型可叠加（与），事件匹配上就回调相应的统计方法
 * 2：eventKey——事件KEY，统计类型匹配后，再匹配事件KEY并执行（eventKey为NULL方法总是被执行）
 * 3：eventListHost——事件HOST，统计类型匹配后，再匹配数据内容URI中的HOST并执行
 */
public abstract class StatisticsClient
        implements IStatistics {
    private final int    type; //统计类型
    private final String eventKey; //事件KEY
    private final String eventListHost; //事件队列

    StatisticsClient(int type, String eventKey, String eventListHost) {
        this.type = type;
        this.eventKey = eventKey;
        this.eventListHost = eventListHost;
    }

    public int getType() {
        return type;
    }

    public String getEventKey() {
        return eventKey;
    }

    public String getEventListHost() {
        return eventListHost;
    }

    /**
     * 曝光统计
     * @param link   路由对象
     * @param entity 统计对象
     * @return
     */
    protected abstract boolean onViewStatistics(RouterEntity link, StatisticsEntity entity);

    /**
     * 点击统计
     * @param link   路由对象
     * @param entity 统计对象
     * @return
     */
    protected abstract boolean onClickStatistics(RouterEntity link, StatisticsEntity entity);

    /**
     * 事件统计
     * @param eventKey 事件KEY
     * @param params   事件参数
     * @param entity   统计对象
     * @return
     */
    protected abstract boolean onEventKeyStatistics(String eventKey, Map<String, String> params,
            StatisticsEntity entity);

    /**
     * 事件队列统计（比对eventList，可能会调用多次）
     * @param link   路由对象
     * @param entity 统计对象
     * @return
     */
    protected abstract boolean onEventListStatistics(RouterEntity link, StatisticsEntity entity);
}
