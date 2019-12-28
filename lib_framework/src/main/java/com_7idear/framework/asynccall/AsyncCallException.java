package com_7idear.framework.asynccall;

import java.io.IOException;

/**
 * 异步任务错误类
 * @author ieclipse 19-12-18
 * @description
 */
public class AsyncCallException
        extends IOException {

    public static final int ERRCODE_CANCELED        = 1;
    public static final int ERRCODE_UNKNOWN_NETWORK = 2;
    public static final int ERRCODE_UNKNOWN_HOST    = 2;
    public static final int ERRCODE_MALFORMED_URL   = 3;
    public static final int ERRCODE_SOCKET_TIMEOUT  = 4;
    public static final int ERRCODE_PROTOCOL        = 5;
    public static final int ERRCODE_IOEXCEPTION     = 99;

    private final int mErrCode;

    AsyncCallException(int errCode) {
        mErrCode = errCode;
    }
}
