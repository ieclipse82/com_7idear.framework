package com_7idear.framework.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com_7idear.framework.log.LogEntity;
import com_7idear.framework.log.LogUtils;
import com_7idear.framework.utils.TxtUtils;

import java.util.LinkedList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * 全局路由工具类
 * @author iEclipse 2019/8/12
 * @description
 */
public class RouterUtils
        implements IRouter {

    private static RouterUtils         mInstance;
    private        List<IRouterFilter> mFilters = new LinkedList<IRouterFilter>();

    public static RouterUtils getInstance() {
        if (mInstance == null) {
            synchronized (RouterUtils.class) {
                if (mInstance == null) {
                    mInstance = new RouterUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 获取全部APP筛选器
     * @return
     */
    public List<IRouterFilter> getFilters() {
        return mFilters;
    }

    /**
     * 添加APP筛选器
     * @param filter 筛选器
     */
    public void registerAppFilter(IRouterFilter filter) {
        synchronized (mFilters) {
            if (!mFilters.contains(filter)) mFilters.add(filter);
        }
    }

    /**
     * 注销APP筛选器
     * @param filter 筛选器
     */
    public void unregisterAppFilter(IRouterFilter filter) {
        synchronized (mFilters) {
            if (filter != null) mFilters.remove(filter);
        }
    }

    /**
     * 清除APP筛选器
     */
    public void clearAppFilter() {
        mFilters.clear();
    }

    /**
     * 打开链接
     * @param context     环境对象
     * @param host        链接地址
     * @param bundle      附带参数
     * @param requestCode 请求编码
     * @return
     */
    public boolean openHostLink(Context context, String host, Bundle bundle, int requestCode) {
        return openLink(context, createLinkHost(host), null, bundle, requestCode);
    }

    /**
     * @param context     环境对象
     * @param link        链接地址
     * @param eventList   事件列表
     * @param bundle      附带参数
     * @param requestCode 请求编码
     * @return
     */
    public boolean openLink(Context context, String link, List<String> eventList, Bundle bundle,
            int requestCode) {
        return openLink(context, new RouterEntity(link, eventList), bundle, requestCode);
    }

    /**
     * 打开链接
     * @param context     环境对象
     * @param entity      路由对象
     * @param bundle      附带参数
     * @param requestCode 请求编码
     * @return
     */
    public boolean openLink(Context context, RouterEntity entity, Bundle bundle, int requestCode) {
        if (context == null || entity == null) {
            return false;
        }
        new LogEntity().append(entity).toLogD("openLink");

        //        StatisticsUtils.getInstance().addStatistics(StatisticsUtils.STATISTICS_ACTION.ACTION_CLICK, entity, additions);

        Intent intent = null;
        if (Scheme.SCHEME_APP.equalsIgnoreCase(entity.getScheme())) {
            for (IRouterFilter filter : mFilters) {
                intent = filter.getIntentWithRouterEntity(context, intent, entity, bundle);
                if (intent != null) {
                    break;
                }
            }
        }

        if (intent == null) {
            return false;
        } else {
            intent.putExtra(Params.LINK, entity.getLink());
            //TODO
            intent.putExtra(Params.BACK_SCHEME, entity.getLink());
        }

        try {
            if (requestCode > 0) {
                if (context instanceof Activity) {
                    ((Activity) context).startActivityForResult(intent, requestCode);
                } else if (context instanceof FragmentActivity) {
                    ((FragmentActivity) context).startActivityForResult(intent, requestCode);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } else {
                if (context instanceof Activity) {
                    ((Activity) context).startActivity(intent);
                } else if (context instanceof FragmentActivity) {
                    ((FragmentActivity) context).startActivity(intent);
                } else {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            LogUtils.catchException(e);
            return false;
        }
        return true;
    }

    /**
     * 生成协议
     * @param host 动作标识
     * @return
     */
    public static String createLinkHost(String host) {
        return createLink(Scheme.SCHEME_APP, host, null, null);
    }

    /**
     * 生成协议
     * @param scheme 协议体系
     * @param host   动作标识
     * @param path   路径标识
     * @param params 参数
     * @return
     */
    public static String createLink(String scheme, String host, String path, String[] params) {
        StringBuilder sb = new StringBuilder(TxtUtils.isEmpty(scheme, ""));
        sb.append(Scheme.SCHEME_II).append(TxtUtils.isEmpty(host, ""));
        sb.append(TxtUtils.isEmpty(path) ? "" : "/" + path);
        if (params != null && params.length > 0) {
            sb.append("?");
            for (int i = 0, c = params.length; i < c; i++) {
                if (i > 0) sb.append("&");
                sb.append(params[i]);
            }
        }
        return sb.toString();
    }

    /**
     * 退出路由
     */
    public void exitRouter() {
        for (IRouterFilter filter : mFilters) {
            filter.exitRouterFilter();
        }
        clearAppFilter();
    }


}
