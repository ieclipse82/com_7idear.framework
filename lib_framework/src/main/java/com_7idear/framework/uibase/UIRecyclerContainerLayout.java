package com_7idear.framework.uibase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com_7idear.framework.uibase.entity.UIEntity;

import androidx.recyclerview.widget.RecyclerView;

/**
 * UI基础布局（卡片）
 * @author ieclipse 19-11-20
 * @description UI基础适配器中的UI视图，可认为是一个整行的UI
 */
public abstract class UIRecyclerContainerLayout
        extends RecyclerView.ViewHolder
        implements IUIBase, IUIShowOrHide {

    private UIEntity mUIEntity; //UI信息对象

    public UIRecyclerContainerLayout(Context context, ViewGroup parent, int layoutRes) {
        super(LayoutInflater.from(context).inflate(layoutRes, parent, false));

        mUIEntity = new UIEntity(context, layoutRes, itemView);

        initUI();
    }

    /**
     * 获取UI信息对象
     * @return
     */
    final public UIEntity getUIEntity() {
        return mUIEntity;
    }

    /**
     * 查找视图
     * @param id 资源ID
     * @return
     */
    final protected <T extends View> T findViewById(int id) {
        return mUIEntity == null ? null : (T) mUIEntity.getView().findViewById(id);
    }

    @Override
    public int getUILayoutResID() {
        return mUIEntity.getLayoutResID();
    }

    @Override
    public boolean onUIUnBind() {
        return false;
    }

    @Override
    public void onUIAttached() {

    }

    @Override
    public void onUIDetached() {

    }

    @Override
    public void onUIShow(String action) {

    }

    @Override
    public void onUIHide(String action) {

    }

}
