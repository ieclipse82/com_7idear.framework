package com_7idear.framework.page;


import com_7idear.framework.core.BaseData;
import com_7idear.framework.core.UIFragment;
import com_7idear.framework.core.UIHandler;

/**
 * 基础页面接口
 * @author ieclipse 19-11-29
 * @description
 */
public interface ImplBasePage<D extends BaseData>
        extends ImplBasePageEntity<D>, UIHandler.ImplUIHandlerListener,
                UIFragment.ImplUIFragmentListener {

    /**
     * 创建页面对象
     * @return
     */
    PageEntity<D> createBasePage();

    /**
     * 获取页面对象
     * @return
     */
    PageEntity<D> getPageEntity();

}
