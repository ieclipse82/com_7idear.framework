package com_7idear.framework.core;


import com_7idear.framework.log.LogEntity;
import com_7idear.framework.log.LogUtils;

import java.util.HashMap;
import java.util.Map;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * UI页面管理类
 * @author ieclipse 19-11-29
 * @description 通过runFragment实现Fragment的添加、显示、隐藏、移除等
 */
public class UIFragment {

    /**
     * Fragment类型
     */
    public enum FragmentType {
        /**
         * 页面——0：全部删除
         */
        FRAGMENT_REMOVEALL,
        /**
         * 页面——1：添加 或替换或显示
         */
        FRAGMENT_SHOW,
        /**
         * 页面——2：隐藏
         */
        FRAGMENT_HIDE,
        /**
         * 页面——3：移除
         */
        FRAGMENT_REMOVE,
        /**
         * 页面——4：销毁
         */
        FRAGMENT_DETACH,
    }

    private FragmentManager        mFragmentManager; //页面管理对象
    private Map<Integer, Fragment> mFragmentMap; //页面数据

    public UIFragment(FragmentManager fragmentManager) {
        mFragmentManager = fragmentManager;
        mFragmentMap = new HashMap<Integer, Fragment>();
    }

    /**
     * 销毁
     */
    public void onDestroy() {
        if (mFragmentMap != null) mFragmentMap.clear();
        mFragmentMap = null;
        mFragmentManager = null;
    }

    /**
     * 执行页面机制
     * @param containerResId 容器资源ID
     * @param fragment       页面
     * @param type           操作类型
     * @param addToBackStack 是否添加到回退栈
     */
    public void runFragment(int containerResId, Fragment fragment, FragmentType type,
            boolean addToBackStack) {
        new LogEntity().append("containerResId", containerResId)
                       .append("fragment", fragment)
                       .append("type", type)
                       .append("addToBackStack", addToBackStack)
                       .toLogD("runFragment");
        try {
            FragmentTransaction ft = mFragmentManager.beginTransaction();
            switch (type) {
                case FRAGMENT_REMOVEALL:
                    mFragmentMap.clear();
                    break;
                case FRAGMENT_SHOW:
                    Fragment tmp = mFragmentManager.findFragmentById(containerResId);
                    if (tmp == null) {
                        ft.add(containerResId, fragment);
                    } else if (tmp != fragment) {
                        ft.replace(containerResId, fragment);
                    } else {
                        ft.show(mFragmentMap.get(containerResId));
                    }
                    if (addToBackStack) ft.addToBackStack(null);
                    mFragmentMap.put(containerResId, fragment);
                    break;
                case FRAGMENT_REMOVE:
                    if (mFragmentMap.get(containerResId) != null)
                        ft.remove(mFragmentMap.remove(containerResId));
                    break;
                case FRAGMENT_HIDE:
                    if (mFragmentMap.get(containerResId) != null)
                        ft.hide(mFragmentMap.get(containerResId));
                    break;
                case FRAGMENT_DETACH:
                    if (mFragmentMap.get(containerResId) != null)
                        ft.detach(mFragmentMap.remove(containerResId));
                    break;
                default:
                    break;
            }
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            LogUtils.catchException(e);
        }
    }

    /**
     * UI页面接口
     */
    public interface ImplUIFragmentListener {
        /**
         * 获取UI页面管理对象
         * @return
         */
        UIFragment getUIFragment();
    }
}
