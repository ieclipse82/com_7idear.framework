package com_7idear.framework.ext;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 先进先出缓存
 * @author ieclipse 19-12-10
 * @description
 */
public class FIFOCache<T>
        implements Serializable {

    private LinkedBlockingQueue<T> queue; //队列
    private int                    size; //队列大小

    public FIFOCache(int size) {
        this.queue = new LinkedBlockingQueue<T>();
        this.size = size > 0 ? size : 1;
    }

    /**
     * 设置队列大小
     * @param size 大小
     */
    public void setSize(int size) {
        this.size = size;
    }

    /**
     * 添加缓存
     * @param list 队列
     * @return
     */
    public boolean addCache(List<T> list) {
        if (list == null) return false;
        synchronized (list) {
            for (int i = 0, c = list.size(); i < c; i++) {
                addCache(list.get(i));
            }
            return true;
        }
    }

    /**
     * 添加缓存
     * @param cache 元素
     * @return
     */
    public boolean addCache(T cache) {
        if (queue.size() >= size) queue.poll();
        queue.offer(cache);
        return true;
    }

    /**
     * 获取第一个缓存
     * @return
     */
    public T getCache() {
        return queue.poll();
    }

    /**
     * 获取全部缓存
     * @return
     */
    public List<T> getAllCache() {
        List<T> list = new ArrayList<T>();
        Iterator<T> it = queue.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }

    /**
     * 删除全部缓存
     */
    public void removeAllCache() {
        queue.clear();
    }
}
