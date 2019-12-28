package com_7idear.framework.preference;

import android.content.SharedPreferences;

import com_7idear.framework.application.BaseApplication;
import com_7idear.framework.utils.TxtUtils;

import java.util.Set;


/**
 * 基础共享文件类
 * @author ieclipse 19-12-5
 * @description 实现共享文件的操作，需要实现获取共享文件方法
 */
public abstract class BasePreferences {

    private static final String DEFAULT_PREFERENCES = "default_preferences";

    private SharedPreferences preferences; //共享文件

    public BasePreferences() {
        String file = getPreferencesFile();
        if (TxtUtils.isEmpty(file)) {
            preferences = BaseApplication.getAppContext()
                                         .getSharedPreferences(DEFAULT_PREFERENCES, 0);
        } else {
            preferences = BaseApplication.getAppContext().getSharedPreferences(file, 0);
        }
    }

    /**
     * 获取共享文件
     * @return
     */
    public abstract String getPreferencesFile();

    /**
     * 获取编辑器
     * @return
     */
    protected SharedPreferences.Editor getEditor() {
        return preferences.edit();
    }

    /**
     * 获取布尔值
     * @param key 关键字
     * @return
     */
    protected boolean getBooleanValue(String key) {
        return getBooleanValue(key, false);
    }

    /**
     * 获取布尔值
     * @param key   关键字
     * @param value 默认值
     * @return
     */
    protected boolean getBooleanValue(String key, boolean value) {
        return preferences.getBoolean(key, value);
    }

    /**
     * 获取浮点值
     * @param key 关键字
     * @return
     */
    protected float getFloatValue(String key) {
        return getFloatValue(key, 0f);
    }

    /**
     * 获取浮点值
     * @param key   关键字
     * @param value 默认值
     * @return
     */
    protected float getFloatValue(String key, float value) {
        return preferences.getFloat(key, value);
    }

    /**
     * 获取数值
     * @param key 关键字
     * @return
     */
    protected int getIntValue(String key) {
        return getIntValue(key, 0);
    }

    /**
     * 获取数值
     * @param key   关键字
     * @param value 默认值
     * @return
     */
    protected int getIntValue(String key, int value) {
        return preferences.getInt(key, value);
    }

    /**
     * 获取长整型值
     * @param key 关键字
     * @return
     */
    protected long getLongValue(String key) {
        return getLongValue(key, 0l);
    }

    /**
     * 获取长整型值
     * @param key   关键字
     * @param value 默认值
     * @return
     */
    protected long getLongValue(String key, long value) {
        return preferences.getLong(key, value);
    }

    /**
     * 获取字符串
     * @param key 关键字
     * @return
     */
    protected String getStringValue(String key) {
        return getStringValue(key, "");
    }

    /**
     * 获取字符串
     * @param key   关键字
     * @param value 默认值
     * @return
     */
    protected String getStringValue(String key, String value) {
        return preferences.getString(key, value);
    }

    /**
     * 获取字符串集合
     * @param key 关键字
     * @return
     */
    protected Set<String> getSetValue(String key) {
        return getSetValue(key, null);
    }

    /**
     * 获取字符串集合
     * @param key   关键字
     * @param value 默认值
     * @return
     */
    protected Set<String> getSetValue(String key, Set<String> value) {
        return preferences.getStringSet(key, value);
    }

    /**
     * 设置值
     * @param key   关键字
     * @param value
     * @return
     */
    protected void setValueApply(String key, Object value) {
        if (value instanceof Boolean) {
            preferences.edit().putBoolean(key, (Boolean) value).apply();
        } else if (value instanceof Float) {
            preferences.edit().putFloat(key, (Float) value).apply();
        } else if (value instanceof Integer) {
            preferences.edit().putInt(key, (Integer) value).apply();
        } else if (value instanceof Long) {
            preferences.edit().putLong(key, (Long) value).apply();
        } else if (value instanceof String) {
            preferences.edit().putString(key, (String) value).apply();
        } else if (value instanceof Set) {
            preferences.edit().putStringSet(key, (Set<String>) value).apply();
        }
    }

    /**
     * 设置值
     * @param key   关键字
     * @param value
     * @return
     */
    protected boolean setValueCommit(String key, Object value) {
        if (value instanceof Boolean) {
            return preferences.edit().putBoolean(key, (Boolean) value).commit();
        } else if (value instanceof Float) {
            return preferences.edit().putFloat(key, (Float) value).commit();
        } else if (value instanceof Integer) {
            return preferences.edit().putInt(key, (Integer) value).commit();
        } else if (value instanceof Long) {
            return preferences.edit().putLong(key, (Long) value).commit();
        } else if (value instanceof String) {
            return preferences.edit().putString(key, (String) value).commit();
        } else if (value instanceof Set) {
            return preferences.edit().putStringSet(key, (Set<String>) value).commit();
        }
        return false;
    }
}
