package com_7idear.framework.uibase;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com_7idear.framework.uibase.entity.UIEntity;


/**
 * UI基础布局
 * @author ieclipse 19-11-29
 * @description
 */
public abstract class UIBaseLinearLayout
        extends LinearLayout
        implements IUIBase, IUIShowOrHide {

    private UIEntity mUIEntity; //UI信息对象

    public UIBaseLinearLayout(Context context) {
        this(context, null, 0, null);
    }

    public UIBaseLinearLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0, null);
    }

    public UIBaseLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs, defStyle, null);
    }

    public UIBaseLinearLayout(Context context, AttributeSet attrs, int defStyle, ViewGroup parent) {
        super(context, attrs, defStyle);

        mUIEntity = new UIEntity(context, getUILayoutResID(),
                inflate(getContext(), getUILayoutResID(), this));

        initUI();
    }

    /**
     * 获取UI信息对象
     * @return
     */
    final public UIEntity getUIEntity() {
        return mUIEntity;
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        onUIAttached();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        onUIDetached();
    }

    @Override
    protected void removeDetachedView(View child, boolean animate) {
        super.removeDetachedView(child, animate);
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
