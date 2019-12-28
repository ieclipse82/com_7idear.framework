package com_7idear.framework.page;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com_7idear.framework.core.ImplBaseActivity;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.router.IRouterFilter;
import com_7idear.framework.router.RouterUtils;

import java.util.LinkedList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;

/**
 * 页面工具类
 * @author ieclipse 19-11-29
 * @description 用于页面管理及刷新
 */
public class PageUtils
        implements IPageFactory {

    private static final int MSG_RUN_ACTION    = 1; //执行动作
    private static final int MSG_ON_UI_REFRESH = 2; //UI刷新
    private static final int MSG_THEME_CHANGED = 3; //主题变换

    private static PageUtils mInstance;

    private List<ImplBaseActivity> mUIList; //刷新界面列表

    public PageUtils() {
        if (mUIList == null) mUIList = new LinkedList<ImplBaseActivity>();
        mUIList.clear();
    }

    public static PageUtils getInstance() {
        if (mInstance == null) {
            synchronized (PageUtils.class) {
                if (mInstance == null) {
                    mInstance = new PageUtils();
                }
            }
        }
        return mInstance;
    }

    /**
     * 添加界面接口
     * @param ui 界面接口
     * @return
     */
    public synchronized boolean addUI(ImplBaseActivity ui) {
        if (ui == null || mUIList.contains(ui)) return false;
        new LogEntity().append("ui", ui).toLogD("addUI");
        mUIList.add(ui);
        return true;
    }

    /**
     * 删除界面接口
     * @param ui 界面接口
     * @return
     */
    public synchronized boolean removeUI(ImplBaseActivity ui) {
        if (ui == null || !mUIList.contains(ui)) return false;
        new LogEntity().append("ui", ui).toLogD("removeUI");
        mUIList.remove(ui);
        return true;
    }

    /**
     * 删除全部界面接口
     */
    public synchronized void removeUI() {
        new LogEntity().append("removeUI", "all").toLogD("removeUI");
        mUIList.clear();
    }

    /**
     * 执行界面动作
     * @param action 动作标识
     * @param what   动作标识
     * @param obj    实体对象
     */
    public synchronized void runUIAction(String action, int what, Object obj) {
        new LogEntity().append("action", action)
                       .append("what", what)
                       .append("obj", obj)
                       .toLogD("runUIAction");
        runUIMessage(MSG_RUN_ACTION, action, what, obj);
    }

    /**
     * 刷新界面
     * @param action 动作标识
     * @param what   动作标识
     * @param obj    实体对象
     */
    public synchronized void runUIRefresh(final String action, final int what, final Object obj) {
        new LogEntity().append("action", action)
                       .append("what", what)
                       .append("obj", obj)
                       .toLogD("runUIRefresh");
        runUIMessage(MSG_ON_UI_REFRESH, action, what, obj);
    }

    /**
     * 执行界面销毁
     */
    public synchronized void runUIFinish() {
        new LogEntity().append("runUIFinish", "all").toLogD("runUIFinish");
        for (int i = 0, c = mUIList.size(); i < c; i++) {
            ImplBaseActivity ui = mUIList.get(i);
            if (ui instanceof Activity) {
                ((Activity) ui).finish();
            } else if (ui instanceof FragmentActivity) {
                ((FragmentActivity) ui).finish();
            }
        }
    }

    /**
     * 执行界面销毁除了当前界面
     * @param exceptUI 当前界面
     */
    public synchronized void runUIFinishExcept(ImplBaseActivity exceptUI) {
        new LogEntity().append("exceptUI", "exceptUI").toLogD("runUIFinishExcept");
        for (int i = 0, c = mUIList.size(); i < c; i++) {
            ImplBaseActivity ui = mUIList.get(i);
            if (ui == exceptUI) {
                continue;
            } else if (ui instanceof Activity) {
                ((Activity) ui).finish();
            } else if (ui instanceof FragmentActivity) {
                ((FragmentActivity) ui).finish();
            }
        }
    }

    /**
     * 主题切换
     * @param themePackageName 主题名称
     */
    public synchronized void onThemeChanged(String themePackageName) {
        new LogEntity().append("themePackageName", "themePackageName").toLogD("onThemeChanged");
        runUIMessage(MSG_THEME_CHANGED, null, 0, themePackageName);
    }

    /**
     * 执行UI消息
     * @param msgAction 消息标识
     * @param action    动作标识
     * @param what      动作标识（用于Handler刷新）
     * @param obj       数据
     * @return
     */
    private Message runUIMessage(int msgAction, String action, int what, Object obj) {
        Message msg = mHandler.obtainMessage();
        msg.what = msgAction;
        msg.obj = new UIEntity(action, what, obj);
        return msg;
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper()) {
        public void handleMessage(Message msg) {
            UIEntity entity = (UIEntity) msg.obj;
            if (entity == null) return;
            switch (msg.what) {
                case MSG_RUN_ACTION:
                    synchronized (mUIList) {
                        for (ImplBaseActivity tmp : mUIList) {
                            tmp.runBaseAction(entity.action, entity.what, entity.obj);
                        }
                    }
                    break;
                case MSG_ON_UI_REFRESH:
                    synchronized (mUIList) {
                        for (ImplBaseActivity tmp : mUIList) {
                            tmp.onBaseUIRefresh(entity.action, entity.what, entity.obj);
                        }
                    }
                    break;
                case MSG_THEME_CHANGED:
                    synchronized (mUIList) {
                        for (ImplBaseActivity tmp : mUIList) {
                            tmp.onBaseThemeChanged((String) entity.obj);
                        }
                    }
                    break;
            }
        }
    };

    @Override
    public PageEntity createPageEntity(Class cla) {
        final List<IRouterFilter> filters = RouterUtils.getInstance().getFilters();
        for (IRouterFilter filter : filters) {
            if (filter instanceof IPageFactory) {
                PageEntity page = ((IPageFactory) filter).createPageEntity(cla);
                if (page != null) return page;
            }
        }
        return null;
    }

    private class UIEntity {
        private String action;
        private int    what;
        private Object obj;

        UIEntity(String action, int what, Object obj) {
            this.action = action;
            this.what = what;
            this.obj = obj;
        }
    }

}
