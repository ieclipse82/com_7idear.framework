package com_7idear.framework.support.foldertree;


import com_7idear.framework.utils.CacheUtils;
import com_7idear.framework.utils.EntityUtils;
import com_7idear.framework.utils.TxtUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * 文件树类
 * @author ieclipse 19-12-10
 * @description
 */
public class FolderTree<T> {

    private String              mBasePath;
    /** 节点列表 */
    private List<FolderNode<T>> mList = new ArrayList<FolderNode<T>>();

    public FolderTree(String basePath) {
        mBasePath = TxtUtils.isEmpty(basePath, "");
    }

    /**
     * 添加数据
     * @param path 路径
     * @param obj  对象
     * @return
     */
    public synchronized boolean addData(String path, T obj) {
        if (TxtUtils.isEmpty(path) || EntityUtils.isNull(obj)) return false;
        String parentPath = path.substring(0, path.lastIndexOf("/"));
        String key = TxtUtils.equals(CacheUtils.getSDCardPath(""), parentPath)
                ? mBasePath
                : TxtUtils.isEmpty(parentPath, "");
        for (int i = 0, c = mList.size(); i < c; i++) {
            FolderNode tmp = mList.get(i);
            if (key.equals(tmp.getPath())) {
                tmp.getList().add(obj);
                return true;
            }
        }
        FolderNode node = new FolderNode();
        node.setPath(key);
        node.getList().add(obj);
        mList.add(node);
        return true;
    }

    /**
     * 获取全部列表数据
     * @return
     */
    public List<FolderNode<T>> getAllList() {
        return mList;
    }

}
