package com_7idear.framework.router;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * APP筛选器接口
 * @author ieclipse 19-12-2
 * @description
 */
public interface IRouterFilter {

    /**
     * 获取意图
     * @param context 环境对象
     * @param intent  意图
     * @param entity  路由对象
     * @param bundle  附带参数
     * @return
     */
    Intent getIntentWithRouterEntity(Context context, Intent intent, RouterEntity entity,
            Bundle bundle);

    /**
     * 退出路由筛选器
     */
    void exitRouterFilter();
}
