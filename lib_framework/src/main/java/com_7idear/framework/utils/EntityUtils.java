package com_7idear.framework.utils;

import com_7idear.framework.log.LogUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;


/**
 * 实体对象工具类
 * @author ieclipse 19-12-10
 * @description 实现对象，列表，数据序列化和反序列化
 */
public class EntityUtils {

    /**
     * 是否为NULL
     * @param obj 对象
     * @return
     */
    public static boolean isNull(Object obj) {
        return obj == null;
    }

    /**
     * 是否任一对象为NULL
     * @param objs 对象数组
     * @return
     */
    public static boolean isNullOr(Object... objs) {
        if (objs == null) return true;
        for (int i = 0, c = objs.length; i < c; i++) {
            if (isNull(objs[i])) return true;
        }
        return false;
    }

    /**
     * 是否不为NULL
     * @param obj 对象
     * @return
     */
    public static boolean isNotNull(Object obj) {
        return !isNull(obj);
    }

    /**
     * 列表是否为空
     * @param list 列表
     * @return
     */
    public static boolean isEmpty(List<?> list) {
        return isNull(list) || list.isEmpty();
    }

    /**
     * 列表是否不为空
     * @param list 列表
     * @return
     */
    public static boolean isNotEmpty(List<?> list) {
        return !isEmpty(list);
    }

    /**
     * 列表是否不为空，并且索引是否有效
     * @param list  列表
     * @param index 索引
     * @return
     */
    public static boolean isNotEmpty(List<?> list, int index) {
        return !isEmpty(list) && index >= 0 && index < list.size();
    }

    /**
     * Map是否为空
     * @param map Map
     * @return
     */
    public static boolean isEmpty(Map<?, ?> map) {
        return isNull(map) || map.isEmpty();
    }

    /**
     * 序列化
     * @param obj 对象
     * @return
     */
    public static byte[] serializable(Object obj) {
        if (obj == null) return null;
        byte[] output = null;
        ByteArrayOutputStream bos = null;
        try {
            bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            oos.close();
            output = bos.toByteArray();
        } catch (IOException e) {
            LogUtils.catchException(e);
            output = null;
        } finally {
            if (bos != null) bos = null;
        }
        return output;
    }

    /**
     * 反序列化
     * @param bytes 字节数组
     * @return
     */
    public static Object unserializable(byte[] bytes) {
        if (bytes == null || bytes.length == 0) return null;
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            return new ObjectInputStream(bis).readObject();
        } catch (Exception e) {
            LogUtils.catchException(e);
        }
        return null;
    }

}
