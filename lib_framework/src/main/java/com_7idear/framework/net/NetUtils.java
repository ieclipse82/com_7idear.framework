package com_7idear.framework.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com_7idear.framework.log.LogEntity;
import com_7idear.framework.log.LogUtils;

import java.util.ArrayList;


/**
 * 网络状态工具类
 * @author ieclipse 19-8-15
 * @description
 */
public class NetUtils {
    private static final String TAG = "NetUtils";

    /** 网络类型——未检测网络 */
    private static final String TYPE_NULL       = "NULL";
    /** 网络类型——未知网络 */
    private static final String TYPE_UNKNOWN    = "UNKNOWN";
    /** 网络类型——MOBILE */
    private static final String TYPE_MOBILE     = "MOBILE";
    /** 网络类型——MOBILE_NET */
    private static final String TYPE_MOBILE_NET = "NET";
    /** 网络类型——MOBILE_WAP */
    private static final String TYPE_MOBILE_WAP = "WAP";
    /** 网络类型——WIFI */
    private static final String TYPE_WIFI       = "WIFI";

    /** 当前联网类型 */
    private static volatile String mNetworkType = TYPE_NULL;

    private static final NetworkReceiver             mReceiver  = new NetworkReceiver();
    private static final ArrayList<INetworkListener> mListeners = new ArrayList<>();

    /**
     * 检查网络状态（当网络已经连接，并且网络状态改变时返回True)
     * @param context 环境对象
     * @return true：网络改变并已经连接，false：网络未改变或已经断开连接
     */
    public static synchronized boolean checkNetworkState(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo == null) {
            mNetworkType = TYPE_UNKNOWN;
            return false;
        } else if (networkInfo.isAvailable()) {
            if (TYPE_MOBILE.equalsIgnoreCase(networkInfo.getTypeName())) {
                NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                if (mobileInfo == mobileInfo || mobileInfo.getExtraInfo() == null) {
                    mNetworkType = TYPE_MOBILE_NET;
                    return true;
                }
                String type = mobileInfo.getExtraInfo().toUpperCase();
                if (TYPE_MOBILE_NET.equals(mNetworkType)) {
                    if (type.indexOf(TYPE_MOBILE_WAP) != -1) {
                        mNetworkType = TYPE_MOBILE_WAP;
                        return true;
                    }
                } else if (TYPE_MOBILE_WAP.equals(mNetworkType)) {
                    if (type.indexOf(TYPE_MOBILE_NET) != -1) {
                        mNetworkType = TYPE_MOBILE_NET;
                        return true;
                    }
                } else {
                    if (type.indexOf(TYPE_MOBILE_NET) != -1) {
                        mNetworkType = TYPE_MOBILE_NET;
                    } else if (type.indexOf(TYPE_MOBILE_WAP) != -1) {
                        mNetworkType = TYPE_MOBILE_WAP;
                    }
                    return true;
                }
            } else if (TYPE_WIFI.equalsIgnoreCase(networkInfo.getTypeName())) {
                if (!TYPE_WIFI.equals(mNetworkType)) {
                    mNetworkType = TYPE_WIFI;
                    return true;
                }

            }
            return false;
        }

        mNetworkType = TYPE_UNKNOWN;
        return false;
    }

    /**
     * 判断网络是否连接
     * @param context 环境对象
     * @return
     */
    public static synchronized boolean isNetworkConnected(Context context) {
        if (TYPE_NULL.equals(mNetworkType) || TYPE_UNKNOWN.equals(mNetworkType)) {
            checkNetworkState(context);
        }
        return !TYPE_UNKNOWN.equals(mNetworkType);
    }

    /**
     * 判断网络是否连接（免费网络）
     * @param context 环境对象
     * @return
     */
    public static synchronized boolean isFreeNetworkConnected(Context context) {
        return isNetworkConnected(context) && ((ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE)).isActiveNetworkMetered();
    }

    /**
     * 判断是否为WAP连接
     * @return true：WAP连接，false：不是WAP连接
     */
    public static boolean isWapNetwork() {
        return TYPE_MOBILE_WAP.equals(mNetworkType);
    }

    public static String getNetworkType() {
        return mNetworkType;
    }

    /**
     * 获取整数转IP地址
     * @param ipAddress 整数
     * @return
     */
    private static String formatIntToIp(int ipAddress) {
        return (ipAddress & 0xFF) + "." + ((ipAddress >> 8) & 0xFF) + "." + ((ipAddress >> 16)
                & 0xFF) + "." + (ipAddress >> 24 & 0xFF);
    }

    /**
     * 获取IP地址转整数
     * @param ipAddress IP地址
     * @return
     */
    public static int formatIpToInt(String ipAddress) {
        try {
            int[] ip = new int[4];
            // 先找到IP地址字符串中.的位置
            int position1 = ipAddress.indexOf(".");
            int position2 = ipAddress.indexOf(".", position1 + 1);
            int position3 = ipAddress.indexOf(".", position2 + 1);
            // 将每个.之间的字符串转换成整型
            ip[0] = Integer.parseInt(ipAddress.substring(0, position1));
            ip[1] = Integer.parseInt(ipAddress.substring(position1 + 1, position2));
            ip[2] = Integer.parseInt(ipAddress.substring(position2 + 1, position3));
            ip[3] = Integer.parseInt(ipAddress.substring(position3 + 1));
            return (ip[3] << 24) + (ip[2] << 16) + (ip[1] << 8) + ip[0];
        } catch (Exception e) {
            LogUtils.catchException(e);
        }
        return 0;
    }

    public static boolean registerReceiver(Context context) {
        if (context == null) return false;
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(mReceiver, filter);
        return true;
    }

    public static boolean unregisterReceiver(Context context) {
        if (context == null || mReceiver == null) return false;
        context.unregisterReceiver(mReceiver);
        return true;
    }

    public static void addNetworkStateListener(INetworkListener l) {
        mListeners.add(l);
    }

    public static void removeNetworkStateListener(INetworkListener l) {
        mListeners.remove(l);
    }

    public static void clearNetworkStateListener() {
        mListeners.clear();
    }

    private static class NetworkReceiver
            extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            new LogEntity().append("action", action).toLogD("onReceive");
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                if (checkNetworkState(context)) {
                    final String type = mNetworkType;
                    //禁止耗时操作
                    for (INetworkListener l : mListeners) {
                        l.onConnected(type);
                    }
                } else {
                    //禁止耗时操作
                    for (INetworkListener l : mListeners) {
                        l.onDisconnect();
                    }
                }
            }
        }
    }

    /**
     * 网络状态监听器
     */
    public interface INetworkListener {
        /**
         * 网络连接
         * @param netType 类型
         */
        void onConnected(String netType);

        /**
         * 网络断开
         */
        void onDisconnect();
    }
}
