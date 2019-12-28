package com_7idear.framework.page;

import android.content.Context;
import android.content.Intent;

import com_7idear.framework.core.BaseData;
import com_7idear.framework.core.ImplBaseActivity;
import com_7idear.framework.core.UIFragment;
import com_7idear.framework.core.UIHandler;
import com_7idear.framework.log.LogEntity;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

/**
 * 页面实体类（基类初始化数据）
 * @author ieclipse 19-11-29
 * @description
 */
public class PageEntity<D extends BaseData>
        implements ImplBasePageEntity<D>, UIHandler.ImplUIHandlerListener,
                   UIFragment.ImplUIFragmentListener {

    private final String host; //页面标识
    private final String title; //页面名称
    private final int    layoutResID; //页面布局资源ID
    private final D      data; //数据中心

    private Context          context; //环境对象
    private ImplBaseActivity listener; //基础界面接口
    private UIHandler        uiHandler; //UI刷新对象
    private UIFragment       uiFragment; //UI界面管理对象
    private boolean          isPageDestroy; //页面是否销毁
    private boolean          isPageShow; //页面是否显示

    /**
     * 创造页面对象
     * @param host        页面标识
     * @param title       页面名称
     * @param layoutResID 页面布局资源ID
     * @param data        数据中心
     */
    public PageEntity(String host, String title, int layoutResID, D data) {
        this.host = host;
        this.title = title;
        this.layoutResID = layoutResID;
        this.data = data;
    }

    /**
     * 初始化页面数据
     * @param context  环境对象
     * @param listener 页面监听器
     */
    public void initPage(Context context, ImplBaseActivity listener, FragmentManager manager) {
        this.context = context;
        this.listener = listener;
        if (data != null) {
            data.initData(this.context, this.listener);
        }
        uiHandler = new UIHandler(this.listener);
        if (manager != null) {
            uiFragment = new UIFragment(manager);
        }
        PageUtils.getInstance().addUI(this.listener);
    }

    /**
     * 页面启动
     * @param intent 意图
     */
    public void startPage(Intent intent) {
        isPageDestroy = false;
        if (data != null) data.startData(intent);
    }

    /**
     * 页面结束
     */
    public void endPage() {
        isPageDestroy = true;
        if (data != null) data.onDestroy();
        uiHandler.onDestroy();
        if (uiFragment != null) uiFragment.onDestroy();
        PageUtils.getInstance().removeUI(listener);
    }

    public void showPage() {
        isPageShow = true;
    }

    public void hidePage() {
        isPageShow = false;
    }


    public String getHost() {
        return host;
    }

    public String getTitle() {
        return title;
    }

    public int getLayoutResID() {
        return layoutResID;
    }

    public boolean isPageShow() {
        return isPageShow;
    }


    @Override
    public UIFragment getUIFragment() {
        return uiFragment;
    }

    @Override
    public UIHandler getUIHandler() {
        return uiHandler;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public D getData() {
        return data;
    }

    @Override
    public boolean isPageDestroy() {
        return isPageDestroy;
    }

    @NonNull
    @Override
    public String toString() {
        return new LogEntity().appendLine(super.toString())
                              .append("host", host)
                              .append("title", title)
                              .append("layout", context.getResources().getResourceName(layoutResID))
                              .append("isPageDestroy", isPageDestroy)
                              .appendLine("isPageShow", isPageShow)
                              .appendLine("context", context)
                              .appendLine("listener", listener)
                              .appendLine("data", data)
                              .appendLine("uiHandler", uiHandler)
                              .appendLine("uiFragment", uiFragment)
                              .toString();
    }
}
