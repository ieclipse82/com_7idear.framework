package com_7idear.framework.asynccall;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * 异步基础任务解析类
 * @author ieclipse 19-12-26
 * @description 需要实现解析方法
 */
public abstract class AsyncBaseConverter<T>
        implements Converter<ResponseBody, T> {
    @Override
    final public T convert(ResponseBody value)
            throws IOException {
        return convert(value.string());
    }

    /**
     * 解析数据
     * @param responseBody 返回的字符串数据
     * @return
     */
    protected abstract T convert(String responseBody);
}
