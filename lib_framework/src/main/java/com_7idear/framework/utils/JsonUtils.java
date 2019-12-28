package com_7idear.framework.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON工具类
 * @author ieclipse 19-12-10
 * @description
 */
public class JsonUtils {

    /**
     * 获取JSON对象数据
     * @param bytes 字节数组
     * @return
     * @throws JSONException
     */
    public static JSONObject getJsonObjectData(byte[] bytes)
            throws JSONException {
        return new JSONObject(new String(bytes));
    }

    /**
     * 获取JSON数组数据
     * @param bytes 字节数组
     * @return
     * @throws JSONException
     */
    public static JSONArray getJsonArrayData(byte[] bytes)
            throws JSONException {
        return new JSONArray(new String(bytes));
    }

    /**
     * 获取JSON对象
     * @param jsonObject JSON对象
     * @param tag        标识
     * @return
     * @throws JSONException
     */
    public static JSONObject getJsonObject(JSONObject jsonObject, String tag)
            throws JSONException {
        if (jsonObject != null && jsonObject.has(tag)) return jsonObject.getJSONObject(tag);
        return null;
    }

    /**
     * 获取JSON数组
     * @param jsonObject JSON对象
     * @param tag        标识
     * @return
     * @throws JSONException
     */
    public static JSONArray getJsonArray(JSONObject jsonObject, String tag)
            throws JSONException {
        if (jsonObject != null && jsonObject.has(tag)) return jsonObject.getJSONArray(tag);
        return null;
    }

    /**
     * 是否为NULL
     * @param jsonObject JSON对象
     * @param tag        标识
     * @return
     */
    public static boolean isNotNull(JSONObject jsonObject, String tag) {
        if (jsonObject != null && jsonObject.has(tag) && !jsonObject.isNull(tag)) {
            return true;
        }
        return false;
    }

    /**
     * 获取INT
     * @param jsonObject JSON对象
     * @param tag        标识
     * @return
     */
    public static int getInt(JSONObject jsonObject, String tag) {
        return isNotNull(jsonObject, tag) ? jsonObject.optInt(tag) : 0;
    }

    /**
     * 获取LONG
     * @param jsonObject JSON对象
     * @param tag        标识
     * @return
     */
    public static long getLong(JSONObject jsonObject, String tag) {
        return isNotNull(jsonObject, tag) ? jsonObject.optLong(tag) : 0;
    }

    /**
     * 获取DOUBLE
     * @param jsonObject JSON对象
     * @param tag        标识
     * @return
     */
    public static double getDouble(JSONObject jsonObject, String tag) {
        return isNotNull(jsonObject, tag) ? jsonObject.optDouble(tag) : 0;
    }

    /**
     * 获取BOOLEAN
     * @param jsonObject JSON对象
     * @param tag        标识
     * @return
     */
    public static boolean getBoolean(JSONObject jsonObject, String tag) {
        return isNotNull(jsonObject, tag) ? jsonObject.optBoolean(tag) : false;
    }

    /**
     * 获取STRING
     * @param jsonObject JSON对象
     * @param tag        标识
     * @return
     */
    public static String getString(JSONObject jsonObject, String tag) {
        return isNotNull(jsonObject, tag) ? jsonObject.optString(tag) : "";
    }

    /**
     * 解析JSON对象
     * @param jsonObject JSON对象
     * @param tag        标识
     * @param listener   解析监听器
     * @return
     * @throws JSONException
     */
    public static <T> T parseJsonObject(JSONObject jsonObject, String tag,
            IParseJsonObjectOnlyListener<T> listener)
            throws JSONException {
        if (jsonObject != null && jsonObject.has(tag) && listener != null) {
            return listener.onParseJson(jsonObject.getJSONObject(tag));
        }
        return null;
    }

    /**
     * 解析JSON对象
     * @param jsonObject JSON对象
     * @param tag        标识
     * @param entity     原始数据
     * @param listener   解析监听器
     * @return
     * @throws JSONException
     */
    public static <T> T parseJsonObject(JSONObject jsonObject, String tag, T entity,
            IParseJsonObjectListener<T> listener)
            throws JSONException {
        if (jsonObject != null && jsonObject.has(tag) && listener != null) {
            return listener.onParseJson(jsonObject.getJSONObject(tag), entity);
        }
        return null;
    }

    /**
     * 解析JSON数组
     * @param jsonObject JSON对象
     * @param tag        标识
     * @param listener   解析监听器
     * @return
     * @throws JSONException
     */
    public static <T> T parseJsonArray(JSONObject jsonObject, String tag,
            IParseJsonArrayOnlyListener<T> listener)
            throws JSONException {
        if (jsonObject != null && jsonObject.has(tag) && listener != null) {
            return listener.onParseJson(jsonObject.getJSONArray(tag));
        }
        return null;
    }

    /**
     * 解析JSON数组
     * @param jsonObject JSON对象
     * @param tag        标识
     * @param entity     原始数据
     * @param listener   解析监听器
     * @return
     * @throws JSONException
     */
    public static <T> T parseJsonArray(JSONObject jsonObject, String tag, T entity,
            IParseJsonArrayListener<T> listener)
            throws JSONException {
        if (jsonObject != null && jsonObject.has(tag) && listener != null) {
            return listener.onParseJson(jsonObject.getJSONArray(tag), entity);
        }
        return entity;
    }

    /**
     * 解析JSON数组（循环）
     * @param jsonObject JSON对象
     * @param tag        标识
     * @param entity     原始数据
     * @param listener   解析监听器
     * @return
     * @throws JSONException
     */
    public static <T> T parseJsonObjectEach(JSONObject jsonObject, String tag, T entity,
            IParseJsonObjectEachListener<T> listener)
            throws JSONException {
        JSONArray array = getJsonArray(jsonObject, tag);
        if (array != null && listener != null) {
            for (int i = 0, c = array.length(); i < c; i++) {
                entity = listener.onParseJson(array.getJSONObject(i), entity, i);
            }
            return entity;
        }
        return entity;
    }

    /**
     * 解析JSON数组（循环）
     * @param jsonArray JSON数组
     * @param entity    原始数据
     * @param listener  解析监听器
     * @return
     * @throws JSONException
     */
    public static <T> T parseJsonArrayEach(JSONArray jsonArray, T entity,
            IParseJsonArrayEachListener<T> listener)
            throws JSONException {
        if (jsonArray != null && listener != null) {
            for (int i = 0, c = jsonArray.length(); i < c; i++) {
                entity = listener.onParseJson(jsonArray.get(i), entity, i);
            }
            return entity;
        }
        return entity;
    }

    /**
     * @author ieclipse 19-12-10
     * @description
     */
    public interface IParseJsonObjectOnlyListener<T> {
        /**
         * 解析JSON
         * @param jsonObject JSON对象
         * @return
         */
        T onParseJson(JSONObject jsonObject);
    }

    /**
     * @author ieclipse 19-12-10
     * @description
     */
    public interface IParseJsonObjectListener<T> {
        /**
         * 解析JSON
         * @param jsonObject JSON对象
         * @param entity     原始对象
         * @return
         */
        T onParseJson(JSONObject jsonObject, T entity);
    }

    /**
     * @author ieclipse 19-12-10
     * @description
     */
    public interface IParseJsonObjectEachListener<T> {
        /**
         * 解析JSON
         * @param jsonObject JSON对象
         * @param entity     原始对象
         * @param index      索引
         * @return
         */
        T onParseJson(JSONObject jsonObject, T entity, int index);
    }

    /**
     * @author ieclipse 19-12-10
     * @description
     */
    public interface IParseJsonArrayOnlyListener<T> {
        /**
         * 解析JSON
         * @param jsonArray JSON数组
         * @return
         */
        T onParseJson(JSONArray jsonArray);
    }

    /**
     * @author ieclipse 19-12-10
     * @description
     */
    public interface IParseJsonArrayListener<T> {
        /**
         * 解析JSON
         * @param jsonArray JSON数组
         * @param entity    原始对象
         * @return
         */
        T onParseJson(JSONArray jsonArray, T entity);
    }

    /**
     * @author ieclipse 19-12-10
     * @description
     */
    public interface IParseJsonArrayEachListener<T> {
        /**
         * 解析JSON
         * @param obj    对象
         * @param entity 原始对象
         * @param index  索引
         * @return
         */
        T onParseJson(Object obj, T entity, int index);
    }
}
