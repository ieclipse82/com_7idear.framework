package com_7idear.framework.ext;


import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import androidx.collection.ArraySet;

/**
 * @author ieclipse 19-11-28
 * @description
 */
public class HashCodeArray<T> {

    private SparseArray<ArraySet<T>> mArray = new SparseArray();

    public synchronized boolean add(Object k, T v) {
        if (k == null || v == null) return false;
        final int code = k.hashCode();
        ArraySet<T> set = mArray.get(code);
        if (set == null) set = new ArraySet<>();
        set.add(v);
        mArray.put(code, set);
        return true;
    }

    public synchronized boolean remove(Object k, T v) {
        if (k == null || v == null) return false;
        final int code = k.hashCode();
        ArraySet<T> set = mArray.get(code);
        if (set == null) return false;
        set.remove(v);
        if (set.size() <= 0) {
            mArray.remove(code);
        }
        return true;
    }

    public synchronized List<T> get(Object k) {
        if (k == null) return null;
        final int code = k.hashCode();
        ArraySet<T> set = mArray.get(code);
        if (set == null) return null;
        List<T> list = new ArrayList<>();
        Iterator<T> it = set.iterator();
        while (it.hasNext()) {
            list.add(it.next());
        }
        return list;
    }
}
