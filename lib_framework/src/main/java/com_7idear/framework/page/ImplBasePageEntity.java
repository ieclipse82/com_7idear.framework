package com_7idear.framework.page;

import android.content.Context;

import com_7idear.framework.core.BaseData;


/**
 * 基础页面接口
 * @author ieclipse 19-11-29
 * @description
 */
public interface ImplBasePageEntity<D extends BaseData> {

    /**
     * 获取环境对象
     * @return
     */
    Context getContext();

    /**
     * 获取数据中心
     * @return
     */
    D getData();

    /**
     * 页面是否销毁
     * @return
     */
    boolean isPageDestroy();

}
