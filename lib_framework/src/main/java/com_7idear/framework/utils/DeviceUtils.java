package com_7idear.framework.utils;

import android.Manifest;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com_7idear.framework.application.BaseApplication;
import com_7idear.framework.config.BaseConfig;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.log.LogUtils;
import com_7idear.framework.log.TimerFrameUtils;
import com_7idear.framework.preference.PreferencesUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.UUID;


/**
 * 设备工具类
 * @author ieclipse 19-12-5
 * @description 获取设备UUID，相关信息，屏幕尺寸等
 */
public class DeviceUtils
        extends BaseConfig {

    private static DeviceUtils mInstance;

    private Context mContext; //环境对象

    private UUID UUID; //UUID

    private int androidSDK; //Android系统版本

    private String phoneBoard; //主板
    private String phoneBrand; //Android系统定制商
    private String phoneCPU_ABT; //CPU指令集
    private String phoneDevice; //设备参数
    private String phoneDisplay; //显示屏参数
    private String phoneFingerprint; //硬件名称
    private String phoneHost; //主机地址
    private String phoneID; //修订版本列表
    private String phoneManufacturer; //硬件制造商
    private String phoneModel; //型号
    private String phoneVersion; //系统版本
    private String phoneProduct; //手机制造商
    private String phoneTags; //BUILD标识
    private long   phoneTime; //BUILD时间
    private String phoneType; //BUILD类型
    private String phoneUser; //用户

    private DisplayMetrics screenMetrics; //屏幕指标
    private float          screenDensity; //屏幕密度
    private float          screenScaledDensity; //屏幕字体密度
    private int            screenWidthPixels; //屏幕宽度
    private int            screenHeightPixels; //屏幕高度
    private int            screenStatusBarHeight; //屏幕状态栏高度
    private int            screenNavigationBarHeight; //导航栏高度
    private int            mIsNotchDevice = -1; //是否为刘海屏的设备

    private String  telIMEI; //IMEI
    private String  telIMSI; //IMSI
    private String  telLineNumber; //电话号
    private String  telMCC; //移动国家码
    private String  telMNC; //移动网络码
    private int     telCallState; //电话状态——0：无活动，1：响铃，2：接听
    private int     telCid; //基站编码
    private int     telLac; //区域编码
    private String  telNetworkCountry; //网络国家编码
    private String  telNetworkOperator; //网络运营商编码
    private String  telNetworkOperatorName; //网络运营商名称
    private int     telNetworkType; //网络类型——0：未知网络，1：GPRS网络，2：EDGE网络，3：UMTS网络，4：CDMA网络，5：EVDO网络0，6：EVDO网络A，7：1xRTT网络，8：HSDPA网络，9：HSUPA网络，10：HSPA网络
    private String  telSimCountry; //SIM卡国家编码
    private String  telSimOperator; //SIM运营商编码
    private String  telSimOperatorName; //SIM运营商名称
    private String  telSimSerialNumber; //SIM卡的序列号
    private int     telSimState; //SIM卡状态—— 0：未知，1：没插卡，2：锁定（需要用户PIN码解锁），3：锁定（需要用户PUK码解锁），4：锁定（需要网络PIN解锁），5：就绪状态
    private String  telVoiceMailAlphaTag; //语音信箱
    private String  telVoiceMailNumber; //语音信箱号码
    private boolean telHasIccCard; //ICC卡是否存在
    private boolean telIsNetworkRoaming; //是否漫游（在GSM网络中）

    private String appPackageName; //APP名称
    private String appPackageCodePath; //APP代码目录
    private String appPackageResourcePath; //APP资源目录
    private String appDir; //APP内置系统目录下文件目录
    private String appFilesDir; //APP内置系统文件目录
    private String appCacheDir; //APP内置系统缓存目录
    private String appExternalFilesDir; //APP外置存储文件目录
    private String appExternalCacheDir; //APP外置存储缓存目录
    private String appRootDirectory; //APP系统目录
    private String appDataDirectory; //APP系统数据目录
    private String appDownloadCacheDirectory; //APP系统下载缓存目录
    private String appExternalStorageDirectory; //APP系统外置存储目录

    DeviceUtils() {
        mContext = BaseApplication.getAppContext();
        TimerFrameUtils.timerFrameRestart();
        initUUID();
        TimerFrameUtils.timerFrame();
        initAndroid();
        TimerFrameUtils.timerFrame();
        initPhone();
        TimerFrameUtils.timerFrame();
        initScreen();
        TimerFrameUtils.timerFrame();
        initTelephony();
        TimerFrameUtils.timerFrame();
        initAppDirs();
        TimerFrameUtils.timerFrame();
    }

    public static DeviceUtils getInstance() {
        if (mInstance == null) {
            synchronized (DeviceUtils.class) {
                if (mInstance == null) mInstance = new DeviceUtils();
            }
        }
        return mInstance;
    }


    /**
     * 初始化UUID
     */
    private void initUUID() {
        if (UUID == null) {
            String id = PreferencesUtils.getInstance().getDeviceUUID();
            if (!TxtUtils.isEmpty(id)) {
                UUID = UUID.fromString(id);
            } else {
                String androidId = Settings.Secure.getString(mContext.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                try {
                    if (!"9774d56d682e549c".equals(androidId)) {
                        UUID = UUID.nameUUIDFromBytes(androidId.getBytes("UTF8"));
                    } else if (PermissionUtils.checkPermission(mContext,
                            Manifest.permission.READ_PHONE_STATE)) {
                        final String deviceId = ((TelephonyManager) mContext.getSystemService(
                                Context.TELEPHONY_SERVICE)).getDeviceId();
                        UUID = deviceId != null
                                ? UUID.nameUUIDFromBytes(deviceId.getBytes("UTF8"))
                                : UUID.randomUUID();
                    } else {
                        UUID = UUID.randomUUID();
                        return;
                    }
                } catch (UnsupportedEncodingException e) {
                    LogUtils.catchException(e);
                }
                PreferencesUtils.getInstance().setDeviceUUID(UUID.toString());
            }
        }
    }

    /**
     * 初始化AndroidSDK
     */
    private void initAndroid() {
        androidSDK = Build.VERSION.SDK_INT;
    }

    /**
     * 初始化设备信息
     */
    private void initPhone() {
        phoneBoard = Build.BOARD;
        phoneBrand = Build.BRAND;
        phoneCPU_ABT = Build.CPU_ABI;
        phoneDevice = Build.DEVICE;
        phoneDisplay = Build.DISPLAY;
        phoneFingerprint = Build.FINGERPRINT;
        phoneHost = Build.HOST;
        phoneID = Build.ID;
        phoneManufacturer = Build.MANUFACTURER;
        phoneModel = Build.MODEL;
        phoneVersion = Build.VERSION.RELEASE;
        phoneProduct = Build.PRODUCT;
        phoneTags = Build.TAGS;
        phoneTime = Build.TIME;
        phoneType = Build.TYPE;
        phoneUser = Build.USER;
    }

    /**
     * 初始化屏幕信息
     */
    private void initScreen() {
        screenMetrics = mContext.getResources().getDisplayMetrics();
        screenDensity = screenMetrics.density;
        screenScaledDensity = screenMetrics.scaledDensity;
        screenWidthPixels = screenMetrics.widthPixels;
        screenHeightPixels = screenMetrics.heightPixels;
        //        screenStatusBarHeight = getStatusBarHeight(context);
        //        navigationBarHeight = getNavigationBarHeight(context);
    }

    /**
     * 初始化电话信息
     */
    private void initTelephony() {
        final TelephonyManager tm = (TelephonyManager) mContext.getSystemService(
                Context.TELEPHONY_SERVICE);
        telCallState = tm.getCallState();
        if (PermissionUtils.checkPermission(mContext,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION})) {
            try {
                final GsmCellLocation gcl = (GsmCellLocation) tm.getCellLocation();
                if (gcl != null) {
                    telCid = gcl.getCid();
                    telLac = gcl.getLac();
                }
            } catch (Exception e) {
                LogUtils.catchException(e);
            }
        }
        if (PermissionUtils.checkPermission(mContext, Manifest.permission.READ_PHONE_STATE)) {
            telIMEI = tm.getDeviceId();
            telIMSI = tm.getSubscriberId();
            telLineNumber = tm.getLine1Number();
            telSimSerialNumber = tm.getSimSerialNumber();
            telVoiceMailAlphaTag = tm.getVoiceMailAlphaTag();
            telVoiceMailNumber = tm.getVoiceMailNumber();
        }
        telNetworkCountry = tm.getNetworkCountryIso();
        telNetworkOperator = tm.getNetworkOperator();
        if (!TextUtils.isEmpty(telNetworkOperator)) {
            telMCC = telNetworkOperator.substring(0, 3);
            telMNC = telNetworkOperator.substring(3);
        }
        telNetworkOperatorName = tm.getNetworkOperatorName();
        telNetworkType = tm.getNetworkType();
        telSimCountry = tm.getSimCountryIso();
        telSimOperator = tm.getSimOperator();
        telSimOperatorName = tm.getSimOperatorName();
        telSimState = tm.getSimState();
        telHasIccCard = tm.hasIccCard();
        telIsNetworkRoaming = tm.isNetworkRoaming();

    }

    /**
     * 初始化APP目录
     */
    private void initAppDirs() {
        appPackageName = mContext.getPackageName();
        appPackageCodePath = mContext.getPackageCodePath();
        appPackageResourcePath = mContext.getPackageResourcePath();
        appDir = TxtUtils.toStr(mContext.getDir("", 0));
        appFilesDir = TxtUtils.toStr(mContext.getFilesDir().toString());
        appCacheDir = TxtUtils.toStr(mContext.getCacheDir().toString());
        appExternalFilesDir = TxtUtils.toStr(mContext.getExternalFilesDir(""));
        appExternalCacheDir = TxtUtils.toStr(mContext.getExternalCacheDir());
        appRootDirectory = TxtUtils.toStr(Environment.getRootDirectory());
        appDataDirectory = TxtUtils.toStr(Environment.getDataDirectory());
        appDownloadCacheDirectory = TxtUtils.toStr(Environment.getDownloadCacheDirectory());
        appExternalStorageDirectory = TxtUtils.toStr(Environment.getExternalStorageDirectory());

    }

    /**
     * DIP值转换PX像素
     * @param dipValue DIP值
     * @return
     */
    public static int dip2px(float dipValue) {
        return mInstance == null ? 0 : (int) (dipValue * mInstance.getScreenDensity() + 0.5f);
    }

    /**
     * PX像素转换DIP值
     * @param pxValue PX像素
     * @return
     */
    public static int px2dip(float pxValue) {
        return mInstance == null ? 0 : (int) (pxValue / mInstance.getScreenDensity() + 0.5f);
    }

    /**
     * 获取状态栏高度
     * @param context 环境对象
     * @return
     */
    public int getStatusBarHeight(Context context) {
        int resourceId = context.getResources()
                                .getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        } else {
            return getCommonStatusBarHeight(context);
        }
    }

    private int getCommonStatusBarHeight(Context context) {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusBarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusBarHeight = context.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            LogUtils.catchException(e);
        }
        return statusBarHeight;
    }

    /**
     * 获取UUID
     * @return
     */
    public UUID getUUID() {
        return UUID;
    }

    /**
     * 获取Android系统版本
     * @return
     */
    public int getAndroidSDK() {
        return androidSDK;
    }

    /**
     * 获取主板
     * @return
     */
    public String getPhoneBoard() {
        return phoneBoard;
    }

    /**
     * 获取Android系统定制商
     * @return
     */
    public String getPhoneBrand() {
        return phoneBrand;
    }

    /**
     * 获取CPU指令集
     * @return
     */
    public String getPhoneCPU_ABT() {
        return phoneCPU_ABT;
    }

    /**
     * 获取设备参数
     * @return
     */
    public String getPhoneDevice() {
        return phoneDevice;
    }

    /**
     * 获取显示屏参数
     * @return
     */
    public String getPhoneDisplay() {
        return phoneDisplay;
    }

    /**
     * 获取硬件名称
     * @return
     */
    public String getPhoneFingerprint() {
        return phoneFingerprint;
    }

    /**
     * 获取主机地址
     * @return
     */
    public String getPhoneHost() {
        return phoneHost;
    }

    /**
     * 获取修订版本列表
     * @return
     */
    public String getPhoneID() {
        return phoneID;
    }

    /**
     * 获取硬件制造商
     * @return
     */
    public String getPhoneManufacturer() {
        return phoneManufacturer;
    }

    /**
     * 获取手机型号
     * @return
     */
    public String getPhoneModel() {
        return phoneModel;
    }

    /**
     * 获取系统版本
     * @return
     */
    public String getPhoneVersion() {
        return phoneVersion;
    }

    /**
     * 获取手机制造商
     * @return
     */
    public String getPhoneProduct() {
        return phoneProduct;
    }

    /**
     * 获取BUILD标识
     * @return
     */
    public String getPhoneTags() {
        return phoneTags;
    }

    /**
     * 获取BUILD时间
     * @return
     */
    public long getPhoneTime() {
        return phoneTime;
    }

    /**
     * 获取BUILD类型
     * @return
     */
    public String getPhoneType() {
        return phoneType;
    }

    /**
     * 获取用户
     * @return
     */
    public String getPhoneUser() {
        return phoneUser;
    }

    /**
     * 获取屏幕指标
     * @return
     */
    public DisplayMetrics getScreenMetrics() {
        return screenMetrics;
    }

    /**
     * 获取屏幕密度
     * @return
     */
    public float getScreenDensity() {
        return screenDensity;
    }

    /**
     * 获取屏幕字体密度
     * @return
     */
    public float getScreenScaledDensity() {
        return screenScaledDensity;
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    public int getScreenWidthPixels() {
        return screenWidthPixels;
    }

    /**
     * 获取屏幕高度
     * @return
     */
    public int getScreenHeightPixels() {
        return screenHeightPixels;
    }

    /**
     * 获取屏幕状态栏高度
     * @return
     */
    public int getScreenStatusBarHeight() {
        return screenStatusBarHeight;
    }

    /**
     * 获取IMEI
     * @return
     */
    public String getTelIMEI() {
        return telIMEI;
    }

    /**
     * 获取IMSI
     * @return
     */
    public String getTelIMSI() {
        return telIMSI;
    }

    /**
     * 获取电话号
     * @return
     */
    public String getTelLineNumber() {
        return telLineNumber;
    }

    /**
     * 获取移动国家码
     * @return
     */
    public String getTelMCC() {
        return telMCC;
    }

    /**
     * 获取移动网络码
     * @return
     */
    public String getTelMNC() {
        return telMNC;
    }

    /**
     * 获取电话状态——0：无活动，1：响铃，2：接听
     * @return
     */
    public int getTelCallState() {
        return telCallState;
    }

    /**
     * 获取基站编码
     * @return
     */
    public int getTelCid() {
        return telCid;
    }

    /**
     * 获取区域编码
     * @return
     */
    public int getTelLac() {
        return telLac;
    }

    /**
     * 获取网络国家编码
     * @return
     */
    public String getTelNetworkCountry() {
        return telNetworkCountry;
    }

    /**
     * 获取网络运营商编码
     * @return
     */
    public String getTelNetworkOperator() {
        return telNetworkOperator;
    }

    /**
     * 获取网络运营商名称
     * @return
     */
    public String getTelNetworkOperatorName() {
        return telNetworkOperatorName;
    }

    /**
     * 获取网络类型——0：未知网络，1：GPRS网络，2：EDGE网络，3：UMTS网络，4：CDMA网络，5：EVDO网络0，6：EVDO网络A，7：1xRTT网络，8：HSDPA网络，9：HSUPA网络，10：HSPA网络
     * @return
     */
    public int getTelNetworkType() {
        return telNetworkType;
    }

    /**
     * 获取SIM卡国家编码
     * @return
     */
    public String getTelSimCountry() {
        return telSimCountry;
    }

    /**
     * 获取SIM运营商编码
     * @return
     */
    public String getTelSimOperator() {
        return telSimOperator;
    }

    /**
     * 获取SIM运营商名称
     * @return
     */
    public String getTelSimOperatorName() {
        return telSimOperatorName;
    }

    /**
     * 获取SIM卡的序列号
     * @return
     */
    public String getTelSimSerialNumber() {
        return telSimSerialNumber;
    }

    /**
     * 获取SIM卡状态——0：未知，1：没插卡，2：锁定（需要用户PIN码解锁），3：锁定（需要用户PUK码解锁），4：锁定（需要网络PIN解锁），5：就绪状态
     * @return
     */
    public int getTelSimState() {
        return telSimState;
    }

    /**
     * 获取语音信箱
     * @return
     */
    public String getTelVoiceMailAlphaTag() {
        return telVoiceMailAlphaTag;
    }

    /**
     * 获取语音信箱号码
     * @return
     */
    public String getTelVoiceMailNumber() {
        return telVoiceMailNumber;
    }

    /**
     * 获取ICC卡是否存在
     * @return
     */
    public boolean isTelHasIccCard() {
        return telHasIccCard;
    }

    /**
     * 获取是否漫游（在GSM网络中）
     * @return
     */
    public boolean isTelIsNetworkRoaming() {
        return telIsNetworkRoaming;
    }

    public LogEntity toLog() {
        log().appendLine()
             .appendLine("------------------------------")
             .appendLine("UUID 设备唯一标识", UUID)
             .appendLine("------------------------------")
             .appendLine("androidSDK Android系统版本", androidSDK)
             .appendLine("------------------------------")
             .appendLine("phoneBoard 主板", phoneBoard)
             .appendLine("phoneBrand Android系统定制商", phoneBrand)
             .appendLine("phoneCPU_ABT CPU指令集", phoneCPU_ABT)
             .appendLine("phoneDevice 设备参数", phoneDevice)
             .appendLine("phoneDisplay 显示屏参数", phoneDisplay)
             .appendLine("phoneFingerprint 硬件名称", phoneFingerprint)
             .appendLine("phoneHost 主机地址", phoneHost)
             .appendLine("phoneID 修订版本列表", phoneID)
             .appendLine("phoneManufacturer 硬件制造商", phoneManufacturer)
             .appendLine("phoneModel 手机型号", phoneModel)
             .appendLine("phoneVersion 系统版本", phoneVersion)
             .appendLine("phoneProduct 手机制造商", phoneProduct)
             .appendLine("phoneTags BUILD标识", phoneTags)
             .appendLine("phoneTime BUILD时间", phoneTime)
             .appendLine("phoneType BUILD类型", phoneType)
             .appendLine("phoneUser 用户", phoneUser)
             .appendLine("------------------------------")
             .appendLine("screenMetrics 屏幕指标", screenMetrics)
             .appendLine("screenDensity 屏幕密度", screenDensity)
             .appendLine("screenScaledDensity 屏幕字体密度", screenScaledDensity)
             .appendLine("screenWidthPixels 屏幕宽度", screenWidthPixels)
             .appendLine("screenHeightPixels 屏幕高度", screenHeightPixels)
             .appendLine("screenStatusBarHeight 屏幕状态栏高度", screenStatusBarHeight)
             .appendLine("screenNavigationBarHeight 屏幕导航栏高度", screenNavigationBarHeight)
             .appendLine("------------------------------")
             .appendLine("telIMEI 设备IMEI", telIMEI)
             .appendLine("telIMSI 设备IMSI", telIMSI)
             .appendLine("telLineNumber 电话号", telLineNumber)
             .appendLine("telVoiceMailAlphaTag 语音信箱", telVoiceMailAlphaTag)
             .appendLine("telVoiceMailNumber 语音信箱号码", telVoiceMailNumber)
             .appendLine("telMCC 移动国家码", telMCC)
             .appendLine("telMNC 移动网络码", telMNC)
             .appendLine("telCallState 电话状态", telCallState)
             .appendLine("telCid 基站编码", telCid)
             .appendLine("telLac 区域编码", telLac)
             .appendLine("telNetworkCountry 网络国家编码", telNetworkCountry)
             .appendLine("telNetworkOperator 网络运营商编码", telNetworkOperator)
             .appendLine("telNetworkOperatorName 网络运营商名称", telNetworkOperatorName)
             .appendLine("telNetworkType 网络类型", telNetworkType)
             .appendLine("telSimCountry SIM卡国家编码", telSimCountry)
             .appendLine("telSimOperator SIM运营商编码", telSimOperator)
             .appendLine("telSimOperatorName SIM运营商名称", telSimOperatorName)
             .appendLine("telSimSerialNumber SIM卡的序列号", telSimSerialNumber)
             .appendLine("telSimState SIM卡状态", telSimState)
             .appendLine("telHasIccCard ICC卡是否存在", telHasIccCard)
             .appendLine("telIsNetworkRoaming 是否漫游（在GSM网络中）", telIsNetworkRoaming)
             .appendLine("------------------------------")
             .appendLine("getPackageName", mContext.getPackageName())
             .appendLine("getPackageCodePath", mContext.getPackageCodePath())
             .appendLine("getPackageResourcePath", mContext.getPackageResourcePath())
             .appendLine("getDir", mContext.getDir("", 0))
             .appendLine("getFilesDir", mContext.getFilesDir())
             .appendLine("getCacheDir", mContext.getCacheDir())
             .appendLine("getExternalFilesDir", mContext.getExternalFilesDir(""))
             .appendLine("getExternalCacheDir", mContext.getExternalCacheDir())
             .appendLine("getRootDirectory", Environment.getRootDirectory())
             .appendLine("getDataDirectory", Environment.getDataDirectory())
             .appendLine("getDownloadCacheDirectory", Environment.getDownloadCacheDirectory())
             .appendLine("getExternalStorageDirectory", Environment.getExternalStorageDirectory())
             .appendLine("------------------------------")
             .toLogD("DeviceUtils");

        return null;
    }

}
