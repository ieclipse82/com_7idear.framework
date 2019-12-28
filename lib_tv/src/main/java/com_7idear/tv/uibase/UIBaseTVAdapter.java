package com_7idear.tv.uibase;

import android.content.Context;
import android.view.View;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com_7idear.framework.uibase.UIRecyclerAdapter;
import com_7idear.framework.utils.EntityUtils;

/**
 * UI基础适配器（TV）
 * @author ieclipse 19-11-20
 * @description 实现UI工厂设置，数据设置，底部UI创建，创建UI，绑定UI，解绑UI等
 */
public class UIBaseTVAdapter
        extends UIRecyclerAdapter {

    public UIBaseTVAdapter(Context context) {
        super(context);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof ImplUIBaseTVSingleFocus) {
            View view = ((ImplUIBaseTVSingleFocus) holder).getFocusView();
            view.setOnFocusChangeListener(
                    UIFocusUtils.getInstance(mContext).getOnFocusChangeListener());
        } else if (holder instanceof ImplUIBaseTVAllFocus) {
            final List<View> list = ((ImplUIBaseTVAllFocus) holder).getFocusViews();
            if (EntityUtils.isEmpty(list)) return;
            final View.OnFocusChangeListener eFocusChange = UIFocusUtils.getInstance(mContext)
                                                                        .getOnFocusChangeListener();
            for (View view : list) {
                view.setOnFocusChangeListener(eFocusChange);
            }
        }
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof ImplUIBaseTVSingleFocus) {
            ((ImplUIBaseTVSingleFocus) holder).getFocusView().setOnFocusChangeListener(null);
        } else if (holder instanceof ImplUIBaseTVAllFocus) {
            final List<View> list = ((ImplUIBaseTVAllFocus) holder).getFocusViews();
            if (EntityUtils.isEmpty(list)) return;
            for (View view : list) {
                view.setOnFocusChangeListener(null);
            }
        }
    }

}
