package com_7idear.framework.utils;

import com_7idear.framework.log.LogEntity;

/**
 * 异常工具类
 * @author ieclipse 19-12-25
 * @description 支持抛出异常，输出错误日志
 */
public class ExceptionUtils {

    public static void isNullThrowException(Object obj, String errorMsg) {
        if (obj == null) {
            new LogEntity().append("isNullThrowException", errorMsg).toLogD(null, 6);
            throw new RuntimeException(errorMsg);
        }
    }
}
