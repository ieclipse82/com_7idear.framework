package com_7idear.framework.core;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com_7idear.framework.page.IPageFactory;
import com_7idear.framework.page.PageEntity;
import com_7idear.framework.router.IRouterFilter;
import com_7idear.framework.router.RouterEntity;
import com_7idear.framework.utils.TxtUtils;


/**
 * 基础页面路由筛选类（抽象父类，可继承实现）
 * @author ieclipse 19-12-2
 * @description 统一创建页面对象需要实现createPageEntity方法
 */
public abstract class BaseAppPageRouterFilter
        implements IRouterFilter, IPageFactory {

    @Override
    public Intent getIntentWithRouterEntity(Context context, Intent intent, RouterEntity entity,
            Bundle bundle) {
        return getIntentWithRouterEntity(context, intent, entity.getHost(), entity, bundle);
    }

    @Override
    public void exitRouterFilter() {

    }

    @Override
    public PageEntity createPageEntity(Class cla) {
        return null;
    }

    /**
     * 检查HOST是否相等
     * @param host1 标识1
     * @param host2 标识2
     * @return
     */
    protected boolean checkHostEquals(String host1, String host2) {
        return TxtUtils.isEmptyOR(host1, host2) ? false : host1.equalsIgnoreCase(host2);
    }

    /**
     * 获取真实意图
     * @param context 环境对象
     * @param intent  意图
     * @param host    标识
     * @param entity  路由对象
     * @param bundle  附带参数
     * @return
     */
    protected abstract Intent getIntentWithRouterEntity(Context context, Intent intent, String host,
            RouterEntity entity, Bundle bundle);

}
