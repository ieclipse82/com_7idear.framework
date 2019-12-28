package com_7idear.framework.task;

import com_7idear.framework.entity.FileEntity;
import com_7idear.framework.intface.IState;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.net.ConnectEntity;
import com_7idear.framework.utils.TxtUtils;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 任务实体对象
 * @author ieclipse 19-12-16
 * @description
 */
public class TaskEntity {

    /**
     * 任务组——0：自动
     */
    public static final int GROUP_AUTO = 0;
    /**
     * 任务组——0：一次
     */
    public static final int GROUP_ONCE = 1;

    private String action; //动作标识
    private String keepKey; //保持标识
    private int    type; //任务类型
    private int    level; //任务级别
    private int    state; //任务状态
    private int    group; //任务分组

    private ConnectEntity    conEntity; //联网对象
    private FileEntity fileEntity; //文件对象
    private List<FileEntity> fileList; //文件列表对象

    private ITask     taskListener; //任务返回监听器
    private ITaskToDo taskToDo; //任务TODO

    private Object inEntity; //输入对象
    private Object outEntity; //输出对象

    /**
     * 构造方法
     * @param action  标识
     * @param keepKey 保持标识
     * @param type    类型
     * @param level   级别
     * @param group   分组
     */
    public TaskEntity(String action, String keepKey, int type, int level, int group) {
        this.action = action;
        this.keepKey = TxtUtils.isEmpty(keepKey, "");
        this.type = type;
        this.level = level;
        this.state = IState.UNKNOWN;
        this.group = group;
    }

    /**
     * 获取动作标识
     * @return
     */
    public String getAction() {
        return action;
    }

    /**
     * 设置动作标识
     * @param action
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * 获取保持标识
     * @return
     */
    public String getKeepKey() {
        return keepKey;
    }

    /**
     * 设置保持标识
     * @param keepKey
     */
    public void setKeepKey(String keepKey) {
        this.keepKey = keepKey;
    }

    /**
     * 获取任务类型
     * @return
     */
    public int getType() {
        return type;
    }

    /**
     * 设置任务类型
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 获取任务级别
     * @return
     */
    public int getLevel() {
        return level;
    }

    /**
     * 设置任务级别
     * @param level
     */
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * 获取任务状态
     * @return
     */
    public int getState() {
        return state;
    }

    /**
     * 设置任务状态
     * @param state
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * 获取任务分组
     * @return
     */
    public int getGroup() {
        return group;
    }

    /**
     * 设置任务分组
     * @param group 分组
     */
    public void setGroup(int group) {
        this.group = group;
    }

    /**
     * 获取联网对象
     * @return
     */
    public ConnectEntity getConEntity() {
        return conEntity;
    }

    /**
     * 设置联网对象
     * @param conEntity
     */
    public void setConEntity(ConnectEntity conEntity) {
        this.conEntity = conEntity;
    }

    /**
     * 获取文件对象
     * @return
     */
    public FileEntity getFileEntity() {
        return fileEntity;
    }

    /**
     * 设置文件对象
     * @param fileEntity
     */
    public void setFileEntity(FileEntity fileEntity) {
        this.fileEntity = fileEntity;
    }

    /**
     * 获取文件列表对象
     * @return
     */
    public List<FileEntity> getFileList() {
        return fileList;
    }

    /**
     * 设置文件列表对象
     * @param fileList 文件列表
     */
    public void setFileList(List<FileEntity> fileList) {
        this.fileList = fileList;
    }

    /**
     * 获取任务返回监听器
     * @return
     */
    public ITask getTaskListener() {
        return taskListener;
    }

    /**
     * 设置任务返回监听器
     * @param taskListener
     */
    public void setTaskListener(ITask taskListener) {
        this.taskListener = taskListener;
    }

    /**
     * 获取任务TODO
     * @return
     */
    public ITaskToDo getTaskToDo() {
        return taskToDo;
    }

    /**
     * 设置任务TODO
     * @param taskToDo
     */
    public void setTaskToDo(ITaskToDo taskToDo) {
        this.taskToDo = taskToDo;
    }

    /**
     * 获取输入对象
     * @return
     */
    public Object getInEntity() {
        return inEntity;
    }

    /**
     * 设置输入对象
     * @param inEntity
     */
    public void setInEntity(Object inEntity) {
        this.inEntity = inEntity;
    }

    /**
     * 获取输出对象
     * @return
     */
    public Object getOutEntity() {
        return outEntity;
    }

    /**
     * 设置输出对象
     * @param outEntity
     */
    public void setOutEntity(Object outEntity) {
        this.outEntity = outEntity;
    }

    @NonNull
    @Override
    public String toString() {
        return new LogEntity().append("action", action)
                              .append("keepKey", keepKey)
                              .append("type", type)
                              .append("level", level)
                              .append("state", state)
                              .append("group", group)
                              .append("taskListener", taskListener)
                              .append("taskToDo", taskToDo)
                              .append("inEntity", inEntity)
                              .append("outEntity", outEntity)
                              .toString();
    }

}
