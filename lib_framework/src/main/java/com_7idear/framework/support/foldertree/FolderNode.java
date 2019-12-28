package com_7idear.framework.support.foldertree;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件节点类
 * @author ieclipse 19-12-10
 * @description
 */
public class FolderNode<T> {

    private String  path; //路径
    private List<T> list = new ArrayList<T>(); //数据

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
