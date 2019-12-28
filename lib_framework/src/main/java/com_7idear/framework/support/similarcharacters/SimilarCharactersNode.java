package com_7idear.framework.support.similarcharacters;

import android.util.SparseArray;

import com_7idear.framework.log.LogEntity;
import com_7idear.framework.utils.EntityUtils;
import com_7idear.framework.utils.TxtUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;


/**
 * 相似字符节点类
 * @author ieclipse 19-12-10
 * @description
 */
public class SimilarCharactersNode<T> {

    private SimilarCharactersEntity<T>             mKeyEntity;
    private LinkedList<SimilarCharactersEntity<T>> mKeyList;
    /** 节点字符权重 */
    private int[]                                  mNodeCount;
    /** 节点匹配数组 */
    private SparseArray<SimilarCharactersList<T>>  mNodeArray;

    public SimilarCharactersNode() {
        mKeyList = new LinkedList<SimilarCharactersEntity<T>>();
    }

    public boolean addKey(String key, T obj) {
        if (TxtUtils.isEmpty(key) || obj == null) return false;
        if (mKeyEntity == null) {
            mKeyEntity = new SimilarCharactersEntity();
            mKeyEntity.setKey(key);
            mKeyEntity.setObj(obj);
        } else {
            int index = getMatchIndex(mKeyEntity.getKey(), key);
            if (index < 2) return false;
            if (mKeyEntity.getKey().length() < key.length()) {
                mKeyEntity.setKey(key);
                mKeyEntity.setObj(obj);
            }
        }
        SimilarCharactersEntity tmp = new SimilarCharactersEntity();
        tmp.setKey(key);
        tmp.setObj(obj);
        mKeyList.add(tmp);
        return true;
    }

    public synchronized void runSortNode() {
        if (mNodeCount == null) {
            mNodeCount = new int[mKeyEntity.getKey().length()];
        } else {
            mNodeCount = Arrays.copyOf(mNodeCount, mKeyEntity.getKey().length());
        }
        if (mNodeArray == null) mNodeArray = new SparseArray<SimilarCharactersList<T>>();
        mNodeArray.clear();
        for (SimilarCharactersEntity<T> tmp : mKeyList) {
            addNoteEntity(tmp);
        }
    }

    public boolean addNoteEntity(SimilarCharactersEntity<T> entity) {
        int index = getMatchIndex(mKeyEntity.getKey(), entity.getKey());
        if (index >= 0 && index < mKeyEntity.getKey().length()) {
            mNodeCount[index]++;
            SimilarCharactersList<T> list = mNodeArray.get(index);
            if (EntityUtils.isNull(list)) list = new SimilarCharactersList<T>();
            if (EntityUtils.isEmpty(list.getList())) list.setList(new ArrayList<T>());
            list.setKey(mKeyEntity.getKey().substring(0, index + 1));
            list.getList().add(entity.getObj());
            mNodeArray.put(index, list);
            new LogEntity().append("getKey", entity.getKey())
                           .append("index", index)
                           .append("mNodeCount", mNodeCount[index])
                           .toLogD("addNoteEntity");
            return true;
        }
        return false;
    }

    /**
     * 获取匹配索引
     * @param src 源数据
     * @param key 关键字
     * @return
     */
    private int getMatchIndex(String src, String key) {
        for (int i = 0, c = key.length(); i < c; i++) {
            if (i >= src.length() || src.charAt(i) != key.charAt(i)) {
                return i - 1;
            }
        }
        return key.length() - 1;
    }

    /**
     * 获取全匹配列表数据
     * @return
     */
    public SimilarCharactersList<T> getAllMatchList() {
        runSortNode();
        SimilarCharactersList<T> row = new SimilarCharactersList<T>();
        row.setList(new ArrayList<T>());
        for (int i = 0, c = mKeyEntity.getKey().length(); i < c; i++) {
            SimilarCharactersList<T> tmp = mNodeArray.get(i);
            if (EntityUtils.isNotNull(tmp) && EntityUtils.isNotEmpty(tmp.getList())) {
                if (TxtUtils.isEmpty(row.getKey()) || row.getKey().length() > tmp.getKey()
                                                                                 .length()) {
                    row.setKey(tmp.getKey());
                }
                row.getList().addAll(tmp.getList());
            }
        }
        new LogEntity().append("size", row.getList().size()).toLogD("getAllMatchList");
        return row;
    }

    /**
     * 获取最多匹配列表数据
     * @return
     */
    public List<SimilarCharactersList<T>> getMostMatchList() {
        runSortNode();
        List<SimilarCharactersList<T>> list = new ArrayList<SimilarCharactersList<T>>();
        int last = mKeyEntity.getKey().length();
        int end = mKeyEntity.getKey().length();
        while (last > 0) {
            SimilarCharactersList<T> row = new SimilarCharactersList<T>();
            row.setList(new ArrayList<T>());
            int mostCount = 0;
            int start = 0;
            for (int i = 0; i < last; i++) {
                if (mostCount < mNodeCount[i]) {
                    mostCount = mNodeCount[i];
                    start = i;
                }
            }
            if (mostCount == 0) break;
            last = start;
            for (int i = end - 1; i >= start; i--) {
                SimilarCharactersList<T> tmp = mNodeArray.get(i);
                if (EntityUtils.isNotNull(tmp) && EntityUtils.isNotEmpty(tmp.getList())) {
                    if (TxtUtils.isEmpty(row.getKey()) || row.getKey().length() > tmp.getKey()
                                                                                     .length()) {
                        row.setKey(tmp.getKey());
                    }
                    row.getList().addAll(tmp.getList());
                }
            }
            list.add(row);
            end = start;
        }
        new LogEntity().append("size", list.size()).toLogD("getMostMatchList");
        return list;
    }

    public List<SimilarCharactersList<T>> getMoreMatchList() {
        runSortNode();
        List<SimilarCharactersList<T>> list = new ArrayList<SimilarCharactersList<T>>();
        int length = mKeyEntity.getKey().length();
        int start = 0;
        while (start < length) {
            SimilarCharactersList<T> row = new SimilarCharactersList<T>();
            row.setList(new ArrayList<T>());
            int end = 0;
            int count = 0;
            for (int i = start; i < length; i++) {
                if (mNodeCount[i] > 0) {
                    end = i + 1;
                    if (i == 0) {
                        break;
                    } else if (count == 0) {
                        count = mNodeCount[i];
                    } else if (count < mNodeCount[i]) {
                        end = i;
                        break;
                    }
                }
            }

            if (end == 0) break;
            for (int i = start; i < end; i++) {
                SimilarCharactersList<T> tmp = mNodeArray.get(i);
                if (EntityUtils.isNotNull(tmp) && EntityUtils.isNotEmpty(tmp.getList())) {
                    if (TxtUtils.isEmpty(row.getKey()) || row.getKey().length() > tmp.getKey()
                                                                                     .length()) {
                        row.setKey(tmp.getKey());
                    }
                    row.getList().addAll(tmp.getList());
                }
            }
            list.add(row);

            start = end;

        }
        return list;
    }

}
