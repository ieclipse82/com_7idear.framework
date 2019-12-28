package com_7idear.framework.support.similarcharacters;

import java.util.List;

/**
 * 相似字符列表实体类
 * @author ieclipse 19-12-10
 * @description
 */
public class SimilarCharactersList<T> {

    private String  key;
    private List<T> list;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
