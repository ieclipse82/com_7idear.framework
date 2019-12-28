package com_7idear.framework.uibase;

import android.content.Context;
import android.view.ViewGroup;

/**
 * UI基础工厂（抽象父类）
 * @author ieclipse 19-12-4
 * @description 实现UI创建前后的监听器，需要实现onFactoryUICreate方法完成工厂UI的创建
 */
public abstract class UIRecyclerFactory
        implements ImplUIRecyclerFactory {

    private IUICreateListener mUICreateListener;

    protected UIRecyclerFactory() {

    }

    protected UIRecyclerFactory(IUICreateListener listener) {
        mUICreateListener = listener;
    }

    @Override
    public UIRecyclerContainerLayout getUIFactoryView(Context context, int layoutType,
            ViewGroup parent) {
        UIRecyclerContainerLayout ui = null;
        if (mUICreateListener != null) {
            ui = mUICreateListener.onAppUICreate(context, layoutType, parent);
        }
        if (ui == null) {
            ui = onFactoryUICreate(context, layoutType, parent);
            if (ui != null && mUICreateListener != null) {
                mUICreateListener.onFactoryUICreated(ui, layoutType);
            }
        }
        return ui;
    }

    /**
     * 当工厂创建UI
     * @param context    环境对象
     * @param layoutType 布局类型
     * @param parent     父容器
     * @return
     */
    protected abstract UIRecyclerContainerLayout onFactoryUICreate(Context context, int layoutType,
            ViewGroup parent);

    /**
     * UI创建监听器接口
     */
    public interface IUICreateListener {

        /**
         * 当APP创建UI
         * @param context    环境对象
         * @param layoutType 布局类型
         * @param parent     父容器
         * @return
         */
        UIRecyclerContainerLayout onAppUICreate(Context context, int layoutType, ViewGroup parent);

        /**
         * 当工厂UI创建完成
         * @param ui         UI
         * @param layoutType 布局类型
         * @return
         */
        UIRecyclerContainerLayout onFactoryUICreated(UIRecyclerContainerLayout ui, int layoutType);
    }

}
