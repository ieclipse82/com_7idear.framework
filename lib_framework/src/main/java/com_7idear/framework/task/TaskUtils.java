package com_7idear.framework.task;

import com_7idear.framework.config.TaskConfig;
import com_7idear.framework.entity.FileEntity;
import com_7idear.framework.intface.IFormat;
import com_7idear.framework.intface.IState;
import com_7idear.framework.intface.IThreadPool;
import com_7idear.framework.net.ConnectEntity;
import com_7idear.framework.net.ConnectUtils;
import com_7idear.framework.utils.EntityUtils;
import com_7idear.framework.utils.SDKUtils;
import com_7idear.framework.utils.TxtUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * 任务调度工具类
 * @author ieclipse 19-12-16
 * @description 所有任务都会打开线程执行（请求，下载，后台任务等）
 */
public final class TaskUtils
        extends TaskConfig
        implements IState {

    private static final String TAG = "TaskUtils";

    /**
     * 任务级别——0：核心CPU数
     */
    public static final int TASKLEVEL_CORE_CPU = 0;
    /**
     * 任务级别——1：最大CPU数
     */
    public static final int TASKLEVEL_MAX_CPU  = 1;
    /**
     * 任务级别——2：单一线程
     */
    public static final int TASKLEVEL_SERIAL   = 2;

    private static TaskUtils mInstance;

    private int                mTaskLevel; //全局任务级别
    private String             mCachePath; //全局任务缓存目录
    private Executor           mExecutor; //执行器对象
    private List<DownloadTask> mTask; //任务执行队列
    private List<TaskEntity>   mTaskList; //任务队列
    private int                mLimitTaskCount; //任务限制数量

    @Override
    protected boolean init(int taskLevel, String cachePath) {
        mTaskLevel = taskLevel;
        mCachePath = cachePath;

        if (mTask == null) mTask = new LinkedList<DownloadTask>();
        if (mTaskList == null) mTaskList = new LinkedList<TaskEntity>();
        switch (taskLevel) {
            case TASKLEVEL_MAX_CPU:
                mExecutor = Executors.newCachedThreadPool();
                mLimitTaskCount = IThreadPool.MAXIMUM_POOL_SIZE * 2;
                break;
            case TASKLEVEL_SERIAL:
                mExecutor = Executors.newSingleThreadExecutor();
                mLimitTaskCount = 1;
                break;
            default:
                mExecutor = new ThreadPoolExecutor(IThreadPool.CORE_POOL_SIZE,
                        IThreadPool.MAXIMUM_POOL_SIZE, IThreadPool.KEEP_ALIVE, TimeUnit.SECONDS,
                        new LinkedBlockingQueue<Runnable>(IThreadPool.CAPACITY),
                        new ThreadFactory() {
                            private final AtomicInteger mCount = new AtomicInteger(1);

                            public Thread newThread(Runnable r) {
                                return new Thread(r, TAG + IFormat._3 + mCount.getAndIncrement());
                            }
                        });
                mLimitTaskCount = IThreadPool.CPU_COUNT;
                break;
        }

        return true;
    }

    public static TaskUtils getInstance() {
        if (mInstance == null) {
            synchronized (TaskUtils.class) {
                if (mInstance == null) mInstance = new TaskUtils();
            }
        }
        return mInstance;
    }


    /**
     * 打开网址（添加新任务到队列，并执行或稍后执行）
     * @param action       动作标识
     * @param url          网址
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runOpenGetUrl(String action, String url, ITask taskListener, Object inEntity,
            ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || TxtUtils.isEmpty(url)) return false;
        cancelTask(action);
        ConnectEntity conEntity = ConnectUtils.createConnectEntity(url, null, 0, 0);
        mTaskList.add(getTaskEntity(action, null, DownloadTask.TYPE_OPEN_URL_GET,
                DownloadTask.LEVEL_DEFALUT, conEntity, null, null, taskListener, inEntity,
                taskToDo));
        runTaskAuto();
        return true;
    }

    /**
     * 打开网址（添加新任务到队列顶部，并立即执行）
     * @param action       动作标识
     * @param url          网址
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runOpenGetUrlNow(String action, String url, ITask taskListener, Object inEntity,
            ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || TxtUtils.isEmpty(url)) return false;
        cancelTask(action);
        ConnectEntity conEntity = ConnectUtils.createConnectEntity(url, null, 0, 0);
        mTaskList.add(
                getTaskEntity(action, null, DownloadTask.TYPE_OPEN_URL_GET, DownloadTask.LEVEL_HIGH,
                        conEntity, null, null, taskListener, inEntity, taskToDo));
        runTask(mTaskList.get(0), true);
        return true;
    }

    /**
     * 打开网址（如果在执行中，不处理直接返回，否则，添加新任务到队列，并执行或稍后执行）
     * @param action       动作标识
     * @param keepKey      保持标识
     * @param url          网址
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runOpenGetUrlKeeping(String action, String keepKey, String url,
            ITask taskListener, Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || TxtUtils.isEmpty(url) || checkTask(action + keepKey))
            return false;
        ConnectEntity conEntity = ConnectUtils.createConnectEntity(url, null, 0, 0);
        mTaskList.add(getTaskEntity(action, keepKey, DownloadTask.TYPE_OPEN_URL_GET,
                DownloadTask.LEVEL_DEFALUT, conEntity, null, null, taskListener, inEntity,
                taskToDo));
        runTaskAuto();
        return true;
    }

    /**
     * 打开网址（添加新任务到队列，并执行或稍后执行）
     * @param action       动作标识
     * @param url          网址
     * @param params       参数
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runOpenPostUrl(String action, String url, String params, ITask taskListener,
            Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || TxtUtils.isEmpty(url)) return false;
        cancelTask(action);
        ConnectEntity conEntity = ConnectUtils.createConnectEntity(url,
                params == null ? null : params.getBytes(), 0, 0);
        mTaskList.add(getTaskEntity(action, null, DownloadTask.TYPE_OPEN_URL_POST,
                DownloadTask.LEVEL_DEFALUT, conEntity, null, null, taskListener, inEntity,
                taskToDo));
        runTaskAuto();
        return true;
    }

    /**
     * 打开网址（添加新任务到队列顶部，并立即执行）
     * @param action       动作标识
     * @param url          网址
     * @param params       参数
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runOpenPostUrlNow(String action, String url, String params, ITask taskListener,
            Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || TxtUtils.isEmpty(url)) return false;
        cancelTask(action);
        ConnectEntity conEntity = ConnectUtils.createConnectEntity(url,
                params == null ? null : params.getBytes(), 0, 0);
        mTaskList.add(getTaskEntity(action, null, DownloadTask.TYPE_OPEN_URL_POST,
                DownloadTask.LEVEL_HIGH, conEntity, null, null, taskListener, inEntity, taskToDo));
        runTask(mTaskList.get(0), true);
        return true;
    }

    /**
     * 打开网址（如果在执行中，不处理直接返回，否则，添加新任务到队列，并执行或稍后执行）
     * @param action       动作标识
     * @param keepKey      保持标识
     * @param url          网址
     * @param params       参数
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runOpenPostUrlKeeping(String action, String keepKey, String url, String params,
            ITask taskListener, Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || TxtUtils.isEmpty(url) || checkTask(action + keepKey))
            return false;
        ConnectEntity conEntity = ConnectUtils.createConnectEntity(url,
                params == null ? null : params.getBytes(), 0, 0);
        mTaskList.add(getTaskEntity(action, keepKey, DownloadTask.TYPE_OPEN_URL_POST,
                DownloadTask.LEVEL_DEFALUT, conEntity, null, null, taskListener, inEntity,
                taskToDo));
        runTaskAuto();
        return true;
    }

    /**
     * 下载文件（添加新任务到队列，并执行或稍后执行）
     * @param action       动作标识
     * @param fileEntity   文件对象
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runDownloadFile(String action, FileEntity fileEntity, ITask taskListener,
            Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || fileEntity == null || TxtUtils.isEmpty(fileEntity.getUrl()))
            return false;
        cancelTask(action);
        ConnectEntity conEntity = ConnectUtils.createConnectEntity(fileEntity.getUrl(), null, 0, 0);
        mTaskList.add(getTaskEntity(action, null, DownloadTask.TYPE_DOWNLOAD_FILE,
                DownloadTask.LEVEL_LOWEST, conEntity, fileEntity, null, taskListener, inEntity,
                taskToDo));
        runTaskAuto();
        return true;
    }

    /**
     * 下载文件（添加新任务到队列顶部，并立即执行）
     * @param action       动作标识
     * @param fileEntity   文件对象
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runDownloadFileNow(String action, FileEntity fileEntity, ITask taskListener,
            Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || fileEntity == null || TxtUtils.isEmpty(fileEntity.getUrl()))
            return false;
        cancelTask(action);
        ConnectEntity conEntity = ConnectUtils.createConnectEntity(fileEntity.getUrl(), null, 0, 0);
        mTaskList.add(getTaskEntity(action, null, DownloadTask.TYPE_DOWNLOAD_FILE,
                DownloadTask.LEVEL_DEFALUT, conEntity, fileEntity, null, taskListener, inEntity,
                taskToDo));
        runTask(mTaskList.get(0), true);
        return true;
    }

    /**
     * 下载文件（如果在执行中，不处理直接返回，否则，添加新任务到队列，并执行或稍后执行）
     * @param action       动作标识
     * @param keepKey      保持标识
     * @param fileEntity   文件对象
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runDownloadFileKeeping(String action, String keepKey, FileEntity fileEntity,
            ITask taskListener, Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action)
                || fileEntity == null
                || TxtUtils.isEmpty(fileEntity.getUrl())
                || checkTask(action + keepKey)) return false;
        ConnectEntity conEntity = ConnectUtils.createConnectEntity(fileEntity.getUrl(), null, 0, 0);
        mTaskList.add(getTaskEntity(action, keepKey, DownloadTask.TYPE_DOWNLOAD_FILE,
                DownloadTask.LEVEL_LOWEST, conEntity, fileEntity, null, taskListener, inEntity,
                taskToDo));
        runTaskAuto();
        return true;
    }

    /**
     * 下载文件（添加新任务到队列，并执行或稍后执行）
     * @param action       动作标识
     * @param fileList     文件队列
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runDownloadFileList(String action, List<FileEntity> fileList, ITask taskListener,
            Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || EntityUtils.isEmpty(fileList)) return false;
        cancelTask(action);
        mTaskList.add(getTaskEntity(action, null, DownloadTask.TYPE_DOWNLOAD_FILE,
                DownloadTask.LEVEL_LOWEST, null, null, fileList, taskListener, inEntity, taskToDo));
        runTaskAuto();
        return true;
    }

    /**
     * 下载文件（添加新任务到队列顶部，并立即执行）
     * @param action       动作标识
     * @param fileList     文件队列
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runDownloadFileListNow(String action, List<FileEntity> fileList,
            ITask taskListener, Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || EntityUtils.isEmpty(fileList)) return false;
        cancelTask(action);
        mTaskList.add(getTaskEntity(action, null, DownloadTask.TYPE_DOWNLOAD_FILE,
                DownloadTask.LEVEL_DEFALUT, null, null, fileList, taskListener, inEntity,
                taskToDo));
        runTask(mTaskList.get(0), true);
        return true;
    }

    /**
     * 下载文件（如果在执行中，不处理直接返回，否则，添加新任务到队列，并执行或稍后执行）
     * @param action       动作标识
     * @param keepKey      保持标识
     * @param fileList     文件队列
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runDownloadFileListKeeping(String action, String keepKey,
            List<FileEntity> fileList, ITask taskListener, Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || EntityUtils.isEmpty(fileList) || checkTask(
                action + keepKey)) return false;
        mTaskList.add(getTaskEntity(action, keepKey, DownloadTask.TYPE_DOWNLOAD_FILE_LIST,
                DownloadTask.LEVEL_LOWEST, null, null, fileList, taskListener, inEntity, taskToDo));
        runTaskAuto();
        return true;
    }

    /**
     * 执行后台任务（添加新任务到队列，并执行或稍后执行）
     * @param action       动作标识
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runDoInBackground(String action, ITask taskListener, Object inEntity,
            ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action)) return false;
        cancelTask(action);
        mTaskList.add(getTaskEntity(action, null, DownloadTask.TYPE_DO_IN_BACKGROUND,
                DownloadTask.LEVEL_DEFALUT, null, null, null, taskListener, inEntity, taskToDo));
        runTaskAuto();
        return true;
    }

    /**
     * 执行后台任务（添加新任务到队列顶部，并立即执行）
     * @param action       动作标识
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runDoInBackgroundNow(String action, ITask taskListener, Object inEntity,
            ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action)) return false;
        cancelTask(action);
        mTaskList.add(getTaskEntity(action, null, DownloadTask.TYPE_DO_IN_BACKGROUND,
                DownloadTask.LEVEL_HIGH, null, null, null, taskListener, inEntity, taskToDo));
        runTask(mTaskList.get(0), true);
        return true;
    }

    /**
     * 执行后台任务（如果在执行中，不处理直接返回，否则，添加新任务到队列，并执行或稍后执行）
     * @param action       动作标识
     * @param keepKey      保持标识
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public boolean runDoInBackgroundKeeping(String action, String keepKey, ITask taskListener,
            Object inEntity, ITaskToDo taskToDo) {
        if (TxtUtils.isEmpty(action) || checkTask(action + keepKey)) return false;
        mTaskList.add(getTaskEntity(action, keepKey, DownloadTask.TYPE_DO_IN_BACKGROUND,
                DownloadTask.LEVEL_DEFALUT, null, null, null, taskListener, inEntity, taskToDo));
        runTaskAuto();
        return true;
    }


    /**
     * 执行任务（自动）
     * @return
     */
    private synchronized boolean runTaskAuto() {
        if (mTaskList == null) return false;
        boolean isRun = false;
        synchronized (mTaskList) {
            for (int i = 0, c = mTaskList.size(); i < c; i++) {
                TaskEntity task = mTaskList.get(i);
                if (TaskEntity.GROUP_AUTO == task.getGroup() && UNKNOWN == task.getState()) {
                    isRun = runTask(task, false);
                }
            }
        }
        return isRun;
    }

    /**
     * 执行任务
     * @param entity 任务对象
     * @param isRun  是否立即执行
     * @return
     */
    private synchronized boolean runTask(TaskEntity entity, boolean isRun) {
        if (entity == null || TxtUtils.isEmpty(entity.getAction())) {
            return false;
        } else if (mTask.size() >= mLimitTaskCount && !isRun) {
            return false;
        } else if (checkTask(entity.getAction() + entity.getKeepKey())) {
            return true;
        }
        entity.setState(READY);
        DownloadTask task = new DownloadTask(entity, eTaskListener, isLog());
        if (SDKUtils.equalAPI_11_Honeycomb()) {
            task.executeOnExecutor(mExecutor);
        } else {
            task.execute();
        }

        mTask.add(task);
        return true;
    }

    /**
     * 检查任务
     * @param action 任务标识
     * @return
     */
    public synchronized boolean checkTask(String action) {
        if (TxtUtils.isEmpty(action)) return false;
        for (int i = 0, c = mTask.size(); i < c; i++) {
            if (action.equals(mTask.get(i).getAction())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取消任务
     * @param action 任务标识
     * @return
     */
    public synchronized boolean cancelTask(String action) {
        if (TxtUtils.isEmpty(action)) return false;
        for (int i = 0, c = mTask.size(); i < c; i++) {
            if (action.equals(mTask.get(i).getAction())) {
                mTask.get(i).cancel(true);
                mTask.remove(i);
                break;
            }
        }
        for (int i = 0, c = mTaskList.size(); i < c; i++) {
            if (action.equals(mTaskList.get(i).getAction() + mTaskList.get(i).getKeepKey())) {
                mTaskList.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * 取消全部任务
     */
    public synchronized void cancelAllTask() {
        for (int i = 0, c = mTask.size(); i < c; i++) {
            mTask.get(i).cancel(true);
        }
        mTask.clear();
        mTaskList.clear();
    }

    private ImplDownloadTask eTaskListener = new ImplDownloadTask() {

        @Override
        public void onDownloadBegin(TaskEntity taskEntity) {
            if (taskEntity == null || taskEntity.getTaskListener() == null) return;
            taskEntity.getTaskListener()
                      .onTaskBegin(taskEntity.getAction(), taskEntity.getInEntity());
        }

        public void onDownloadProgress(TaskEntity taskEntity, String action, int what, Object obj) {
            if (taskEntity == null || taskEntity.getTaskListener() == null) return;
            taskEntity.getTaskListener().onTaskProgress(action, what, obj);
        }

        @Override
        public void onDownloadFinished(TaskEntity taskEntity) {
            if (taskEntity == null || taskEntity.getAction() == null) return;
            cancelTask(taskEntity.getAction() + taskEntity.getKeepKey());
            runTaskAuto();
            if (taskEntity.getTaskListener() != null) taskEntity.getTaskListener()
                                                                .onTaskFinished(
                                                                        taskEntity.getAction(),
                                                                        taskEntity.getInEntity(),
                                                                        taskEntity.getOutEntity());
        }

        @Override
        public void onDownloadError(TaskEntity taskEntity) {
            if (taskEntity == null || taskEntity.getTaskListener() == null) return;
            if (CANCELLED != taskEntity.getState())
                cancelTask(taskEntity.getAction() + taskEntity.getKeepKey());
            runTaskAuto();
            taskEntity.getTaskListener()
                      .onTaskError(taskEntity.getAction(), taskEntity.getInEntity(),
                              taskEntity.getState());
        }

    };

    /**
     * 获取任务对象
     * @param action       动作标识
     * @param keepKey
     * @param type         联网类型
     * @param level        线程级别
     * @param conEntity    联网对象
     * @param fileEntity   文件对象
     * @param fileList     文件队列
     * @param taskListener 任务监听器
     * @param inEntity     输入对象
     * @param taskToDo     后台线程执行调用对象
     * @return
     */
    public static TaskEntity getTaskEntity(String action, String keepKey, int type, int level,
            ConnectEntity conEntity, FileEntity fileEntity, List<FileEntity> fileList,
            ITask taskListener, Object inEntity, ITaskToDo taskToDo) {
        TaskEntity entity = new TaskEntity(action, keepKey, type, level, TaskEntity.GROUP_AUTO);
        entity.setConEntity(conEntity);
        entity.setFileEntity(fileEntity);
        entity.setFileList(fileList);
        entity.setTaskListener(taskListener);
        entity.setInEntity(inEntity);
        entity.setTaskToDo(taskToDo);
        return entity;
    }

}
