package com_7idear.framework.uibase;

import android.content.Context;
import android.view.ViewGroup;

import com_7idear.framework.log.TimerFrameUtils;
import com_7idear.framework.uibase.entity.UIBaseItemEntity;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * UI基础适配器
 * @author ieclipse 19-11-20
 * @description 实现UI工厂设置，数据设置，底部UI创建，创建UI，绑定UI，解绑UI等
 */
public class UIRecyclerAdapter
        extends RecyclerView.Adapter
        implements ImplUIRecyclerAdapter {

    private static final String TAG = "UIRecyclerAdapter";

    private static final boolean ISLOG         = true; //是否输出LOG
    private static final int     LAYOUT_FOOTER = -1; //底部UI布局类型

    protected Context                          mContext; //环境对象
    protected ImplUIRecyclerFactory            mFactory; //UI工厂
    protected List<? extends UIBaseItemEntity> mList; //数据列表

    protected ICreateFooterListener mFooterListener; //底部UI创建监听器
    protected boolean               isShowFooter; //是否显示底部UI

    public UIRecyclerAdapter(Context context) {
        mContext = context;
        mList = new ArrayList<UIBaseItemEntity>();
        isShowFooter = false;
        setHasStableIds(true);

    }

    @Override
    public void setUIFactory(ImplUIRecyclerFactory factory) {
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
    public int getItemCount() {
        return isShowFooter ? mList.size() + 1 : mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position < mList.size()) {
            final UIBaseItemEntity tmp = mList.get(position);
            if (tmp != null) return tmp.getLayoutType();
        } else if (isShowFooter) {
            return LAYOUT_FOOTER;
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (position < mList.size()) {
            final UIBaseItemEntity tmp = mList.get(position);
            if (tmp != null) return tmp.hashCode();
        } else if (isShowFooter) {
            return LAYOUT_FOOTER;
        }
        return 0;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TimerFrameUtils.timerFrameRestart(TAG);
        UIRecyclerContainerLayout ui = null;
        if (isShowFooter && LAYOUT_FOOTER == viewType && mFooterListener != null) {
            ui = mFooterListener.onCreateFooterView(mContext, parent);
        } else {
            ui = mFactory.getUIFactoryView(mContext, viewType, parent);
        }
        TimerFrameUtils.timerFrame(TAG);
        return ui;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TimerFrameUtils.timerFrameRestart(TAG);
        if (holder instanceof IUIBase && position < mList.size()) {
            ((IUIBase) holder).onUIBind(IUIBase.ACTION_BIND_VALUE, mList.get(position), position,
                    position == 0, position == mList.size() - 1);
        }
        TimerFrameUtils.timerFrame(TAG);
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof IUIBase) ((IUIBase) holder).onUIUnBind();
    }

    @Override
    public boolean onFailedToRecycleView(@NonNull RecyclerView.ViewHolder holder) {
        onViewRecycled(holder);
        return false;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof IUIBase) ((IUIBase) holder).onUIAttached();
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof IUIBase) ((IUIBase) holder).onUIDetached();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
    }

    /**
     * 底部UI创建接口
     */
    public interface ICreateFooterListener {

        /**
         * 创建底部UI
         * @param context 环境对象
         * @param parent  父布局
         * @return
         */
        UIRecyclerContainerLayout onCreateFooterView(Context context, ViewGroup parent);
    }
}
