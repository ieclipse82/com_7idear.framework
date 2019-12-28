package com_7idear.framework.uibase;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com_7idear.framework.uibase.entity.UIBaseItemEntity;

import java.util.ArrayList;
import java.util.List;


/**
 * @author ieclipse 19-12-10
 * @description
 */
public class UIBaseAdapter
        extends BaseAdapter
        implements ImplUIBaseAdapter {

    private static final String TAG = "UIBaseAdapter";

    private static final boolean ISLOG         = true; //是否输出LOG
    private static final int     LAYOUT_FOOTER = -1; //底部UI布局类型

    protected Context                          mContext; //环境对象
    protected ImplUIBaseFactory                mFactory; //UI工厂
    protected List<? extends UIBaseItemEntity> mList; //数据列表

    //    protected ICreateFooterListener mFooterListener; //底部UI创建监听器
    protected boolean isShowFooter; //是否显示底部UI

    public UIBaseAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<UIBaseItemEntity>();
        isShowFooter = false;

    }

    @Override
    public void setUIFactory(ImplUIBaseFactory factory) {
        mFactory = factory;
    }

    @Override
    public boolean setData(List<? extends UIBaseItemEntity> list) {
        if (list == null || mList == list) return false;
        mList = list;
        notifyDataSetChanged();
        return true;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position < mList.size() ? mList.get(position).getLayoutType() : 0;
    }

    @Override
    public int getViewTypeCount() {
        return mFactory.getUIViewTypeCount();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        UIBaseItemEntity entity = mList.get(position);
        final boolean isFirst = position == 0;
        final boolean isLast = position == mList.size() - 1;
        if (convertView == null) {
            convertView = mFactory.getUIFactoryView(mContext, entity.getLayoutType(), parent);
        }

        if (convertView != null && convertView instanceof IUIBase) {
            ((IUIBase) convertView).onUIBind(IUIBase.ACTION_BIND_VALUE, entity, position, isFirst,
                    isLast);
        }
        return convertView;
    }


}
