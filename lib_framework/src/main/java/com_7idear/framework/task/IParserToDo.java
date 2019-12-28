package com_7idear.framework.task;

/**
 * 解析接口
 * @author ieclipse 19-12-16
 * @description 需要实现自定义数据解析
 */
public interface IParserToDo<In, Out>
        extends ITaskToDo {

    /**
     * 执行解析
     * @param action   动作标识
     * @param bytes    字节数组
     * @param inEntity 输入对象
     * @return
     */
    Out runParser(String action, byte[] bytes, In inEntity, ImplTaskHelper helper);
}
