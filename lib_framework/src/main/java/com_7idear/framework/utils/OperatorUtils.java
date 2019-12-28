package com_7idear.framework.utils;

/**
 * 运算符工具类
 * @author ieclipse 19-12-16
 * @description
 */
public class OperatorUtils {

    /**
     * 检查与值是否相等
     * @param equalsValue 相等的值
     * @param value       值
     * @return
     */
    public static boolean equalsAndValue(int equalsValue, int value) {
        return equalsValue == (equalsValue & value);
    }

    /**
     * 左移
     * @param value   值
     * @param postion 位
     * @return
     */
    public static int getMoveLeft(int value, int postion) {
        return value << postion;
    }

    /**
     * 右移
     * @param value   值
     * @param postion 位
     * @return
     */
    public static int getMoveRight(int value, int postion) {
        return value >> postion;
    }

    /**
     * 添加与值
     * @param value    值
     * @param andValue 与值
     * @return
     */
    public static int addAndValue(int value, int andValue) {
        return value &= andValue;
    }

}
