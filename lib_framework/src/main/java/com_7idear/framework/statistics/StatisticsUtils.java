package com_7idear.framework.statistics;

import com_7idear.framework.async.AsyncMsgQueueThread;
import com_7idear.framework.config.BaseConfig;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.router.RouterEntity;
import com_7idear.framework.utils.EntityUtils;
import com_7idear.framework.utils.OperatorUtils;
import com_7idear.framework.utils.TxtUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * 统计工具类
 * @author ieclipse 19-12-16
 * @description
 */
public class StatisticsUtils
        extends BaseConfig {

    private static final String TAG = "StatisticsUtils";

    private static StatisticsUtils mInstance;

    private IGlobalPreStatistics                  mPreStatistics; //解析器列表
    private List<IStatisticsParser>               mParserList; //解析器列表
    private List<StatisticsClient>                mClientList; //统计打点客户端
    private LinkedBlockingQueue<StatisticsEntity> mCacheStatisticsQueue;
    private AsyncMsgQueueThread<StatisticsEntity> mMsgHandler; //异步消息对象

    private boolean isStatistics = false; //是否开启统计功能

    StatisticsUtils() {
        mParserList = new ArrayList<>();
        mClientList = new ArrayList<>();
        mCacheStatisticsQueue = new LinkedBlockingQueue<>();
        mMsgHandler = new AsyncMsgQueueThread<StatisticsEntity>(TAG) {
            @Override
            protected void onAsyncMessage(int action, StatisticsEntity msg) {
                if (msg == null) return;

                final int type = action;
                List<StatisticsEntity> list = getParseStatisticsList(type, msg);
                if (EntityUtils.isEmpty(list)) return;
                log().append("type", getTypeString(type))
                     .append("list", list)
                     .append("size", list.size())
                     .toLogD();

                for (StatisticsEntity item : list) {
                    if (item == null) continue;
                    if (item.getRunnable() != null) {
                        runRunnableStatistics(type, item);
                    } else {
                        runClientStatistics(type, item);
                    }
                }
            }

            /**
             * 获取解析的统计队列
             * @param type
             * @param entity
             * @return
             */
            private List<StatisticsEntity> getParseStatisticsList(int type,
                    StatisticsEntity entity) {
                List<StatisticsEntity> list = new ArrayList<>();
                synchronized (mParserList) {
                    for (IStatisticsParser parser : mParserList) {
                        switch (type) {
                            case IStatistics.TYPE_VIEW:
                                if (parser.isParseViewStatisticsFinished(entity, list)) {
                                    return list;
                                }
                                continue;
                            case IStatistics.TYPE_CLICK:
                                if (parser.isParseClickStatisticsFinished(entity, list)) {
                                    return list;
                                }
                                continue;
                            case IStatistics.TYPE_EVENT_KEY:
                                if (parser.isParseEventKeyStatisticsFinished(entity, list)) {
                                    return list;
                                }
                                continue;
                            case IStatistics.TYPE_EVENT_LIST:
                                if (parser.isParseEventListStatisticsFinished(entity, list)) {
                                    return list;
                                }
                                continue;
                            default:
                        }
                    }
                }
                return list;
            }

            /**
             * 执行数据对象自定义统计打点
             * @param type 类型
             * @param entity 统计对象
             */
            private void runRunnableStatistics(int type, StatisticsEntity entity) {
                log().append("type", getTypeString(type)).append("entity", entity).toLogD();
                switch (type) {
                    case IStatistics.TYPE_VIEW:
                        entity.getRunnable().runViewStatistics(entity.getLink(), entity);
                        break;
                    case IStatistics.TYPE_CLICK:
                        entity.getRunnable().runClickStatistics(entity.getLink(), entity);
                        break;
                    case IStatistics.TYPE_EVENT_KEY:
                        entity.getRunnable()
                              .runEventKeyStatistics(entity.getEventKey(), entity.getParams(),
                                      entity);
                        break;
                    default:
                }
            }

            /**
             * 执行客户端统计打点
             * @param type 类型
             * @param entity 统计对象
             */
            private void runClientStatistics(int type, StatisticsEntity entity) {
                LogEntity log = new LogEntity(isLog()).append("type", getTypeString(type))
                                                      .appendLine("entity", entity);
                synchronized (mClientList) {
                    for (StatisticsClient client : mClientList) {
                        //曝光打点，只打注册了TYPE_VIEW事件的CLIENT
                        if (IStatistics.TYPE_VIEW == type && OperatorUtils.equalsAndValue(
                                IStatistics.TYPE_VIEW, client.getType())) {
                            appendLog(type, entity, client, null, log);

                            client.onViewStatistics(entity.getLink(), entity);
                        }
                        //点击打点，只打注册了TYPE_CLICK事件的CLIENT
                        if (IStatistics.TYPE_CLICK == type && OperatorUtils.equalsAndValue(
                                IStatistics.TYPE_CLICK, client.getType())) {
                            appendLog(type, entity, client, null, log);

                            client.onClickStatistics(entity.getLink(), entity);
                        }
                        //事件打点，只打注册了TYPE_EVENT_KEY事件的CLIENT
                        if (IStatistics.TYPE_EVENT_KEY == type && OperatorUtils.equalsAndValue(
                                IStatistics.TYPE_EVENT_KEY, client.getType())) {
                            appendLog(type, entity, client, null, log);

                            if (client.getEventKey() == null) { //不匹配EventKey执行全部统计
                                client.onEventKeyStatistics(entity.getEventKey(),
                                        entity.getParams(), entity);
                            } else if (TxtUtils.equalsIgnoreCase(entity.getEventKey(),
                                    client.getEventKey())) { //匹配EventKey执行统计
                                client.onEventKeyStatistics(entity.getEventKey(),
                                        entity.getParams(), entity);
                            }
                        }
                        //事件队列打点，只打注册了TYPE_EVENT_LIST事件的CLIENT，并且比对HOST
                        String host = client.getEventListHost();
                        if ((IStatistics.TYPE_EVENT_LIST == type
                                || IStatistics.TYPE_VIEW == type
                                || IStatistics.TYPE_CLICK == type) && OperatorUtils.equalsAndValue(
                                IStatistics.TYPE_EVENT_LIST, client.getType()) && !TxtUtils.isEmpty(
                                host) && EntityUtils.isNotEmpty(entity.getEventList())) {
                            appendLog(type, entity, client, host, log);

                            for (int i = 0; i < entity.getEventList().size(); i++) {
                                RouterEntity event = entity.getEventList().get(i);
                                if (TxtUtils.equalsIgnoreCase(event.getHost(),
                                        host)) { //匹配EventListHost执行统计
                                    client.onEventListStatistics(event, entity);
                                }
                            }
                        }
                    }
                }
            }

            /**
             * 添加日志
             * @param type 类型
             * @param entity 统计对象
             * @param client 统计客户端
             * @param host 匹配标识
             * @param log 日志对象
             */
            private void appendLog(int type, StatisticsEntity entity, StatisticsClient client,
                    String host, LogEntity log) {
                if (!isLog()) return;
                switch (type) {
                    case IStatistics.TYPE_VIEW:
                    case IStatistics.TYPE_CLICK:
                        log.append("getLink", entity.getLink()).appendLine("client", client);
                        return;
                    case IStatistics.TYPE_EVENT_KEY:
                        log.append("getEventKey", entity.getEventKey())
                           .append("getParams", entity.getParams())
                           .appendLine("client", client);
                        return;
                    case IStatistics.TYPE_EVENT_LIST:
                        log.append("host", host)
                           .append("getEventList", entity.getEventList())
                           .appendLine("client", client);
                        return;
                    default:
                }
            }

        };
    }

    public static StatisticsUtils getInstance() {
        if (mInstance == null) {
            synchronized (StatisticsUtils.class) {
                if (mInstance == null) {
                    mInstance = new StatisticsUtils();
                }
            }
        }
        return mInstance;
    }

    public void enableStatistics() {
        isStatistics = true;
    }

    public void disableStatistics() {
        isStatistics = false;
    }

    /**
     * 设置全局统计预处理接口
     * @param preStatistics
     */
    public StatisticsUtils setGlobalPreStatistics(IGlobalPreStatistics preStatistics) {
        log().append(preStatistics).toLogD();
        mPreStatistics = preStatistics;
        return this;
    }

    /**
     * 添加统计解析器
     * @param parser 解析器
     * @return
     */
    public StatisticsUtils addStatisticsParser(IStatisticsParser parser) {
        if (parser == null) return this;
        log().append(parser).toLogD();
        mParserList.add(parser);
        return this;
    }

    /**
     * 移除统计解析器
     * @param parser 解析器
     * @return
     */
    public StatisticsUtils removeStatisticsParser(IStatisticsParser parser) {
        if (parser == null) return this;
        log().append(parser).toLogD();
        mParserList.remove(parser);
        return this;
    }

    /**
     * 清除全部解析器
     */
    public void clearStatisticsParser() {
        log().toLogD();
        mParserList.clear();
    }

    /**
     * 注册统计客户端
     * @param client 客户端
     * @return
     */
    public StatisticsUtils registerStatisticsClient(StatisticsClient client) {
        if (client == null || mClientList.contains(client)) return this;
        log().append(client).toLogD();
        mClientList.add(client);
        return this;
    }

    /**
     * 解注册统计客户端
     * @param client 客户端
     * @return
     */
    public StatisticsUtils unregisterStatisticsClient(StatisticsClient client) {
        if (client == null) return this;
        log().append(client).toLogD();
        mClientList.remove(client);
        return this;
    }

    /**
     * 清除统计客户端
     */
    public void clearStatisticsClient() {
        log().toLogD();
        mClientList.clear();
    }

    /**
     * 上报曝光统计
     * @param entity 统计对象
     * @return
     */
    public boolean reportViewStatistics(StatisticsEntity entity) {
        if (entity == null) return false;
        return reportStatistics(entity.setType(IStatistics.TYPE_VIEW));
    }

    /**
     * 上报点击统计
     * @param entity 统计对象
     * @return
     */
    public boolean reportClickStatistics(StatisticsEntity entity) {
        if (entity == null) return false;
        return reportStatistics(entity.setType(IStatistics.TYPE_CLICK));
    }

    /**
     * 上报事件统计
     * @param entity 统计对象
     * @return
     */
    public boolean reportEventKeyStatistics(StatisticsEntity entity) {
        if (entity == null) return false;
        return reportStatistics(entity.setType(IStatistics.TYPE_EVENT_KEY));
    }

    private boolean reportStatistics(StatisticsEntity entity) {
        if (mPreStatistics != null) {
            log().append("onPreStatistics", mPreStatistics).append("entity", entity).toLogD();
            mPreStatistics.onPreStatistics(entity);
        }
        if (!isStatistics) {
            log().append("isStatistics", isStatistics).append("entity", entity).toLogD();
            mCacheStatisticsQueue.add(entity);
            return false;
        }
        log().append("type", getTypeString(entity.getType())).append("entity", entity).toLogD();
        mMsgHandler.sendMsg(entity.getType(), entity);
        return true;
    }

    private String getTypeString(int type) {
        switch (type) {
            case IStatistics.TYPE_VIEW:
                return "TYPE_VIEW";
            case IStatistics.TYPE_CLICK:
                return "TYPE_CLICK";
            case IStatistics.TYPE_EVENT_KEY:
                return "TYPE_EVENT_KEY";
            case IStatistics.TYPE_EVENT_LIST:
                return "TYPE_EVENT_LIST";
            default:
                return "TYPE_UNKOWN";
        }
    }

    /**
     * 全局统计预处理接口
     */
    public interface IGlobalPreStatistics {
        /**
         * 预处理统计
         * @param entity 统计对象
         */
        void onPreStatistics(StatisticsEntity entity);
    }

    /**
     * 统计解析接口
     */
    public interface IStatisticsParser {
        /**
         * 是否完成解析曝光数据
         * @param entity   统计对象
         * @param realList 真实的统计数据列表
         * @return
         */
        boolean isParseViewStatisticsFinished(StatisticsEntity entity,
                List<StatisticsEntity> realList);

        /**
         * 是否完成解析点击数据
         * @param entity   统计对象
         * @param realList 真实的统计数据列表
         * @return
         */
        boolean isParseClickStatisticsFinished(StatisticsEntity entity,
                List<StatisticsEntity> realList);

        /**
         * 是否完成解析事件数据
         * @param entity   统计对象
         * @param realList 真实的统计数据列表
         * @return
         */
        boolean isParseEventKeyStatisticsFinished(StatisticsEntity entity,
                List<StatisticsEntity> realList);

        /**
         * 是否完成解析事件队列数据
         * @param entity   统计对象
         * @param realList 真实的统计数据列表
         * @return
         */
        boolean isParseEventListStatisticsFinished(StatisticsEntity entity,
                List<StatisticsEntity> realList);

    }
}
