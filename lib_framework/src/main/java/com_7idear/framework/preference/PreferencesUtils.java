package com_7idear.framework.preference;

/**
 * 共享文件工具类
 * @author ieclipse 19-12-5
 * @description 保存设备UUID、应用主题包名、APP首次启动等信息。可继承此类来完成自己的共享文件，需要实现子类实例
 */
public class PreferencesUtils
        extends BasePreferences {

    private static final String PREFERENCES = "preferences"; //共享文件

    /**
     * 设备UUID
     */
    protected static final String KEY_DEVICE_UUID       = "KEY_DEVICE_UUID";
    /**
     * 主题应用名
     */
    protected static final String KEY_THEME_PACKAGENAME = "KEY_THEME_PACKAGENAME";
    /**
     * 应用第一次启动
     */
    protected static final String KEY_FIRST_LAUNCHER    = "KEY_FIRST_LAUNCHER";

    private static PreferencesUtils mInstance;

    public static PreferencesUtils getInstance() {
        if (mInstance == null) {
            synchronized (PreferencesUtils.class) {
                if (mInstance == null) {
                    mInstance = new PreferencesUtils();
                }
            }
        }
        return mInstance;
    }

    @Override
    public String getPreferencesFile() {
        return PREFERENCES;
    }


    public String getDeviceUUID() {
        return getStringValue(KEY_DEVICE_UUID);
    }

    public boolean setDeviceUUID(String uuid) {
        return setValueCommit(KEY_DEVICE_UUID, uuid);
    }

    public String getThemePackageName() {
        return getStringValue(KEY_THEME_PACKAGENAME);
    }

    public boolean setThemePackageName(String themePackageName) {
        return setValueCommit(KEY_THEME_PACKAGENAME, themePackageName);
    }

    public boolean getFirstLauncher() {
        return getBooleanValue(KEY_FIRST_LAUNCHER, true);
    }

    public boolean setFirstLauncher(boolean isFirstLauncher) {
        return setValueCommit(KEY_FIRST_LAUNCHER, isFirstLauncher);
    }
}
