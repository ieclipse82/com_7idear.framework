package com_7idear.framework.core;

import android.content.Context;
import android.content.Intent;


/**
 * 基础数据中心类（抽象父类）
 * @author ieclipse 19-9-18
 * @description 实现统一的初始化、销毁、UI刷新
 */
public abstract class BaseData {

    private Context          mContext; //环境对象
    private ImplBaseActivity mListener; //监听器

    /**
     * 初始化
     * @param context  环境对象
     * @param listener 监听器
     */
    public void initData(Context context, ImplBaseActivity listener) {
        mContext = context;
        mListener = listener;
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        mContext = null;
        mListener = null;
    }

    /**
     * 获取环境对象
     * @return
     */
    protected Context getContext() {
        return mContext;
    }

    /**
     * 刷新页面
     * @param action 动作标识
     * @param what   索引
     * @param obj    数据
     * @return
     */
    protected boolean onUIRefresh(String action, int what, Object obj) {
        return mListener == null ? false : mListener.onBaseUIRefresh(action, what, obj);
    }

    /**
     * 开始数据
     * @param intent 意图参数
     */
    public abstract void startData(Intent intent);

    /**
     * 结束数据
     */
    public abstract void stopData();

    /**
     * 基础数据接口
     * @param <T>
     */
    public interface IBaseDataListener<T extends BaseData> {

        /**
         * 获取数据中心
         * @return
         */
        public T getData();

        /**
         * 创建数据中心
         * @return
         */
        abstract T createData();
    }
}
