package com_7idear.framework.uibase.entity;

import android.content.Context;
import android.view.View;

import com_7idear.framework.uibase.IUIBaseClick;


/**
 * UI信息实体类（基类初始化数据）
 * @author ieclipse 19-12-6
 * @description
 */
public class UIEntity
        implements IUIBaseClick {

    private Context                  mContext; //环境对象
    private int                      mLayoutResID; //资源布局ID
    private View                     vView; //当前视图
    private View.OnClickListener     eUIClickListener; //UI单击事件监听器
    private View.OnLongClickListener eUILongClickListener; //UI长按事件监听器

    public UIEntity(Context context, int layoutResID, View view) {
        mContext = context;
        mLayoutResID = layoutResID;
        vView = view;
    }

    public Context getContext() {
        return mContext;
    }

    public int getLayoutResID() {
        return mLayoutResID;
    }

    public View getView() {
        return vView;
    }

    @Override
    public View.OnClickListener getUIClickListener() {
        return eUIClickListener;
    }

    @Override
    public void setUIClickListener(View.OnClickListener UIClickListener) {
        eUIClickListener = UIClickListener;
    }

    @Override
    public View.OnLongClickListener getUILongClickListener() {
        return eUILongClickListener;
    }

    @Override
    public void setUILongClickListener(View.OnLongClickListener UILongClickListener) {
        eUILongClickListener = UILongClickListener;
    }
}
