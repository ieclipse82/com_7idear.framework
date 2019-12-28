package com_7idear.framework.ext;


import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;

import androidx.annotation.NonNull;

/**
 * 先进先出队列
 * @author ieclipse 19-12-10
 * @description 自动弹出首个元素
 */
public class FIFOLinkedQueue<E>
        extends LinkedBlockingQueue<E> {

    public FIFOLinkedQueue() {
        super(Integer.MAX_VALUE);
    }

    public FIFOLinkedQueue(int capacity) {
        super(capacity);
    }

    public FIFOLinkedQueue(Collection<? extends E> c) {
        super(c);
    }

    /**
     * 添加元素（如果队列满，自动弹出首个元素）
     * @param e 元素
     * @return
     */
    public boolean offerWithPoll(@NonNull E e) {
        if (remainingCapacity() <= 0) {
            poll();
        }
        return super.offer(e);
    }

}
