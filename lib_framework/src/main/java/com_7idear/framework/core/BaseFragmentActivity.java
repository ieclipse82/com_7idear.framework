package com_7idear.framework.core;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com_7idear.framework.page.ImplBasePage;
import com_7idear.framework.page.PageEntity;
import com_7idear.framework.page.PageUtils;
import com_7idear.framework.theme.ImplBaseTheme;
import com_7idear.framework.theme.ThemeUtils;
import com_7idear.framework.utils.TxtUtils;

import androidx.fragment.app.FragmentActivity;


/**
 * 基础页面（抽象父类）
 * @author ieclipse 19-9-18
 * @description 实现初始化、动作执行、UI刷新、主题切换等
 */
public abstract class BaseFragmentActivity<D extends BaseData>
        extends FragmentActivity
        implements ImplBaseTheme, ImplBaseActivity, ImplBasePage<D>, IActivity<D> {

    private PageEntity<D> mPageEntity; //页面对象

    private View   vThemeContentView; //主题视图
    private String mApplyThemePackageName; //应用的主题包名

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mPageEntity == null) {
            mPageEntity = createPage();
            if (mPageEntity == null) mPageEntity = createBasePage();
            if (mPageEntity == null) throw new RuntimeException("create null page entity!");
            mPageEntity.initPage(this, this, getSupportFragmentManager());
        }
        mPageEntity.startPage(getIntent());

        setContentView(mPageEntity.getLayoutResID());

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mPageEntity.endPage();

    }

    @Override
    public void setContentView(int layoutResID) {
        if (!TxtUtils.isEmpty(mApplyThemePackageName) && TxtUtils.equals(mApplyThemePackageName,
                ThemeUtils.getInstance().getThemePackageName())) {
            return;
        }
        mApplyThemePackageName = ThemeUtils.getInstance().getThemePackageName();
        vThemeContentView = ThemeUtils.getInstance().getLayout(layoutResID);
        if (vThemeContentView == null) {
            super.setContentView(layoutResID);
        } else {
            super.setContentView(vThemeContentView);
        }

        initFindViews();
        initViewsValue();
    }

    @Override
    public View findViewById(int id) {
        return vThemeContentView == null
                ? super.findViewById(id)
                : vThemeContentView.findViewById(ThemeUtils.getInstance().getId(id));
    }

    @Override
    final public boolean runBaseAction(String action, int what, Object obj) {
        if (isPageDestroy() || isDestroyed()) return false;
        return runAction(action, what, obj);
    }

    @Override
    final public boolean onBaseUIRefresh(String action, int what, Object obj) {
        if (isPageDestroy() || isDestroyed()) return false;
        return onUIRefresh(action, what, obj);
    }

    @Override
    public boolean onBaseThemeChanged(String themePackageName) {
        setContentView(mPageEntity.getLayoutResID());
        return true;
    }

    @Override
    final public PageEntity<D> createBasePage() {
        return PageUtils.getInstance().createPageEntity(this.getClass());
    }

    @Override
    final public PageEntity<D> getPageEntity() {
        return mPageEntity;
    }

    @Override
    final public UIFragment getUIFragment() {
        return mPageEntity.getUIFragment();
    }

    @Override
    final public UIHandler getUIHandler() {
        return mPageEntity.getUIHandler();
    }

    @Override
    final public Context getContext() {
        return mPageEntity.getContext();
    }

    @Override
    final public D getData() {
        return mPageEntity.getData();
    }

    @Override
    final public boolean isPageDestroy() {
        return mPageEntity.isPageDestroy();
    }

}
