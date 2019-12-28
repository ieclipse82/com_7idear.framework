package com_7idear.framework.router;

import com_7idear.framework.BuildConfig;

/**
 * 全局代码标识类
 * @author iEclipse 2019/8/12
 * @description
 */
public interface IRouter {

    /** 通用Scheme */
    interface Scheme {
        String SCHEME_APP    = BuildConfig.APP_SCHEME;
        String SCHEME_HTTP   = "http";
        String SCHEME_HTTPS  = "https";
        String SCHEME_II     = "://";
        String SCHEME_APP_II = SCHEME_APP + SCHEME_II;
    }

    /** 通用Host */
    interface Host {
        /** intent序列化成URI */
        String INTENT_TO_URI = "intentToUri";

        /** 启动页面 */
        String INTENT = "Intent";

    }

    /** 通用Params */
    interface Params {
        /** 参数——链接 */
        String LINK        = "link";
        /** 参数——动作 */
        String ACTION      = "action";
        /** 参数——来源 */
        String REF         = "ref";
        /** 参数——ID（打开界面时，特定内容使用ID参数，例如请求链接？后面的参数值） */
        String ID          = "id";
        /** 参数——链接（打开界面时，请求网址使用URL参数，例如请求链接？前面的网址链接） */
        String URL         = "url";
        /** 参数——链接列表（打开界面时，请求网址使用URL参数，例如请求链接？前面的网址链接） */
        String URL_LIST    = "url_list";
        /** 参数——链接（打开界面时，请求搜索接口使用KEY参数） */
        String KEY         = "key";
        /** 参数——链接（打开界面时，显示特定内容使用TITLE参数） */
        String TITLE       = "title";
        /** 参数——后退退出（打开界面后，按后退键直接退回到原来的界面） */
        String BACK_FINISH = "back_finish";
        /** 参数——自动退出（打开界面后，无特殊动作自动后退到原来的界面） */
        String AUTO_FINISH = "auto_finish";
        /** 参数——返回新界面（打开界面后，后退需要打开的新界面） */
        String BACK_SCHEME = "back_scheme";
        /** 参数——数据（打开界面后，加载保存的数据） */
        String LOAD_ENTITY = "load_entity";
        /** 参数——执行特殊动作 */
        String RUN_ACTION  = "run_action";
        /** 参数——Ajax技术 */
        String MAP_KEY     = "map_key";
        /** 参数——显示的页面 */
        String SHOW_PAGE   = "show_page";
        /** 参数——显示的路径 */
        String SHOW_PATH   = "show_path";

        /** 参数——插件ID */
        String PLUGIN_ID = "plugin_id";
        /** 参数——cp ID */
        String VIDEO_CP  = "cp";

        String VIDEO_VID = "vid";
        /** 参数——详情页ID */
        String DETAIL_ID = "detail_id";

        String DEEP_LINK = "url";

        String NEW_DEEP_LINK = "deeplink";

        String TARGET_URL = "target_url";

        String POSITION = "position";

    }

    /** 通用RequestCode */
    interface RequestCode {
        int CHECK_APP_VERSION = 100;
    }

    /** 通用Result */
    interface Result {
        int CHECK_APP_VERSION = 1000;
    }

    /** 通用字体 */
    interface Font {
        String FONT_FZTYSJ = "font_fztysj.ttf";
    }

}
