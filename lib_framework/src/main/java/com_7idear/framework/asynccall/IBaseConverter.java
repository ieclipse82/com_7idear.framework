package com_7idear.framework.asynccall;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 基础解析器接口（自定义解析器时需要继承此类）
 * @author ieclipse 19-12-26
 * @description 通过继承此类实现自定义数据解析器，{@link AsyncCallConverterFactory}
 */
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface IBaseConverter {
    /**
     * 解析器类
     * @return
     */
    Class<?> className();

    /**
     * 构造器参数中的全部类
     * @return
     */
    Class<?>[] paramClass() default {};

    /**
     * 构造器参数中的全部接口
     * @return
     */
    Class<?>[] paramClassInterface() default {};
}