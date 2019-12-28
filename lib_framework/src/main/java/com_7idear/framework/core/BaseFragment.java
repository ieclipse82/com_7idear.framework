package com_7idear.framework.core;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com_7idear.framework.adapter.ImplBasePageChange;
import com_7idear.framework.page.ImplBasePage;
import com_7idear.framework.page.PageEntity;
import com_7idear.framework.page.PageUtils;
import com_7idear.framework.theme.ImplBaseTheme;
import com_7idear.framework.theme.ThemeUtils;
import com_7idear.framework.utils.TxtUtils;

import androidx.fragment.app.Fragment;


/**
 * 基础页面（抽象父类）
 * @author ieclipse 19-9-18
 * @description 实现初始化、动作执行、UI刷新、主题切换等
 */
public abstract class BaseFragment<D extends BaseData>
        extends Fragment
        implements ImplBaseTheme, ImplBaseActivity, ImplBasePage<D>, ImplBasePageChange,
                   IActivity<D> {

    private PageEntity<D> mPageEntity; //页面对象

    private View           vContentView; //缓存视图
    private LayoutInflater mInflater; //布局加载器

    private View   vThemeContentView; //主题视图
    private String mApplyThemePackageName; //应用的主题包名

    private int mPageChanged = PAGE_UNCHANGED; //页面是否改变

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mPageEntity == null) {
            mPageEntity = createPage();
            if (mPageEntity == null) mPageEntity = createBasePage();
            if (mPageEntity == null) throw new RuntimeException("create null page entity!");
            mPageEntity.initPage(getActivity(), this, null);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mPageEntity.endPage();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        if (vContentView == null) {
            mInflater = inflater;
            setContentView(mPageEntity.getLayoutResID());
        }
        mPageEntity.showPage();

        return vContentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        mPageEntity.hidePage();
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
            vContentView = mInflater.inflate(layoutResID, null);
        } else {
            vContentView = vThemeContentView;
        }

        initFindViews();
        initViewsValue();
    }

    @Override
    public View findViewById(int id) {
        return vThemeContentView == null
                ? vContentView.findViewById(id)
                : vThemeContentView.findViewById(ThemeUtils.getInstance().getId(id));
    }

    @Override
    final public boolean runBaseAction(String action, int what, Object obj) {
        if (isPageDestroy() || isDetached()) return false;
        return runAction(action, what, obj);
    }

    @Override
    final public boolean onBaseUIRefresh(String action, int what, Object obj) {
        if (isPageDestroy() || isDetached()) return false;
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

    @Override
    public int getPageChanged() {
        return mPageChanged;
    }

    @Override
    public void setPageChanged() {
        mPageChanged = PAGE_CHANGED;
    }

}
