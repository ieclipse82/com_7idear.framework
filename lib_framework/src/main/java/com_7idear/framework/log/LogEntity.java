package com_7idear.framework.log;

import com_7idear.framework.intface.IFormat;

/**
 * 日志实体类
 * @author iEclipse 2019/7/2
 * @description
 */
public final class LogEntity
        implements IFormat {

    private boolean       isLog;
    private StringBuilder mSb = new StringBuilder();

    public LogEntity() {
        this.isLog = true;
    }

    public LogEntity(boolean isLog) {
        this.isLog = isLog;
    }

    public LogEntity append(Object v) {
        if (v == null) return this;
        mSb.append(_T).append(v);
        return this;
    }

    public LogEntity append(String k, Object v) {
        if (k == null || v == null) return this;
        mSb.append(_T).append(k).append(" = ").append(v);
        return this;
    }

    public LogEntity append(String k, String[] vs) {
        if (k == null || vs == null) return this;
        StringBuilder s = new StringBuilder();
        for (int i = 0, c = vs.length; i < c; i++) {
            s.append(",").append(vs[i]);
        }
        mSb.append(_T).append(k).append(" = ").append(s.substring(1));
        return this;
    }

    public LogEntity appendAnd(String... vs) {
        if (vs == null || vs.length == 0) return this;
        mSb.append(_T);
        for (int i = 0; i < vs.length; i++) {
            if (vs[i] != null) mSb.append(vs[i]);
        }
        return this;
    }

    public LogEntity appendLine() {
        mSb.append(_R_N);
        return this;
    }

    public LogEntity appendLine(Object v) {
        if (v == null) return this;
        append(v);
        mSb.append(_R_N);
        return this;
    }

    public LogEntity appendLine(String k, Object v) {
        if (k == null || v == null) return this;
        append(k, v);
        mSb.append(_R_N);
        return this;
    }

    public LogEntity appendLine(String k, String[] vs) {
        if (k == null || vs == null) return this;
        append(k, vs);
        mSb.append(_R_N);
        return this;
    }

    public void toLogD() {
        toLogD(null, 5);
    }

    public void toLogD(String tag) {
        toLogD(tag, 5);
    }

    public void toLogD(String tag, int deep) {
        if (isLog) LogUtils.runShowLog(LogUtils.LOG_D, tag, mSb.toString(), deep);
    }

    public void toLogW() {
        toLogW(null, 5);
    }

    public void toLogW(String tag) {
        toLogW(tag, 5);
    }

    public void toLogW(String tag, int deep) {
        if (isLog) LogUtils.runShowLog(LogUtils.LOG_W, tag, mSb.toString(), deep);
    }

    @Override
    public String toString() {
        return mSb.toString();
    }
}
