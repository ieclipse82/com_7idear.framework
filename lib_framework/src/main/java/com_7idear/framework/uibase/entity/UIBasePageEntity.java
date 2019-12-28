package com_7idear.framework.uibase.entity;

import java.io.Serializable;
import java.util.List;

/**
 * UI基础数据实体类（抽象父类，页面）
 * @author ieclipse 19-11-20
 * @description 用于展示列表或页面内容
 */
public abstract class UIBasePageEntity<T extends UIBaseRowEntity>
        extends UIBaseRowEntity
        implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 数据索引 */
    protected long    index;
    /** 页面页数 */
    protected int     page;
    /** 是否数据结尾 */
    protected boolean isEndPage;
    /** 页面模板数据 */
    protected List<T> list;


    public long getIndex() {
        return index;
    }

    public void setIndex(long index) {
        this.index = index;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public boolean isEndPage() {
        return isEndPage;
    }

    public void setEndPage(boolean endPage) {
        isEndPage = endPage;
    }
}
