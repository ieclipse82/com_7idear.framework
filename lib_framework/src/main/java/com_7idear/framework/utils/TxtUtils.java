package com_7idear.framework.utils;

import android.text.TextUtils;

/**
 * 文本工具类
 * @author iEclipse 2019/7/2
 * @description
 */
public class TxtUtils {

    /**
     * 是否为空
     * @param s 字符串
     * @return
     */
    public static boolean isEmpty(CharSequence s) {
        if (s == null || s.length() == 0 || s.toString().trim().length() == 0) return true;
        return false;
    }

    /**
     * 是否为空，为空用默认值替换
     * @param s            字符串
     * @param defaultValue 默认值
     * @return
     */
    public static String isEmpty(CharSequence s, CharSequence defaultValue) {
        if (isEmpty(s)) return defaultValue == null ? "" : defaultValue.toString();
        return s.toString();
    }

    /**
     * 是否为空，为空用空替换
     * @param s 字符串
     * @return
     */
    public static String isEmptyToSet(CharSequence s) {
        if (isEmpty(s)) return "";
        return s.toString();
    }

    /**
     * 是否为空（多参数，或值为空就返回真）
     * @param s 多个字符串参数
     * @return
     */
    public static boolean isEmptyOR(CharSequence... s) {
        if (s == null) return true;
        for (int i = 0, c = s.length; i < c; i++) {
            if (isEmpty(s[i])) return true;
        }
        return false;
    }

    /**
     * 是否为空（多参数，与值为空就返回真）
     * @param s 多个字符串参数
     * @return
     */
    public static boolean isEmptyAND(CharSequence... s) {
        if (s == null) return true;
        for (int i = 0, c = s.length; i < c; i++) {
            if (!isEmpty(s[i])) return false;
        }
        return true;
    }

    /**
     * 是否相等
     * @param a 字符
     * @param b 字符
     * @return
     */
    public static boolean equals(CharSequence a, CharSequence b) {
        return TextUtils.equals(a, b);
    }

    /**
     * 是否相等（多值判断）
     * @param value     需要判断的值
     * @param keyValues 值数组
     * @return
     */
    public static boolean equals(int value, int... keyValues) {
        if (keyValues == null) return false;
        for (int i = 0, c = keyValues.length; i < c; i++) {
            if (value != keyValues[i]) return false;
        }
        return true;
    }

    /**
     * 是否相等（多值判断）
     * @param value     需要判断的值
     * @param keyValues 值数组
     * @return
     */
    public static boolean equals(String value, String... keyValues) {
        if (keyValues == null) return false;
        for (int i = 0, c = keyValues.length; i < c; i++) {
            if (!equals(value, keyValues[i])) return false;
        }
        return true;
    }

    /**
     * 是否相等（不区分大小写）
     * @param a 字符
     * @param b 字符
     * @return
     */
    public static boolean equalsIgnoreCase(String a, String b) {
        return a == b || (a != null && a.equalsIgnoreCase(b)) || (b != null && b.equalsIgnoreCase(
                a));
    }

    /**
     * 对象转字符串
     * @param obj 对象
     * @return
     */
    public static String toStr(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    private static int index = 0;

    /**
     * 字符串比较，根据字符串中每个字符转换成的ASCII码来比较
     * @param leftStr
     * @param rightStr
     * @return int值 >0  左边的比右边的大，<0 左边的比右边的小
     */
    public static int compare(String leftStr, String rightStr) {
        int tmpIndex = 0;
        if (leftStr.charAt(index) - rightStr.charAt(index) == 0) {
            if (index < leftStr.length() - 1 && index < rightStr.length() - 1) {
                index++;
                tmpIndex = index;
                compare(leftStr, rightStr);
            } else {
                tmpIndex = index;
                index = 0;
            }
        } else {
            index = 0;
        }
        return leftStr.charAt(tmpIndex) - rightStr.charAt(tmpIndex);
    }
}
