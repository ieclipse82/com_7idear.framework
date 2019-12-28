package com_7idear.framework.statistics;

import com_7idear.framework.router.RouterEntity;
import com_7idear.framework.utils.EntityUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 统计实体类
 * @author ieclipse 19-12-16
 * @description 支持数据内容的曝光，点击，事件以及统计打点队列
 * 1：数据曝光、点击需要配置（link）字段
 * 2：数据事件需要配置（eventKey，params）字段
 * 3：数据队列事件打点需要配置（eventList）字段
 */
public class StatisticsEntity {

    private int type;

    private String             link; //URI链接地址
    private List<String>       eventList; //事件列表地址
    private RouterEntity       linkUri; //URI链接地址
    private List<RouterEntity> eventUriList; //事件列表地址

    private String              eventKey; //事件关键字
    private Map<String, String> params; //事件参数

    private IStatisticsRunnable runnable; //统计自定义执行接口

    private Object entity; //数据对象

    private String formPage; //页面来源
    private String formRef; //渠道来源

    public StatisticsEntity(String link, List<String> eventList) {
        this.link = link;
        this.eventList = eventList;
    }

    public StatisticsEntity(String eventKey, HashMap<String, String> params) {
        this.eventKey = eventKey;
        this.params = params;
    }

    public StatisticsEntity(IStatisticsRunnable runnable) {
        this.runnable = runnable;
    }

    StatisticsEntity setType(int type) {
        this.type = type;
        return this;
    }

    public int getType() {
        return type;
    }

    public RouterEntity getLink() {
        if (linkUri == null) linkUri = new RouterEntity(link);
        return linkUri;
    }

    public List<RouterEntity> getEventList() {
        if (eventUriList == null) {
            eventUriList = new ArrayList<>();
            if (EntityUtils.isNotEmpty(eventList)) {
                for (String s : eventList) {
                    eventUriList.add(new RouterEntity(s));
                }
            }
        }
        return eventUriList;
    }

    public String getEventKey() {
        return eventKey == null ? "" : eventKey;
    }

    public Map<String, String> getParams() {
        if (params == null) params = new HashMap<>();
        return params;
    }

    public IStatisticsRunnable getRunnable() {
        return runnable;
    }

    public Object getEntity() {
        return entity;
    }

    public StatisticsEntity setEntity(Object entity) {
        this.entity = entity;
        return this;
    }

    /**
     * 统计自定义执行接口
     */
    public interface IStatisticsRunnable {
        /**
         * 曝光统计
         * @param link   路由对象
         * @param entity 统计对象
         * @return
         */
        void runViewStatistics(RouterEntity link, StatisticsEntity entity);

        /**
         * 点击统计
         * @param link   路由对象
         * @param entity 统计对象
         * @return
         */
        void runClickStatistics(RouterEntity link, StatisticsEntity entity);

        /**
         * 事件统计
         * @param eventKey 事件KEY
         * @param params   事件参数
         * @param entity   统计对象
         * @return
         */
        void runEventKeyStatistics(String eventKey, Map<String, String> params,
                StatisticsEntity entity);
    }
}
