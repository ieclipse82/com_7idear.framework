package com_7idear.framework.support.similarcharacters;

/**
 * 相似字符实体类
 * @author ieclipse 19-12-10
 * @description
 */
public class SimilarCharactersEntity<T> {

    private String key; //关键字
    private T      obj; //数据

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getObj() {
        return obj;
    }

    public void setObj(T obj) {
        this.obj = obj;
    }
}
