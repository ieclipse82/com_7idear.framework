package com_7idear.framework.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com_7idear.framework.log.LogUtils;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import androidx.core.content.FileProvider;

/**
 * APP工具类
 * @author ieclipse 19-12-10
 * @description
 */
public class AppUtils {

    private static final String TAG                       = "AppUtils";
    private static final String MIMETYPE_APPLICATION      = "application/vnd.android.package-archive";
    private static final String FILE_PROVIDER_AUTHORITIES = "com.miui.localvideoplayer.shareprovider";

    /**
     * 判断APK是否安装
     * @param context     环境对象
     * @param packageName 包名
     * @return
     */
    public static boolean isPackageInstalled(Context context, String packageName) {
        if (TxtUtils.isEmpty(packageName)) return false;
        try {
            ApplicationInfo info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (NameNotFoundException e) {
            LogUtils.e(TAG, "packageName:" + packageName + " not found");
        }
        return false;
    }

    /**
     * 获取APK信息
     * @param context     环境对象
     * @param packageName 包名
     * @return
     */
    public static PackageInfo getPackageInfo(Context context, String packageName) {
        if (TxtUtils.isEmpty(packageName)) return null;
        PackageManager pm = context.getPackageManager();
        if (pm == null) {
            LogUtils.e(TAG, "getPackageInfo: get PackageManager failed");
            return null;
        }
        try {
            return pm.getPackageInfo(packageName, 0);
        } catch (NameNotFoundException e) {
            LogUtils.d(TAG, "getPackageInfo: Package is not installed: " + packageName);
            return null;
        }
    }

    /**
     * 安装APK（首先调用MIUI系统权限安装，失败再次调用ANDROID系统安装）
     * @param context 环境对象
     * @param apkPath APK文件地址
     * @return
     */
    public static void installApk(final Context context, final String apkPath) {
        ExecutorService singleThread = Executors.newSingleThreadExecutor();
        singleThread.execute(new Runnable() {
            @Override
            public void run() {
                installApkByUser(context, apkPath);
            }
        });
    }

    public static void installApkByUser(Context context, String apkPath) {
        try {
            Uri data;
            //ANDROID通用安装APK
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 判断版本大于等于7.0
            if (SDKUtils.equalAPI_24_Nougat()) {
                data = FileProvider.getUriForFile(context, FILE_PROVIDER_AUTHORITIES,
                        new File(apkPath));
                // 给目标应用一个临时授权
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                data = Uri.fromFile(new File(apkPath));
            }
            intent.setDataAndType(data, MIMETYPE_APPLICATION);

            context.startActivity(intent);
        } catch (Exception e) {
            LogUtils.catchException(e);
        }
    }

    /**
     * 获取应用版本名称
     * @param context 环境对象
     * @return
     */
    public static String getAppVersionName(Context context) {
        if (context == null) return "";
        try {
            return context.getPackageManager()
                          .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (NameNotFoundException e) {
            LogUtils.catchException(e);
        }
        return "";
    }

    /**
     * 获取应用版本号
     * @param context 环境对象
     * @return
     */
    public static int getAppVersionCode(Context context) {
        if (context == null) return 0;
        try {
            return context.getPackageManager()
                          .getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (NameNotFoundException e) {
            LogUtils.catchException(e);
        }
        return 0;
    }


    /**
     * 查询指定APP的安装APK路径
     * @param packageName
     * @return
     */
    public static String findInstalledApkPath(Context ctx, String packageName) {
        if (TxtUtils.isEmpty(packageName)) return null;
        try {
            ApplicationInfo info = ctx.getPackageManager().getApplicationInfo(packageName, 0);
            if (info != null) {
                return info.sourceDir;
            }
        } catch (NameNotFoundException e) {
            LogUtils.catchException(e);
        }
        return null;
    }

    /**
     * 销毁带图片的视图
     * @param views 视图数组
     */
    public static void onDestoryViewWithImage(View... views) {
        if (views == null) return;
        for (int i = 0, c = views.length; i < c; i++) {
            onDestoryViewWithImage(views[i]);
        }
    }

    /**
     * 销毁带图片的视图
     * @param view 视图
     */
    @SuppressWarnings("deprecation")
    public static void onDestoryViewWithImage(View view) {
        if (view == null) return;
        if (SDKUtils.equalAPI_16_Jelly_Bean()) {
            view.setBackground(null);
        } else {
            view.setBackgroundDrawable(null);
        }
        if (view instanceof ImageView) {
            ((ImageView) view).setImageDrawable(null);
        }
    }

    /**
     * 是否为全屏（至少传一个参数）
     * @param context 环境对象
     * @param config  配置对象
     * @return
     */
    public static boolean isFullScreen(Context context, Configuration config) {
        if (config == null) {
            if (context == null) {
                return false;
            }
            return Configuration.ORIENTATION_LANDSCAPE == context.getResources()
                                                                 .getConfiguration().orientation;
        } else {
            return Configuration.ORIENTATION_LANDSCAPE == config.orientation;
        }
    }

    /**
     * 应用全屏模式
     * @param activity 界面对象
     * @param isFull   是否全屏
     */
    public static void applyFullScreen(Activity activity, boolean isFull) {
        if (activity == null) return;
        if (isFull) {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            activity.getWindow().setAttributes(attrs);
        } else {
            WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow().setAttributes(attrs);
        }
    }

    /**
     * 获取运行的APP名称
     * @param context 环境对象
     * @param pid     PID
     * @return
     */
    public static String getRunningAppName(Context context, int pid) {
        List<ActivityManager.RunningAppProcessInfo> infos = getRunningApp(context);
        if (infos == null) return "";
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid) {
                return info.processName;
            }
        }
        return "";
    }

    /**
     * 获取运行中的APP PID
     * @param context     环境对象
     * @param packageName 包名
     * @return
     */
    public static int getRunningAppPID(Context context, String packageName) {
        List<ActivityManager.RunningAppProcessInfo> infos = getRunningApp(context);
        if (infos == null) return -1;
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.processName.equalsIgnoreCase(packageName)) {
                return info.pid;
            }
        }
        return -1;
    }

    /**
     * 获取运行中的APP进程数据
     * @param context 环境对象
     * @return
     */
    private static List<ActivityManager.RunningAppProcessInfo> getRunningApp(Context context) {
        if (context == null) return null;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        return am.getRunningAppProcesses();
    }

    public static boolean isActivityForeground(Activity activity) {

        if (activity == null || activity.isFinishing()) {
            return false;
        }
        String activityName = activity.getComponentName().getClassName();
        ActivityManager am = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;
            if (activityName.equals(cpn.getClassName())) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    public static boolean isAppForeground(final Context context) {

        String packageName = context.getPackageName();

        final ActivityManager am = (ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE);
        final List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (tasks != null && !tasks.isEmpty()) {
            final ComponentName topActivity = tasks.get(0).topActivity;
            if (topActivity != null && topActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }

        List<ActivityManager.RunningAppProcessInfo> appProcesses = am.getRunningAppProcesses();
        if (appProcesses != null && appProcesses.size() > 0) {

            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(packageName) && (appProcess.importance
                        == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        //||appProcess.importance == RunningAppProcessInfo.IMPORTANCE_VISIBLE
                )) {
                    return true;
                }
            }
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            long ts = System.currentTimeMillis();
            UsageStatsManager usageStatsManager = (UsageStatsManager) context.getSystemService(
                    Context.USAGE_STATS_SERVICE);
            List<UsageStats> queryUsageStats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_BEST, ts - 5000, ts);

            if (queryUsageStats != null && !queryUsageStats.isEmpty()) {

                UsageStats recentStats = null;
                for (UsageStats usageStats : queryUsageStats) {
                    if (recentStats == null
                            || recentStats.getLastTimeUsed() < usageStats.getLastTimeUsed()) {
                        recentStats = usageStats;
                    }
                }
                boolean isAppForeground = recentStats.getPackageName()
                                                     .equalsIgnoreCase(packageName);
                if (isAppForeground) {
                    return true;
                }
            }
        }

        return false;
    }

    // 判断是否是锁屏页面
    public static boolean isScreenLocked(final Context context) {
        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(
                Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }

    public static boolean isInMultiWindowMode(Activity activity) {
        if (activity != null) {
//            if (SDKUtils.equalAPI_24_Nougat()) {
//                return activity.isInMultiWindowMode();
//            }
        }
        return false;
    }

}
