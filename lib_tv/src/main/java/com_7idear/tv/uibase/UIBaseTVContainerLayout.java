package com_7idear.tv.uibase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com_7idear.framework.uibase.UIRecyclerContainerLayout;


/**
 * UI基础类（行卡片）
 * @author ieclipse 19-11-20
 * @description UI基础适配器中的UI视图，可认为是一个整行的UI
 */
public abstract class UIBaseTVContainerLayout
        extends UIRecyclerContainerLayout
        implements ImplUIBaseTVAllFocus {

    protected final List<View> vFocusViews; //全部可获取焦点的视图

    public UIBaseTVContainerLayout(Context context, ViewGroup parent, int layoutRes) {
        super(context, parent, layoutRes);

        vFocusViews = getUIEntity().getView().getFocusables(View.FOCUS_DOWN);

    }

    @Override
    final public List<View> getFocusViews() {
        return vFocusViews;
    }

    /**
     * 设置自己为单一视图（可获取焦点）
     */
    final protected void setSelfIsSingleView() {
        if (getUIEntity().getView() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) getUIEntity().getView();
            viewGroup.setFocusable(true);
            viewGroup.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        }
    }
}
