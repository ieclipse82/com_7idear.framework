package com_7idear.framework.adapter;

import android.os.Parcelable;
import android.view.ViewGroup;

import com_7idear.framework.core.BaseFragment;
import com_7idear.framework.log.LogEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

/**
 * 多页面适配器
 * @author ieclipse 19-12-6
 * @description
 */
public class UIFragmentPagerAdapter
        extends FragmentStatePagerAdapter {

    private List<? extends BaseFragment> mViews; //界面列表

    public UIFragmentPagerAdapter(@NonNull FragmentManager fm) {
        super(fm);
        mViews = new ArrayList<>();
    }

    /**
     * 设置视图数据
     * @param views 数组
     */
    public boolean setData(List<? extends BaseFragment> views) {
        if (views == null) return false;
        if (mViews != views) mViews = views;
        notifyDataSetChanged();
        return true;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mViews.get(position);
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Object f = super.instantiateItem(container, position);
        new LogEntity().append("position", position).append("Object", f).toLogD("instantiateItem");
        return f;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        new LogEntity().append("position", position).append("Object", object).toLogD("destroyItem");
        super.destroyItem(container, position, object);
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof ImplBasePageChange) ((ImplBasePageChange) object).getPageChanged();
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mViews.get(position).getPageEntity().getTitle();
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
