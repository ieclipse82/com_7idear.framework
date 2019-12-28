package com_7idear.framework.support.similarcharacters;

import com_7idear.framework.utils.TxtUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 相似字符树类
 * @author ieclipse 19-12-10
 * @description
 */
public class SimilarCharactersTree<T> {

    /** 节点列表 */
    private List<SimilarCharactersNode> mList = new ArrayList<SimilarCharactersNode>();

    /**
     * 添加数据
     * @param key 关键字
     * @param obj 对象
     * @return
     */
    public synchronized boolean addData(String key, T obj) {
        if (TxtUtils.isEmpty(key)) return false;
        for (int i = 0, c = mList.size(); i < c; i++) {
            if (mList.get(i).addKey(key, obj)) {
                return true;
            }
        }
        SimilarCharactersNode node = new SimilarCharactersNode();
        node.addKey(key, obj);
        mList.add(node);
        return true;
    }

    /**
     * 获取全部列表数据
     * @return
     */
    public List<T> getAllList() {
        List<T> list = new ArrayList<T>();
        for (int i = 0, c = mList.size(); i < c; i++) {
            list.addAll(mList.get(i).getAllMatchList().getList());
        }
        return list;
    }

    /**
     * 获取全匹配列表数据
     * @return
     */
    public List<SimilarCharactersList<T>> getAllMatchList() {
        List<SimilarCharactersList<T>> list = new ArrayList<SimilarCharactersList<T>>();
        for (int i = 0, c = mList.size(); i < c; i++) {
            list.add(mList.get(i).getAllMatchList());
        }
        return list;
    }

    /**
     * 获取最多匹配列表数据
     * @return
     */
    public List<SimilarCharactersList<T>> getMostMatchList() {
        List<SimilarCharactersList<T>> list = new ArrayList<SimilarCharactersList<T>>();
        for (int i = 0, c = mList.size(); i < c; i++) {
            list.addAll(mList.get(i).getMostMatchList());
        }
        return list;
    }

    public List<SimilarCharactersList<T>> getMoreMatchList() {
        List<SimilarCharactersList<T>> list = new ArrayList<SimilarCharactersList<T>>();
        for (int i = 0, c = mList.size(); i < c; i++) {
            list.addAll(mList.get(i).getMoreMatchList());
        }
        return list;
    }

}
