package com_7idear.framework.theme;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;

import com_7idear.framework.R;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.log.LogUtils;
import com_7idear.framework.page.PageUtils;
import com_7idear.framework.utils.TxtUtils;


/**
 * 主题工具类（实现换肤功能）
 * @author DZH 2014年9月16日
 * @description 主应用和主题资源包中需要配置相同的android:sharedUserId值，并且资源文件strings.xml文件中都需要有R.string.theme_id字段值匹配才会换肤
 */
public class ThemeUtils {

    private static final String RES_ANIM      = "anim";
    private static final String RES_ARRAY     = "array";
    private static final String RES_ATTR      = "attr";
    private static final String RES_BOOL      = "bool";
    private static final String RES_COLOR     = "color";
    private static final String RES_DIMEN     = "dimen";
    private static final String RES_DRAWABLE  = "drawable";
    private static final String RES_ID        = "id";
    private static final String RES_INTEGER   = "integer";
    private static final String RES_LAYOUT    = "layout";
    private static final String RES_STRING    = "string";
    private static final String RES_STYLE     = "style";
    private static final String RES_XML       = "xml";
    private static final String RES_STYLEABLE = "styleable";

    private static ThemeUtils mInstance;

    private Context        mApplicationContext; //主应用环境对象
    private LayoutInflater mApplicationInflater; //主应用布局加载对象
    private Context        mThemeContext; //资源包环境对象
    private LayoutInflater mThemeInflater; //资源包布局加载对象
    private boolean        isApplyTheme; //是否换肤
    private String         mThemeID; //主题标识ID
    private String         mThemePackageName; //应用的主题包名

    public static ThemeUtils getInstance() {
        if (mInstance == null) {
            synchronized (ThemeUtils.class) {
                if (mInstance == null) {
                    mInstance = new ThemeUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 初始化
     * @param appContext APP环境对象
     * @param themeID    主题标识ID
     */
    public void init(Context appContext, String themeID) {
        mApplicationContext = appContext;
        mApplicationInflater = LayoutInflater.from(appContext);
        mThemeID = themeID;
    }

    /**
     * 启用主题
     * @param themePackageName 主题包名
     */
    public void enableTheme(String themePackageName) {
        if (TxtUtils.isEmpty(themePackageName) || TxtUtils.equals(mThemePackageName,
                themePackageName)) return;
        if (createPackageContext(themePackageName)) {
            PageUtils.getInstance().onThemeChanged(themePackageName);
        }
    }

    /**
     * 禁用主题
     */
    public void disableTheme() {
        if (createPackageContext(null)) {
            PageUtils.getInstance().onThemeChanged(mThemePackageName);
        }
    }

    public boolean isApplyTheme() {
        return isApplyTheme;
    }

    public String getThemeID() {
        return mThemeID;
    }

    public String getThemePackageName() {
        return mThemePackageName;
    }

    /**
     * 获取共享的主题界面包
     * @return
     */
    public String[] getThemeSupportPackages() {
        String[] list = mApplicationContext.getPackageManager().getPackagesForUid(Process.myUid());
        if (list != null) {
            LogEntity log = new LogEntity();
            for (int i = 0, c = list.length; i < c; i++) {
                log.appendLine("themePackageName", list[i]);
            }
            log.toLogD("getThemeSupportPackages");
        }
        return list;
    }

    /**
     * 建立资源包环境对象
     * @return
     */
    private boolean createPackageContext(String themePackageName) {
        if (TxtUtils.equals(themePackageName, mThemePackageName)) return false;
        if (TxtUtils.isEmptyOR(themePackageName, mThemeID)) {
            isApplyTheme = false;
            mThemeContext = null;
            mThemeInflater = null;
            mThemePackageName = null;
            toLog();
            return true;
        } else {
            try {
                final Context tmpContext = mApplicationContext.createPackageContext(
                        themePackageName, Context.CONTEXT_IGNORE_SECURITY);
                int resValue = getResourceValue(R.string.theme_id, RES_STRING);
                if (resValue != 0) {
                    String themeId = tmpContext.getResources().getString(resValue);
                    if (TxtUtils.equals(mThemeID, themeId)) {
                        isApplyTheme = true;
                        mThemeContext = tmpContext;
                        mThemeInflater = LayoutInflater.from(tmpContext);
                        mThemePackageName = themePackageName;
                        toLog();
                        return true;
                    }
                }
            } catch (NameNotFoundException e) {
                LogUtils.catchException(e);
            }
            toLog();
            return false;
        }
    }


    /**
     * 获取资源
     * @param resId   资源ID
     * @param resType 资源类型
     * @return
     */
    private int getResourceValue(int resId, String resType) {
        String resName = getResourceName(resId);
        resName = resName.substring(1 + resName.lastIndexOf("/"));
        if (!TxtUtils.isEmpty(resName)) {
            int id = 0;
            if (isApplyTheme) {
                id = mThemeContext.getResources()
                                  .getIdentifier(resName, resType, mThemeContext.getPackageName());
            } else {
                id = mApplicationContext.getResources()
                                        .getIdentifier(resName, resType,
                                                mApplicationContext.getPackageName());
            }
            if (id == 0) {
                new LogEntity().append("resId", resId)
                               .append("resName", resName)
                               .toLogW("getResourceValue failed");
            }
            return id;
        }
        return 0;
    }

    /**
     * 获取资源名称
     * @param resId 资源ID
     * @return
     */
    public String getResourceName(int resId) {
        return mApplicationContext.getResources().getResourceName(resId);
    }

    /**
     * 获取资源ID
     * @param resId   资源ID
     * @param resType 资源类型
     * @return
     */
    public int getResourceId(int resId, String resType) {
        if (isApplyTheme) {
            int resValue = getResourceValue(resId, resType);
            if (resValue != 0) {
                return resValue;
            }
        }
        return resId;
    }

    /**
     * 获取视图（注意，如果未获取到视图，证明当前界面未找到主题样式，返回为null，使用默认界面，需要Activity自行判断）
     * @param resId 资源ID
     * @return
     */
    public View getLayout(int resId) {
        if (isApplyTheme) {
            int resValue = getResourceValue(resId, RES_LAYOUT);
            if (resValue != 0) {
                return mThemeInflater.inflate(mThemeContext.getResources().getLayout(resValue),
                        null);
            }
        }
        return null;
        // return mApplicationInflater.inflate(resId, null);
    }

    /**
     * 获取ID
     * @param resId 资源ID
     * @return
     */
    public int getId(int resId) {
        if (isApplyTheme) {
            int resValue = getResourceValue(resId, RES_ID);
            if (resValue != 0) {
                return resValue;
            }
        }
        return resId;
    }

    /**
     * 获取字符串
     * @param resId 资源ID
     * @return
     */
    public String getString(int resId) {
        if (isApplyTheme) {
            int resValue = getResourceValue(resId, RES_STRING);
            if (resValue != 0) {
                return mThemeContext.getResources().getString(resValue);
            }
        }
        return mApplicationContext.getResources().getString(resId);
    }

    /**
     * 获取颜色
     * @param resId 资源ID
     * @return
     */
    public int getColor(int resId) {
        if (isApplyTheme) {
            int resValue = getResourceValue(resId, RES_COLOR);
            if (resValue != 0) {
                return mThemeContext.getResources().getColor(resValue);
            }
        }
        return mApplicationContext.getResources().getColor(resId);
    }

    /**
     * 获取图像
     * @param resId 资源ID
     * @return
     */
    public Drawable getDrawable(int resId) {
        if (isApplyTheme) {
            int resValue = getResourceValue(resId, RES_DRAWABLE);
            if (resValue != 0) {
                return mThemeContext.getResources().getDrawable(resValue);
            }
        }
        return mApplicationContext.getResources().getDrawable(resId);
    }

    public void toLog() {
        new LogEntity().appendLine("isApplyTheme", isApplyTheme)
                       .append("mThemeID", mThemeID)
                       .appendLine("mThemePackageName", mThemePackageName)
                       .append("mApplicationContext", mApplicationContext)
                       .appendLine("mApplicationInflater", mApplicationInflater)
                       .append("mThemeContext", mThemeContext)
                       .appendLine("mThemeInflater", mThemeInflater)
                       .toLogD("ThemeUtils");
    }

}
