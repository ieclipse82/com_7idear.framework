package com_7idear.framework.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * 权限工具类
 * @author ieclipse 19-12-5
 * @description 帮助APP获取相应运行时权限
 */
public class PermissionUtils {

    public static final String READ_PHONE_STATE       = Manifest.permission.READ_PHONE_STATE;
    public static final String GET_ACCOUNTS           = Manifest.permission.GET_ACCOUNTS;
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String ACCESS_FINE_LOCATION   = Manifest.permission.ACCESS_FINE_LOCATION;

    /**
     * 检查权限
     * @param context    环境对象
     * @param permission 权限名称
     * @return
     */
    public static boolean checkPermission(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        return true;
    }

    /**
     * 检查权限
     * @param context     环境对象
     * @param permissions 权限名称数组
     * @return
     */
    public static boolean checkPermission(Context context, String[] permissions) {
        if (permissions == null || permissions.length == 0) return false;
        for (String p : permissions) {
            if (!checkPermission(context, p)) return false;
        }
        return true;
    }

    /**
     * 申请权限
     * @param activity    当前页面
     * @param permission  权限名称
     * @param requestCode 请求代码
     */
    public static void requestPermissions(Activity activity, String permission, int requestCode) {
        requestPermissions(activity, new String[]{permission}, requestCode);
    }

    /**
     * 申请权限
     * @param activity    当前页面
     * @param permissions 权限名称数组
     * @param requestCode 请求代码
     */
    public static void requestPermissions(Activity activity, String[] permissions,
            int requestCode) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * 验证权限结果
     * @param permissions  权限名称数组
     * @param grantResults 权限值数组
     * @return
     */
    public static boolean onRequestPermissionsResult(String[] permissions, int[] grantResults) {
        if (permissions != null
                && grantResults != null
                && permissions.length > 0
                && permissions.length == grantResults.length) {
            for (int result : grantResults) {
                if (PackageManager.PERMISSION_GRANTED != result) return false;
            }
            return true;
        }
        return false;
    }

    /**
     * 验证权限结果（如果权限被禁止，帮助Activity再次申请权限，可能会造成无限循环，慎用）
     * @param activity     当前页面
     * @param requestCode  请求代码
     * @param permissions  权限名称数组
     * @param grantResults 权限值数组
     * @return
     */
    public static boolean onRequestPermissionsResult(Activity activity, int requestCode,
            String[] permissions, int[] grantResults) {
        if (permissions != null
                && grantResults != null
                && permissions.length > 0
                && permissions.length == grantResults.length) {
            for (int i = 0; i < grantResults.length; i++) {
                if (PackageManager.PERMISSION_GRANTED != grantResults[i]) {
                    if (shouldShowRequestPermissionRationale(activity, permissions[i])) {
                        requestPermissions(activity, permissions[i], requestCode);
                    }
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * 权限被禁止时是否需要提示
     * @param activity   当前页面
     * @param permission 权限名称
     * @return
     */
    public static boolean shouldShowRequestPermissionRationale(Activity activity,
            String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(activity, permission);
    }

    /**
     * 检查被禁止的权限名称（返回NULL代表没有权限被禁止）
     * @param permissions  权限名称数组
     * @param grantResults 权限值数组
     * @return
     */
    public static String checkPermissionsDenied(String[] permissions, int[] grantResults) {
        if (permissions != null
                && grantResults != null
                && permissions.length > 0
                && permissions.length == grantResults.length) {
            for (int i = 0; i < grantResults.length; i++) {
                if (PackageManager.PERMISSION_GRANTED != grantResults[i]) {
                    return permissions[i];
                }
            }
        }
        return null;
    }


}
