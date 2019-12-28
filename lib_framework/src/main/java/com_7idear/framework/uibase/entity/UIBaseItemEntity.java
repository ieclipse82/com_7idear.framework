package com_7idear.framework.uibase.entity;

import java.io.Serializable;

/**
 * UI基础数据实体类（抽象父类，单元）
 * @author ieclipse 19-11-20
 * @description 最小单元，用于展示唯一单元内容
 */
public abstract class UIBaseItemEntity
        implements Serializable {
    private static final long serialVersionUID = 1L;

    /** ID */
    private long   baseId;
    /** 标签 */
    private String baseLabel;
    /** 布局类型 */
    private int    layoutType;
    /** 显示类型 */
    private int    showType;
    /** 按位比较显示开关 */
    private int    showValue;
    /** 显示百分比 */
    private int    showPercent;


    public long getBaseId() {
        return baseId;
    }

    public void setBaseId(long baseId) {
        this.baseId = baseId;
    }

    public String getBaseLabel() {
        return baseLabel;
    }

    public void setBaseLabel(String baseLabel) {
        this.baseLabel = baseLabel;
    }

    public int getLayoutType() {
        return layoutType;
    }

    public void setLayoutType(int layoutType) {
        this.layoutType = layoutType;
    }

    public int getShowType() {
        return showType;
    }

    public void setShowType(int showType) {
        this.showType = showType;
    }

    public int getShowValue() {
        return showValue;
    }

    public void setShowValue(int showValue) {
        this.showValue = showValue;
    }

    public int getShowPercent() {
        return showPercent;
    }

    public void setShowPercent(int showPercent) {
        this.showPercent = showPercent;
    }
}
