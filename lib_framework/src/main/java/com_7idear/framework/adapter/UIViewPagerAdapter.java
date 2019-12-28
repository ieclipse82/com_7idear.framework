package com_7idear.framework.adapter;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * 多页面适配器
 * @author ieclipse 19-12-6
 * @description
 */
public class UIViewPagerAdapter
        extends PagerAdapter {

    private List<? extends View> mViews; //界面列表

    public UIViewPagerAdapter() {
        mViews = new ArrayList<>();
    }

    /**
     * 设置视图数据
     * @param views 数组
     * @return
     */
    public boolean setData(List<? extends View> views) {
        if (views == null) return false;
        if (mViews != views) mViews = views;
        notifyDataSetChanged();
        return true;
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        try {
            ((ViewPager) collection).addView(mViews.get(position), 0);
        } catch (Exception e) {
        }
        return mViews.get(position);
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object object) {
        if (getCount() > 1) {
            try {
                ((ViewPager) collection).removeView(mViews.get(position));
            } catch (Exception e) {
            }
        }
    }

    @Override
    public int getItemPosition(Object object) {
        if (object instanceof ImplBasePageChange) ((ImplBasePageChange) object).getPageChanged();
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mViews.get(position).getTag().toString();
    }
}
