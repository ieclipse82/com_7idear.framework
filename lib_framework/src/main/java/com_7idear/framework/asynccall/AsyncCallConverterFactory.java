package com_7idear.framework.asynccall;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * 异步任务解析器工厂
 * @author ieclipse 19-12-26
 * @description 实现返回的数据自定义解析能力
 */
public class AsyncCallConverterFactory
        extends Converter.Factory {

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
            Retrofit retrofit) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof IBaseConverter) {
                IBaseConverter converter = (IBaseConverter) annotation;
                Class className = converter.className();
                Class[] paramClass = converter.paramClass();
                Class[] paramClassInterface = converter.paramClassInterface();
                try {
                    if (paramClass.length <= 0) {
                        return (Converter<ResponseBody, ?>) className.newInstance();
                    } else {
                        Constructor c = null;
                        if (paramClassInterface.length > 0) {
                            c = className.getDeclaredConstructor(paramClassInterface);
                        } else {
                            c = className.getDeclaredConstructor(paramClass);
                        }
                        c.setAccessible(true);
                        Object[] paramObjs = new Object[paramClass.length];
                        for (int i = 0; i < paramClass.length; i++) {
                            paramObjs[i] = paramClass[i].newInstance();
                        }
                        return (Converter<ResponseBody, ?>) c.newInstance(paramObjs);
                    }
                } catch (InstantiationException e) {
                    e.printStackTrace();
                    return null;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    return null;
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                    return null;
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
}
