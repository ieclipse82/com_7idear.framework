package com_7idear.framework.utils;

import android.os.Build;

/**
 * Android SDK版本检测工具类
 * @author ieclipse 19-12-10
 * @description 统一提供系统Android版本检测方法
 */
public class SDKUtils {

    /**
     * 检测系统版本是否是Android 1.5(API_3)以上版本
     * @return
     */
    public static boolean equalAPI_3_Cupcake() {
        return Build.VERSION.SDK_INT >= 3;
    }

    /**
     * 检测系统版本是否是Android 1.6(API_4)以上版本
     * @return
     */
    public static boolean equalAPI_4_Donut() {
        return Build.VERSION.SDK_INT >= 4;
    }

    /**
     * 检测系统版本是否是Android 2.0(API_5)以上版本
     * @return
     */
    public static boolean equalAPI_5_Eclair() {
        return Build.VERSION.SDK_INT >= 5;
    }

    /**
     * 检测系统版本是否是Android 2.0.1(API_6)以上版本
     * @return
     */
    public static boolean equalAPI_6_Eclair_0_1() {
        return Build.VERSION.SDK_INT >= 6;
    }

    /**
     * 检测系统版本是否是Android 2.1(API_7)以上版本
     * @return
     */
    public static boolean equalAPI_7_Eclair_MR1() {
        return Build.VERSION.SDK_INT >= 7;
    }

    /**
     * 检测系统版本是否是Android 2.2(API_8)以上版本
     * @return
     */
    public static boolean equalAPI_8_Froyo() {
        return Build.VERSION.SDK_INT >= 8;
    }

    /**
     * 检测系统版本是否是Android 2.3(API_9)以上版本
     * @return
     */
    public static boolean equalAPI_9_Gingerbread() {
        return Build.VERSION.SDK_INT >= 9;
    }

    /**
     * 检测系统版本是否是Android 2.3.3(API_10)以上版本
     * @return
     */
    public static boolean equalAPI_10_Gingerbread_MR1() {
        return Build.VERSION.SDK_INT >= 10;
    }

    /**
     * 检测系统版本是否是Android 3.0(API_11)以上版本
     * @return
     */
    public static boolean equalAPI_11_Honeycomb() {
        return Build.VERSION.SDK_INT >= 11;
    }

    /**
     * 检测系统版本是否是Android 3.1(API_12)以上版本
     * @return
     */
    public static boolean equalAPI_12_Honeycomb_MR1() {
        return Build.VERSION.SDK_INT >= 12;
    }

    /**
     * 检测系统版本是否是Android 3.2(API_13)以上版本
     * @return
     */
    public static boolean equalAPI_13_Honeycomb_MR2() {
        return Build.VERSION.SDK_INT >= 13;
    }

    /**
     * 检测系统版本是否是Android 4.0(API_14)以上版本
     * @return
     */
    public static boolean equalAPI_14_Ice_Cream_Sandwich() {
        return Build.VERSION.SDK_INT >= 14;
    }

    /**
     * 检测系统版本是否是Android 4.0.3(API_15)以上版本
     * @return
     */
    public static boolean equalAPI_15_Ice_Cream_Sandwich_MR1() {
        return Build.VERSION.SDK_INT >= 15;
    }

    /**
     * 检测系统版本是否是Android 4.1(API_16)以上版本
     * @return
     */
    public static boolean equalAPI_16_Jelly_Bean() {
        return Build.VERSION.SDK_INT >= 16;
    }

    /**
     * 检测系统版本是否是Android 4.2(API_17)以上版本
     * @return
     */
    public static boolean equalAPI_17_Jelly_Bean_MR1() {
        return Build.VERSION.SDK_INT >= 17;
    }

    /**
     * 检测系统版本是否是Android 4.3(API_18)以上版本
     * @return
     */
    public static boolean equalAPI_18_Jelly_Bean_MR2() {
        return Build.VERSION.SDK_INT >= 18;
    }

    /**
     * 检测系统版本是否是Android 4.4(API_19)以上版本
     * @return
     */
    public static boolean equalAPI_19_Kitkat() {
        return Build.VERSION.SDK_INT >= 19;
    }

    /**
     * 检测系统版本是否是Android 4.4w(API_20)以上版本
     * @return
     */
    public static boolean equalAPI_20_Kitkat_Watch() {
        return Build.VERSION.SDK_INT >= 20;
    }

    /**
     * 检测系统版本是否是Android 5.0(API_21)以上版本
     * @return
     */
    public static boolean equalAPI_21_Lollipop() {
        return Build.VERSION.SDK_INT >= 21;
    }

    /**
     * 检测系统版本是否是Android 5.1(API_22)以上版本
     * @return
     */
    public static boolean equalAPI_22_Lollipop_MR1() {
        return Build.VERSION.SDK_INT >= 22;
    }

    /**
     * 检测系统版本是否是Android 6.0(API_23)以上版本
     * @return
     */
    public static boolean equalAPI_23_Marshmallow() {
        return Build.VERSION.SDK_INT >= 23;
    }

    /**
     * 检测系统版本是否是Android 7.0(API_24)以上版本
     * @return
     */
    public static boolean equalAPI_24_Nougat() {
        return Build.VERSION.SDK_INT >= 24;
    }

    /**
     * 检测系统版本是否是Android 7.1.1(API_25)以上版本
     * @return
     */
    public static boolean equalAPI_25_Nougat() {
        return Build.VERSION.SDK_INT >= 25;
    }

    /**
     * 检测系统版本是否是Android 8(API_26)以上版本
     * @return
     */
    public static boolean equalAPI_26_Oreo() {
        return Build.VERSION.SDK_INT >= 26;
    }

    /**
     * 检测系统版本是否是Android 8.1(API_27)以上版本
     * @return
     */
    public static boolean equalAPI_27_Oreo() {
        return Build.VERSION.SDK_INT >= 27;
    }

    /**
     * 检测系统版本是否是Android 9(API_28)以上版本
     * @return
     */
    public static boolean equalAPI_28_Pie() {
        return Build.VERSION.SDK_INT >= 28;
    }

    /**
     * 检测系统版本是否是Android 10(API_29)以上版本
     * @return
     */
    public static boolean equalAPI_29_Q() {
        return Build.VERSION.SDK_INT >= 29;
    }


}
