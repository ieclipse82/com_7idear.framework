package com_7idear.framework.ext;

import android.text.TextPaint;
import android.text.style.URLSpan;

/**
 * 无下划线文本
 * @author ieclipse 19-12-10
 * @description
 */
public class NoUnderlineSpan
        extends URLSpan {

    private int mColor = -1; //颜色

    public NoUnderlineSpan(String url) {
        super(url);
    }

    /**
     * 设置颜色
     * @param color 颜色
     */
    public void setColor(int color) {
        mColor = color;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        if (mColor == -1) {
            ds.setColor(ds.linkColor);
        } else {
            ds.setColor(mColor);
        }
        ds.setUnderlineText(false);
    }
}
