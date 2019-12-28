package com_7idear.framework.ext;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.ScaleXSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;

import com_7idear.framework.log.LogUtils;
import com_7idear.framework.utils.PatternUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 标签文本
 * @author ieclipse 19-12-10
 * @description 提供文本多种样式显示能力，输入类型全部、数值、字母、数值加字母等，内容类型下划线、删除线、网址、电话、邮箱、短信、彩信、位置等
 */
public class SpanText
        extends SpannableString {

    /**
     * 类型
     */
    public enum Type {
        /**
         * 隐藏——下划线
         */
        SHOW_UNDERLINE,
        /**
         * 显示——下划线
         */
        HIDE_UNDERLINE,
        /**
         * 隐藏——删除线
         */
        SHOW_STRIKETHROUGH,
        /**
         * 显示——删除线
         */
        HIDE_STRIKETHROUGH,
        /**
         * 显示——网址
         */
        SHOW_HTTP,
        /**
         * 显示——电话
         */
        SHOW_TEL,
        /**
         * 显示——邮箱
         */
        SHOW_MAIL,
        /**
         * 显示——短信
         */
        SHOW_SMS,
        /**
         * 显示——彩信
         */
        SHOW_MMS,
        /**
         * 显示——位置
         */
        SHOW_GEO,
    }

    /**
     * 显示——下划线
     */
    public static final String SHOW_UNDERLINE     = "underline";
    /**
     * 显示——删除线
     */
    public static final String SHOW_STRIKETHROUGH = "strikethrough";
    /**
     * 显示——网址
     */
    public static final String SHOW_HTTP          = "http:";
    /**
     * 显示——电话
     */
    public static final String SHOW_TEL           = "tel:";
    /**
     * 显示——邮箱
     */
    public static final String SHOW_MAIL          = "mailto:";
    /**
     * 显示——短信
     */
    public static final String SHOW_SMS           = "sms:";
    /**
     * 显示——彩信
     */
    public static final String SHOW_MMS           = "mms:";
    /**
     * 显示——位置
     */
    public static final String SHOW_GEO           = "geo:";

    /**
     * 文本类型——全部
     */
    public static final int STRTYPE_ALL         = 0;
    /**
     * 文本类型——数值
     */
    public static final int STRTYPE_NUMERICAL   = 1;
    /**
     * 文本类型——字母
     */
    public static final int STRTYPE_CHARACTER   = 2;
    /**
     * 文本类型——数值加字母
     */
    public static final int STRTYPE_WORD        = 3;
    /**
     * 文本类型——搜索文本
     */
    public static final int STRTYPE_SEARCH_TEXT = 4;

    public SpanText(CharSequence source) {
        super(source);
    }

    /**
     * 设置字体大小
     * @param start  起始位置
     * @param length 长度
     * @param scale  放大缩小倍数
     * @param size   大小
     * @param isDip  是否为DP显示
     * @return
     */
    public SpanText setSize(int start, int length, float scale, int size, boolean isDip) {
        if (length() < start + length) return this;
        if (scale > 0) {
            setSpan(new RelativeSizeSpan(scale), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (size > 0) {
            setSpan(new AbsoluteSizeSpan(size, isDip), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return this;
    }

    /**
     * 设置字体大小
     * @param strType 文本类型
     * @param scale   放大缩小倍数
     * @param size    大小
     * @param isDip   是否为DP显示
     * @return
     */
    public SpanText setSize(int strType, float scale, int size, boolean isDip) {
        String pattern = "";
        if (STRTYPE_NUMERICAL == strType) {
            pattern = "\\d";
        } else if (STRTYPE_CHARACTER == strType) {
            pattern = "\\D";
        } else if (STRTYPE_WORD == strType) {
            pattern = "\\w";
        } else {
            setSize(0, length(), scale, size, isDip);
            return this;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(this);
        while (matcher.find()) {
            int start = matcher.start(0);
            int length = matcher.end(0) - matcher.start(0);
            setSize(start, length, scale, size, isDip);
        }
        return this;
    }

    /**
     * 设置字体大小
     * @param searchTexts 搜索字符数组
     * @param scale       放大缩小倍数
     * @param size        大小
     * @param isDip       是否为DP显示
     * @return
     */
    public SpanText setSize(String[] searchTexts, float scale, int size, boolean isDip) {
        if (searchTexts == null) return this;
        StringBuffer pattern = new StringBuffer();
        for (int i = 0, c = searchTexts.length; i < c; i++) {
            pattern.setLength(0);
            pattern.append(searchTexts[i]);
            Pattern p = Pattern.compile(pattern.toString());
            Matcher matcher = p.matcher(this);
            while (matcher.find()) {
                int start = matcher.start(0);
                int length = matcher.end(0) - matcher.start(0);
                setSize(start, length, scale, size, isDip);
            }
        }
        return this;
    }

    /**
     * 设置字体宽度
     * @param start  起始位置
     * @param length 长度
     * @param scale  长度
     * @return
     */
    public SpanText setScale(int start, int length, float scale) {
        if (length() < start + length) return this;
        if (scale > 0) {
            setSpan(new ScaleXSpan(scale), start, start + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return this;
    }

    /**
     * 设置字体颜色
     * @param start    起始位置
     * @param length   长度
     * @param color    解析后的颜色值
     * @param colorTxt 颜色文本
     * @return
     */
    public SpanText setColor(int start, int length, int color, String colorTxt) {
        if (length() < start + length) return this;
        if (!TextUtils.isEmpty(colorTxt)) {
            setSpan(new ForegroundColorSpan(Color.parseColor(colorTxt)), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else {
            setSpan(new ForegroundColorSpan(color), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return this;
    }

    /**
     * 设置字体颜色
     * @param strType  文本类型
     * @param color    解析后的颜色值
     * @param colorTxt 颜色文本
     * @return
     */
    public SpanText setColor(int strType, int color, String colorTxt) {
        String pattern = "";
        if (STRTYPE_NUMERICAL == strType) {
            pattern = "\\d";
        } else if (STRTYPE_CHARACTER == strType) {
            pattern = "\\D";
        } else if (STRTYPE_WORD == strType) {
            pattern = "\\w";
        } else {
            setColor(0, length(), color, colorTxt);
            return this;
        }
        Pattern p = Pattern.compile(pattern);
        Matcher matcher = p.matcher(this);
        while (matcher.find()) {
            int start = matcher.start(0);
            int length = matcher.end(0) - matcher.start(0);
            setColor(start, length, color, colorTxt);
        }
        return this;
    }

    /**
     * 设置字体颜色
     * @param searchTexts 搜索字符数组
     * @param color       解析后的颜色值
     * @param colorTxt    颜色文本
     * @return
     */
    public SpanText setColor(String[] searchTexts, int color, String colorTxt) {
        if (searchTexts == null) return this;
        try {
            StringBuffer pattern = new StringBuffer();
            for (int i = 0, c = searchTexts.length; i < c; i++) {
                pattern.setLength(0);
                pattern.append(PatternUtils.replaceAllSpecial(searchTexts[i]));
                Pattern p = Pattern.compile(pattern.toString());
                Matcher matcher = p.matcher(this);
                while (matcher.find()) {
                    int start = matcher.start(0);
                    int length = matcher.end(0) - matcher.start(0);
                    setColor(start, length, color, colorTxt);
                }
            }
        } catch (Exception e) {
            LogUtils.catchException(e);
        }
        return this;
    }

    /**
     * 设置背景颜色
     * @param start    起始位置
     * @param length   长度
     * @param colorTxt 颜色文本
     * @param color    解析后的颜色值
     * @return
     */
    public SpanText setBgColor(int start, int length, String colorTxt, int color) {
        if (length() < start + length) return this;
        if (!TextUtils.isEmpty(colorTxt)) {
            setSpan(new BackgroundColorSpan(Color.parseColor(colorTxt)), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (color > 0) {
            setSpan(new BackgroundColorSpan(color), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return this;
    }

    /**
     * 设置图片
     * @param start    起始位置
     * @param length   长度
     * @param drawable 图片
     * @return
     */
    public SpanText setDrawable(int start, int length, Drawable drawable) {
        if (length() < start + length) return this;
        if (drawable != null) {
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            setSpan(new ImageSpan(drawable), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return this;
    }

    /**
     * 设置文本样式
     * @param start    起始位置
     * @param length   长度
     * @param typeface 文本样式
     * @return
     */
    public SpanText setStyle(int start, int length, int typeface) {
        if (length() < start + length) return this;
        switch (typeface) {
            case Typeface.BOLD:
                setSpan(new StyleSpan(Typeface.BOLD), start, start + length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case Typeface.ITALIC:
                setSpan(new StyleSpan(Typeface.ITALIC), start, start + length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            case Typeface.BOLD_ITALIC:
                setSpan(new StyleSpan(Typeface.BOLD_ITALIC), start, start + length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
            default:
                setSpan(new StyleSpan(Typeface.NORMAL), start, start + length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                break;
        }
        return this;
    }

    /**
     * 设置文本显示类型
     * @param start    起始位置
     * @param length   长度
     * @param showType 显示类型
     * @param content  内容
     * @return
     */
    public SpanText setShowType(int start, int length, String showType, String content) {
        if (length() < start + length) return this;
        if (SHOW_UNDERLINE.equals(showType)) {
            setSpan(new UnderlineSpan(), start, start + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (SHOW_STRIKETHROUGH.equals(showType)) {
            setSpan(new StrikethroughSpan(), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (SHOW_HTTP.equals(showType)) {
            setSpan(new URLSpan(content), start, start + length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (SHOW_TEL.equals(showType)) {
            setSpan(new URLSpan(SHOW_TEL + content), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (SHOW_MAIL.equals(showType)) {
            setSpan(new URLSpan(SHOW_MAIL + content), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (SHOW_SMS.equals(showType)) {
            setSpan(new URLSpan(SHOW_SMS + content), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (SHOW_MMS.equals(showType)) {
            setSpan(new URLSpan(SHOW_MMS + content), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        } else if (SHOW_GEO.equals(showType)) {
            setSpan(new URLSpan(SHOW_GEO + content), start, start + length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return this;
    }

    /**
     * 隐藏下划线
     * @param start  起始位置
     * @param length 长度
     * @param color  解析后的颜色值
     * @return
     */
    public SpanText hideUnderline(int start, int length, int color) {
        if (length() < start + length) return this;
        URLSpan[] urls = getSpans(0, length(), URLSpan.class);
        for (URLSpan url : urls) {
            NoUnderlineSpan s = new NoUnderlineSpan(url.getURL());
            s.setColor(color);
            setSpan(s, getSpanStart(url), getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        }
        return this;
    }

}
