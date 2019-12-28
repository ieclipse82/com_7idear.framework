package com_7idear.framework.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.SparseArray;

import com_7idear.framework.log.LogEntity;
import com_7idear.framework.net.NetUtils;

import java.util.LinkedList;
import java.util.List;


/**
 * APP全局广播接收器工具类
 * @author ieclipse 19-8-15
 * @description
 */
public class AppReceiverUtils
        extends BroadcastReceiver {

    public static final IntentFilter FILTER_CONNECTIVITY_CHANGE = new IntentFilter(
            ConnectivityManager.CONNECTIVITY_ACTION);

    private static volatile AppReceiverUtils mInstance;

    private Context mAppContext; //APP全局环境对象

    AppReceiverUtils() {
        mAppContext = BaseApplication.getAppContext();
    }

    public static AppReceiverUtils getInstance() {
        if (mInstance == null) {
            synchronized (AppReceiverUtils.class) {
                if (mInstance == null) mInstance = new AppReceiverUtils();
            }
        }
        return mInstance;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        new LogEntity().append("action", action).toLogD();
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
            boolean changed = NetUtils.checkNetworkState(context);
            new LogEntity().append("getNetworkType", NetUtils.getNetworkType()).toLogD();
        }
    }

    //TODO 完善全局广播接收和分发
    /** 对话框列表 */
    private static SparseArray<List<BroadcastReceiver>> mArray = new SparseArray<List<BroadcastReceiver>>();

    public synchronized boolean registerReceiver(Context context, BroadcastReceiver receiver,
            IntentFilter filter) {
        if (context == null || receiver == null || filter == null) return false;
        final int code = context.hashCode();
        List<BroadcastReceiver> list = mArray.get(code);
        if (list == null) list = new LinkedList<>();
        if (list.contains(receiver)) {
            unregisterReceiver(context, receiver);
        }
        mAppContext.registerReceiver(receiver, filter);
        list.add(receiver);
        mArray.put(code, list);
        return true;
    }

    public synchronized void unregisterReceiver(Context context) {
        unregisterReceiver(context, null);
    }

    public synchronized boolean unregisterReceiver(Context context, BroadcastReceiver receiver) {
        if (context == null) return false;
        final int code = context.hashCode();
        List<BroadcastReceiver> list = mArray.get(code);
        if (list == null) return false;
        synchronized (list) {
            if (receiver == null) {
                for (BroadcastReceiver tmp : list) {
                    mAppContext.unregisterReceiver(tmp);
                }
                mArray.remove(code);
                return true;
            } else {
                mAppContext.unregisterReceiver(receiver);
                return list.remove(receiver);
            }
        }
    }

    public synchronized void sendBroadcast(Intent intent) {
        mAppContext.sendBroadcast(intent);
    }
}
