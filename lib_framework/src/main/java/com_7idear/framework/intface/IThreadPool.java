package com_7idear.framework.intface;

/**
 * 线程池接口
 * @author ieclipse 19-12-16
 * @description
 */
public interface IThreadPool {

    /**
     * CPU核心数量
     */
    int CPU_COUNT         = Runtime.getRuntime().availableProcessors();
    /**
     * 线程数量
     */
    int CORE_POOL_SIZE    = CPU_COUNT + 1;
    /**
     * 线程最大数量
     */
    int MAXIMUM_POOL_SIZE = CPU_COUNT * 8 + 1;
    /**
     * 保存活跃时间
     */
    int KEEP_ALIVE        = 1;
    /**
     * 线程容量
     */
    int CAPACITY          = 1 << 8;
}
