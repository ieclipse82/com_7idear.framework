package com_7idear.framework.uibase.entity;

import java.io.Serializable;
import java.util.List;

/**
 * UI基础数据实体类（抽象父类，行）
 * @author ieclipse 19-11-20
 * @description 用于展示行内容
 */
public abstract class UIBaseRowEntity<T extends UIBaseItemEntity>
        extends UIBaseItemEntity
        implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 选中的索引 */
    private int     selected = -1;
    /** 页面模板数据 */
    private List<T> list;

    public int getSelected() {
        return selected;
    }

    public boolean setSelected(int index) {
        if (list != null && index >= 0 && index < size()) {
            this.selected = index;
            return true;
        } else {
            this.selected = -1;
            return false;
        }
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int size() {
        return list == null ? 0 : list.size();
    }

    public T get(int index) {
        return list != null && index >= 0 && index < size() ? list.get(index) : null;
    }
}
