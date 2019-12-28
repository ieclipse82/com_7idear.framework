package com_7idear.framework.page;

/**
 * 页面工厂接口
 * @author ieclipse 19-12-2
 * @description
 */
public interface IPageFactory {

    /**
     * 创建页面对象
     * @param cla 页面类型
     * @return
     */
    PageEntity createPageEntity(Class cla);
}
