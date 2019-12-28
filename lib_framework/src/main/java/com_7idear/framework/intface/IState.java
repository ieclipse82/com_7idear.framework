package com_7idear.framework.intface;

/**
 * 通用状态接口
 * @author ieclipse 19-12-16
 * @description 实现（0：未知、1：完成、2：准备好、3：执行中、4：重试、 5：取消
 * 10：失败、11：异常、12：网络未知、13：网址未知、14：服务器未知、15：服务器超时、16：超过最大协议信息）等状态
 */
public interface IState {

    /** 状态——0：未知 */
    int UNKNOWN   = 0;
    /** 状态——1：完成 */
    int FINISHED  = 1;
    /** 状态——2：准备好 */
    int READY     = 2;
    /** 状态——3：执行中 */
    int DOING     = 3;
    /** 状态——4：重试 */
    int RETRY     = 4;
    /** 状态——5：取消 */
    int CANCELLED = 5;
    /** 状态——6：刷新（数据或进度） */
    int REFRESH   = 6;

    /** 状态——10：失败 */
    int FAILED                = 10;
    /** 状态——11：异常 */
    int ERROR                 = 11;
    /** 状态——12：网络未知 */
    int ERROR_NETWORK_UNKNOWN = 12;
    /** 状态——13：网址未知 */
    int ERROR_URL_UNKNOWN     = 13;
    /** 状态——14：服务器未知 */
    int ERROR_SERVER_UNKNOWN  = 14;
    /** 状态——15：服务器超时 */
    int ERROR_SERVER_TIMEOUT  = 15;
    /** 状态——16：超过最大协议信息 */
    int ERROR_MAX_PROTOCOL    = 16;
}
