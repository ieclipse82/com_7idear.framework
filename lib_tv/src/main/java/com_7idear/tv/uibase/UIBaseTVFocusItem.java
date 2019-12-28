package com_7idear.tv.uibase;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;


/**
 * UI基础类（单元卡片）
 * @author ieclipse 19-11-21
 * @description 可获取焦点煌视图，可认为是最小的展示单元，与行卡片是多对一的关系 TODO 待实现细节
 */
public abstract class UIBaseTVFocusItem
        extends FrameLayout {

    public UIBaseTVFocusItem(Context context) {
        this(context, null, 0);
    }

    public UIBaseTVFocusItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UIBaseTVFocusItem(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFocusable(true);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);

        init();
    }

    protected abstract void init();

    protected abstract View getFocusFarView();

    protected abstract View getFocusMiddleView();

    protected abstract View getFocusNearView();

}
