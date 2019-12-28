package com_7idear.tv.uibase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com_7idear.framework.uibase.UIRecyclerContainerLayout;


/**
 * UI基础类（行卡片）
 * @author ieclipse 19-11-20
 * @description UI基础适配器中的UI视图，可认为是一个整行的UI
 */
public abstract class UIBaseTVFocusRow
        extends UIRecyclerContainerLayout
        implements ImplUIBaseTVSingleFocus {

    protected final View vFocusView; //全部可获取焦点的视图

    public UIBaseTVFocusRow(Context context, ViewGroup parent, int layoutRes) {
        super(context, parent, layoutRes);

        vFocusView = getUIEntity().getView();
        vFocusView.setFocusable(true);
        if (vFocusView instanceof ViewGroup) {
            ((ViewGroup) vFocusView).setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);
        }
    }

    @Override
    final public View getFocusView() {
        return vFocusView;
    }

}
