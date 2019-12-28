package com_7idear.framework.utils;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;

import com_7idear.framework.application.BaseApplication;
import com_7idear.framework.config.CacheConfig;

import java.io.File;


/**
 * 缓存工具类
 * @author iEclipse 2019/7/11
 * @description
 */
public class CacheUtils
        extends CacheConfig {

    /**
     * 最小SD空间
     */
    private static final long LOW_SD = 1 << 22;

    /** 应用目录级别——0：数据放到SDCARD */
    public static final int LEVEL_SDCARD         = 0;
    /** 应用目录级别——1：数据放到SDCARD下的Android目录 */
    public static final int LEVEL_SDCARD_ANDROID = 1;
    /** 应用目录级别——2：数据放到手机Data目录 */
    public static final int LEVEL_PHONE_DATA     = 2;

    private static CacheUtils mInstance;

    private static Context mAppContext;
    private static boolean isLog;
    private static String  mAppDir;

    @Override
    protected boolean init(Context appContext, String appDir) {
        mInstance.mAppContext = appContext;
        mInstance.isLog = isLog();
        if (isExistsSdcard()) {
            mInstance.mAppDir = appDir;
        } else {
            mInstance.mAppDir = null;
        }
        return true;
    }

    public static CacheUtils getInstance() {
        if (mInstance == null) {
            synchronized (CacheUtils.class) {
                if (mInstance == null) mInstance = new CacheUtils();
            }
        }
        return mInstance;
    }


    /**
     * 是否存在SDCARD
     * @return
     */
    public static boolean isExistsSdcard() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /**
     * 获取最适合的缓存目录
     * @param level 级别（SDCard > SDCard下的Android目录 > 手机内置Data目录）
     * @return
     */
    public static String getSuitableCachePath(Context context, int level) {
        switch (level) {
            case LEVEL_SDCARD:
                if (isExistsSdcard()) {
                    File file = Environment.getExternalStorageDirectory();
                    if (file != null) return TxtUtils.toStr(file);
                }
            case LEVEL_SDCARD_ANDROID:
                if (isExistsSdcard()) {
                    File file = context.getExternalFilesDir(null);
                    if (file != null) return TxtUtils.toStr(file);
                }
            default:
                return TxtUtils.toStr(context.getFilesDir());
        }
    }

    /**
     * 获取文件目录（有SDCARD：返回SDCARD应用文件目录。无SDCARD：返回DATA应用文件目录）
     * @param filePath 文件目录
     * @return
     */
    public static String getFilePath(String filePath) {
        return getFilePathFile(filePath).toString();
    }

    /**
     * 获取文件目录文件（有SDCARD：返回SDCARD应用文件目录文件。无SDCARD：返回DATA应用文件目录文件）
     * @param filePath 文件目录
     * @return
     */
    public static File getFilePathFile(String filePath) {
        File file = null;
        if (isExistsSdcard()) {
            file = BaseApplication.getAppContext().getExternalFilesDir(filePath);
        } else {
            if (TxtUtils.isEmpty(filePath)) {
                file = BaseApplication.getAppContext().getFilesDir();
            } else {
                file = new File(BaseApplication.getAppContext().getFilesDir().toString()
                        + File.separator
                        + filePath);
            }
        }

        slog(isLog).append("getFilePathFile", TxtUtils.toStr(file.toString())).toLogD();
        return file;
    }

    /**
     * 获取缓存目录（有SDCARD：返回SDCARD应用缓存目录。无SDCARD：返回DATA应用缓存目录）
     * @return
     */
    public static String getCachePath() {
        return getCachePathFile().toString();
    }

    /**
     * 获取缓存目录文件（有SDCARD：返回SDCARD应用缓存目录文件。无SDCARD：返回DATA应用缓存目录文件）
     * @return
     */
    public static File getCachePathFile() {
        File file = null;
        if (isExistsSdcard()) {
            file = BaseApplication.getAppContext().getExternalCacheDir();
        } else {
            file = BaseApplication.getAppContext().getCacheDir();
        }

        slog(isLog).append("getCachePathFile", TxtUtils.toStr(file.toString())).toLogD();
        return file;
    }

    /**
     * 获取SDCard目录（有SDCARD：返回SDCARD应用目录。无SDCARD：返回DATA应用文件目录）
     * @param filePath 文件目录
     * @return
     */
    public static String getSDCardPath(String filePath) {
        return getSDCardPathFile(filePath).toString();
    }

    /**
     * 获取SDCard目录文件（有SDCARD：返回SDCARD应用目录文件。无SDCARD：返回DATA应用文件目录文件）
     * @param filePath 文件目录
     * @return
     */
    public static File getSDCardPathFile(String filePath) {
        File file = null;
        if (isExistsSdcard()) {
            if (TxtUtils.isEmpty(filePath)) {
                file = Environment.getExternalStorageDirectory();
            } else {
                file = new File(
                        Environment.getExternalStorageDirectory() + File.separator + filePath);
            }
        } else {
            if (TxtUtils.isEmpty(filePath)) {
                file = BaseApplication.getAppContext().getFilesDir();
            } else {
                file = new File(BaseApplication.getAppContext().getFilesDir().toString()
                        + File.separator
                        + filePath);
            }
        }

        slog(isLog).append("getSDCardPathFile", TxtUtils.toStr(file.toString())).toLogD();
        return file;
    }

    /**
     * 获取应用目录（有SDCARD：返回SDCARD应用目录。无SDCARD：返回DATA应用文件目录）
     * @param filePath
     * @return
     */
    public static String getAppPath(String filePath) {
        return getAppPathFile(filePath).toString();
    }

    /**
     * 获取应用目录文件（有SDCARD：返回SDCARD应用目录文件。无SDCARD：返回DATA应用文件目录文件）
     * @param filePath 文件目录
     * @return
     */
    public static File getAppPathFile(String filePath) {
        File file = null;
        if (isExistsSdcard()) {
            if (!TxtUtils.isEmpty(mAppDir)) {
                if (TxtUtils.isEmpty(filePath)) {
                    file = new File(mAppDir);
                } else {
                    file = new File(mAppDir + File.separator + filePath);
                }
            } else {
                file = BaseApplication.getAppContext().getExternalFilesDir(filePath);
            }
        } else {
            if (TxtUtils.isEmpty(filePath)) {
                file = BaseApplication.getAppContext().getFilesDir();
            } else {
                file = new File(BaseApplication.getAppContext().getFilesDir().toString()
                        + File.separator
                        + filePath);
            }
        }

        slog(isLog).append("getAppPathFile", TxtUtils.toStr(file.toString())).toLogD();
        return file;
    }


    private static final String System = "System"; //系统区
    private static final String Data   = "Data"; //数据区
    private static final String SDCard = "SDCard"; //SD卡

    private static final String TotalSpace     = "TotalSpace"; //空间总大小
    private static final String FreeSpace      = "FreeSpace"; //空闲空间大小
    private static final String AvailableSpace = "AvailableSpace"; //可用空间大小

    /**
     * 获取系统区空间总大小
     * @return
     */
    public static long getSystemTotalSpace() {
        return getSpace(System, TotalSpace);
    }

    /**
     * 获取系统区空闲空间大小
     * @return
     */
    public static long getSystemFreeSpace() {
        return getSpace(System, FreeSpace);
    }

    /**
     * 获取系统区当前可用空间大小
     * @return
     */
    public static long getSystemAvailableSpace() {
        return getSpace(System, AvailableSpace);
    }

    /**
     * 获取数据区空间总大小
     * @return
     */
    public static long getDataTotalSpace() {
        return getSpace(Data, TotalSpace);
    }

    /**
     * 获取数据区空闲空间大小
     * @return
     */
    public static long getDataFreeSpace() {
        return getSpace(Data, FreeSpace);
    }

    /**
     * 获取数据区当前可用空间大小
     * @return
     */
    public static long getDataAvailableSpace() {
        return getSpace(Data, AvailableSpace);
    }

    /**
     * 获取SD卡空间总大小
     * @return
     */
    public static long getSDCardTotalSpace() {
        return isExistsSdcard() ? getSpace(SDCard, TotalSpace) : 0;
    }

    /**
     * 获取SD卡空闲空间大小
     * @return
     */
    public static long getSDCardFreeSpace() {
        return isExistsSdcard() ? getSpace(SDCard, FreeSpace) : 0;
    }

    /**
     * 获取SD卡当前可用空间大小
     * @return
     */
    public static long getSDCardAvailableSpace() {
        return isExistsSdcard() ? getSpace(SDCard, AvailableSpace) : 0;
    }

    /**
     * 获取空间大小
     * @param path 目录
     * @param type 类型
     * @return
     */
    private static long getSpace(String path, String type) {
        StatFs sf = null;
        long data = 0;
        if (System.equals(path)) {
            sf = new StatFs(Environment.getRootDirectory().getPath());
        } else if (Data.equals(path)) {
            sf = new StatFs(Environment.getDataDirectory().getPath());
        } else {
            sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        }
        if (FreeSpace.equals(type)) {
            data = sf.getFreeBlocksLong();
        } else if (AvailableSpace.equals(type)) {
            data = sf.getAvailableBlocksLong();
        } else {
            data = sf.getBlockCountLong();
        }
        long bs = sf.getBlockSizeLong();
        long size = bs * data;

        slog(isLog).append(path)
                   .append(type, FormatUtils.formatSize(size, FormatUtils.NUMERIAL_3))
                   .append("size", size)
                   .toLogD();
        return size;
    }

    /**
     * 是否SD卡空间不足
     * @return
     */
    public static boolean isLowSD() {
        if (isExistsSdcard() && getSDCardFreeSpace() < LOW_SD) {
            return true;
        }
        return false;
    }


    //    /**
    //     * 获取内存信息
    //     */
    //    public static boolean isLowMemory() {
    //        ActivityManager am = (ActivityManager) FrameworkConfig.getInstance()
    //                                                              .getAppContext()
    //                                                              .getSystemService(Context.ACTIVITY_SERVICE);
    //        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
    //        am.getMemoryInfo(info);
    //
    //        LogUtils.d(TAG,
    //                "totalMem",
    //                info.totalMem + "  formatSize= " + FormatUtils.formatSize(info.totalMem, FormatUtils.NUMERIAL_3));
    //        LogUtils.d(TAG,
    //                "availMem",
    //                info.availMem + "  formatSize= " + FormatUtils.formatSize(info.availMem, FormatUtils.NUMERIAL_3));
    //        LogUtils.d(TAG,
    //                "threshold",
    //                info.threshold + "  formatSize= " + FormatUtils.formatSize(info.threshold, FormatUtils.NUMERIAL_3));
    //        LogUtils.d(TAG, "lowMemory", info.lowMemory);
    //        return info.lowMemory;
    //    }
    //
    //    /**
    //     * 获取应用进程信息
    //     * @param packageName 包名
    //     */
    //    public static void getAppProcessInfo(String packageName) {
    //        ActivityManager am = (ActivityManager) FrameworkConfig.getInstance()
    //                                                              .getAppContext()
    //                                                              .getSystemService(Context.ACTIVITY_SERVICE);
    //
    //        // 获得系统里正在运行的所有进程
    //        List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
    //        for (RunningAppProcessInfo app : list) {
    //            // 进程ID号
    //            int pid = app.pid;
    //            // 用户ID
    //            int uid = app.uid;
    //            // 进程名
    //            String process = app.processName;
    //            if (packageName == null || packageName.equals(process)) {
    //                int[] pids = new int[]{pid};
    //                Debug.MemoryInfo[] memoryInfo = am.getProcessMemoryInfo(pids);
    //                LogUtils.d(TAG,
    //                        "getAppProcessInfo",
    //                        "pid= "
    //                                + pid
    //                                + "  uid= "
    //                                + uid
    //                                + "  process= "
    //                                + process
    //                                + "  memory= "
    //                                + FormatUtils.formatSize(memoryInfo[0].dalvikPrivateDirty * 1024,
    //                                FormatUtils.NUMERIAL_3));
    //            }
    //        }
    //    }
}
