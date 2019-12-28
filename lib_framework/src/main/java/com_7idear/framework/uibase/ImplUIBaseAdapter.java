package com_7idear.framework.uibase;

import com_7idear.framework.uibase.entity.UIBaseItemEntity;

import java.util.List;


/**
 * UI基础数据适配器接口（基类实现）
 * @author ieclipse 19-12-4
 * @description 需要实现设置UI工厂和设置数据能力
 */
public interface ImplUIBaseAdapter {

    /**
     * 设置UI工厂
     * @param factory UI工厂
     */
    void setUIFactory(ImplUIBaseFactory factory);

    /**
     * 设置数据
     * @param list 数据列表
     * @return
     */
    boolean setData(List<? extends UIBaseItemEntity> list);
}
