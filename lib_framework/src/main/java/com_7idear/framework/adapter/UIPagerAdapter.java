package com_7idear.framework.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com_7idear.framework.uibase.IUIBase;
import com_7idear.framework.uibase.ImplUIBaseFactory;
import com_7idear.framework.uibase.entity.UIBaseItemEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

/**
 * @author ieclipse 19-12-6
 * @description
 */
public class UIPagerAdapter
        extends PagerAdapter {

    private Context                          mContext; //环境对象
    private ImplUIBaseFactory                mFactory; //UI工厂
    private List<? extends UIBaseItemEntity> mList; //数据列表

    public UIPagerAdapter(Context context, ImplUIBaseFactory factory) {
        mContext = context;
        mFactory = factory;
        mList = new ArrayList<UIBaseItemEntity>();
    }

    /**
     * 设置UI工厂
     * @param factory UI工厂
     */
    public void setUIFactory(ImplUIBaseFactory factory) {
        mFactory = factory;
    }

    /**
     * 设置数据
     * @param list 数据列表
     * @return
     */
    public boolean setData(List<? extends UIBaseItemEntity> list) {
        if (list == null) return false;
        if (mList != list) mList = list;
        notifyDataSetChanged();
        return true;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == (object);
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        if (mFactory == null) return null;
        UIBaseItemEntity entity = mList.get(position);
        final int type = entity.getLayoutType();
        final boolean isFirst = position == 0;
        final boolean isLast = position == mList.size() - 1;
        View ui = mFactory.getUIFactoryView(mContext, type, collection);
        if (ui instanceof IUIBase)
            ((IUIBase) ui).onUIBind(IUIBase.ACTION_BIND_VALUE, entity, position, isFirst, isLast);
        try {
            collection.addView(ui);
        } catch (Exception e) {
        }
        return ui;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object object) {
        if (getCount() > 1) {
            try {
                ((ViewPager) collection).removeView((View) object);
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
        return mList.get(position).getBaseLabel();
    }
}
