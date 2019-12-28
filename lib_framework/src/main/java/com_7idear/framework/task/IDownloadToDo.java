package com_7idear.framework.task;

import com_7idear.framework.entity.FileEntity;

import java.io.IOException;
import java.io.InputStream;


/**
 * 下载接口监听器
 * @author ieclipse 19-12-16
 * @description
 */
public interface IDownloadToDo<In, Out>
        extends ITaskToDo {

    /**
     * 执行下载文件在后台线程
     * @param action     动作标识
     * @param fileEntity 文件实体对象
     * @param is         返回流
     * @param inEnitty   输入对象
     * @param helper
     * @return
     * @throws IOException
     */
    Out runDownload(String action, FileEntity fileEntity, InputStream is, In inEnitty,
            ImplTaskHelper helper)
            throws IOException;
}
