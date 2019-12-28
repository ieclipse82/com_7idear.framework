package com_7idear.framework.task;

import android.os.AsyncTask;
import android.os.Process;
import android.webkit.URLUtil;

import com_7idear.framework.entity.FileEntity;
import com_7idear.framework.intface.IFile;
import com_7idear.framework.intface.IFormat;
import com_7idear.framework.intface.IState;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.log.LogUtils;
import com_7idear.framework.net.Connect;
import com_7idear.framework.net.ConnectEntity;
import com_7idear.framework.net.ConnectUtils;
import com_7idear.framework.utils.CacheUtils;
import com_7idear.framework.utils.EntityUtils;
import com_7idear.framework.utils.FileUtils;
import com_7idear.framework.utils.TxtUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


/**
 * 下载任务类
 * @author ieclipse 19-12-16
 * @description 支持网址请求（GET，POST），文件（文件队列）下载和后台任务
 */
public class DownloadTask
        extends AsyncTask<Object, Object, TaskEntity>
        implements IState, IFile, IFormat {

    /**
     * 类型——0：未知
     */
    public static final int TYPE_UNKNOWN            = 0;
    /**
     * 类型——1：打开URL GET请求
     */
    public static final int TYPE_OPEN_URL_GET       = 1;
    /**
     * 类型——2：打开URL POST请求
     */
    public static final int TYPE_OPEN_URL_POST      = 2;
    /**
     * 类型——3：下载文件
     */
    public static final int TYPE_DOWNLOAD_FILE      = 3;
    /**
     * 类型——4：下载文件队列
     */
    public static final int TYPE_DOWNLOAD_FILE_LIST = 4;
    /**
     * 类型——5：后台任务
     */
    public static final int TYPE_DO_IN_BACKGROUND   = 5;

    /**
     * 级别——0：默认
     */
    public static final int LEVEL_DEFALUT = 0;
    /**
     * 级别——1：高
     */
    public static final int LEVEL_HIGH    = 1;
    /**
     * 级别——-1：低
     */
    public static final int LEVEL_LOWEST  = -1;

    private static int mBuffer = 2048; //缓冲大小

    private TaskEntity       mTaskEntity; //任务对象
    private ImplDownloadTask mTaskListener; //任务监听器
    private int              mPriority; //任务执行级别
    private boolean          isLog; //是否输出日志

    public DownloadTask(TaskEntity entity) {
        this(entity, null, false);
    }

    public DownloadTask(TaskEntity entity, ImplDownloadTask listener, boolean isLog) {
        if (entity == null) {
            mTaskEntity = new TaskEntity("", null, TYPE_UNKNOWN, LEVEL_DEFALUT,
                    TaskEntity.GROUP_ONCE);
            mTaskEntity.setState(FAILED);
        } else {
            mTaskEntity = entity;
        }
        this.mTaskListener = listener;
        this.isLog = isLog;

        switch (mTaskEntity.getLevel()) {
            case LEVEL_HIGH:
                mPriority = Process.THREAD_PRIORITY_FOREGROUND;
                break;
            case LEVEL_LOWEST:
                mPriority = Process.THREAD_PRIORITY_LOWEST;
                break;
            default:
                mPriority = Process.THREAD_PRIORITY_BACKGROUND;
                break;
        }
    }

    /**
     * 获取动作标识
     * @return
     */
    public String getAction() {
        return mTaskEntity.getAction() + TxtUtils.isEmpty(mTaskEntity.getKeepKey(), "");
    }

    @Override
    protected void onPreExecute() {
        if (isLog) new LogEntity().append("mTaskEntity", mTaskEntity).toLogD();
        if (mTaskListener != null) {
            mTaskListener.onDownloadBegin(mTaskEntity);
        }
    }

    @Override
    protected TaskEntity doInBackground(Object... params) {
        if (isLog) new LogEntity().append("mTaskEntity", mTaskEntity).toLogD();
        Process.setThreadPriority(mPriority);

        if (mTaskEntity == null || FAILED == mTaskEntity.getState()) {
            mTaskEntity.setState(FAILED);
            return mTaskEntity;
        } else if (isCancelled()) {
            mTaskEntity.setState(CANCELLED);
            return mTaskEntity;
        }
        mTaskEntity.setState(DOING);
        if (TYPE_OPEN_URL_GET == mTaskEntity.getType()) {
            runOpenUrl(TYPE_OPEN_URL_GET);
        } else if (TYPE_OPEN_URL_POST == mTaskEntity.getType()) {
            runOpenUrl(TYPE_OPEN_URL_POST);
        } else if (TYPE_DOWNLOAD_FILE == mTaskEntity.getType()) {
            runDownloadFile();
        } else if (TYPE_DOWNLOAD_FILE_LIST == mTaskEntity.getType()) {
            runDownloadFileList();
        } else if (TYPE_DO_IN_BACKGROUND == mTaskEntity.getType()) {
            runBackgroundTask();
        }

        return mTaskEntity;
    }

    @Override
    protected void onProgressUpdate(Object... values) {
        super.onProgressUpdate(values);
        if (isLog) new LogEntity().append("mTaskEntity", mTaskEntity).toLogD();
        if (mTaskListener != null
                && values != null
                && values.length > 2
                && values[0] instanceof String
                && values[1] instanceof Integer) {
            mTaskListener.onDownloadProgress(mTaskEntity, (String) values[0], (Integer) values[1],
                    values[2]);
        }
    }

    @Override
    protected void onPostExecute(TaskEntity result) {
        super.onPostExecute(result);
        if (isLog) new LogEntity().append("mTaskEntity", mTaskEntity).toLogD();
        if (mTaskListener != null) {
            if (FINISHED == mTaskEntity.getState()) {
                mTaskListener.onDownloadFinished(mTaskEntity);
            } else {
                mTaskListener.onDownloadError(mTaskEntity);
            }
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        mTaskEntity.setState(CANCELLED);
        if (mTaskEntity.getConEntity() != null) {
            mTaskEntity.getConEntity().setState(CANCELLED);
        }
        if (isLog) new LogEntity().append("mTaskEntity", mTaskEntity).toLogD();
        if (mTaskListener != null) {
            mTaskListener.onDownloadError(mTaskEntity);
        }
    }

    private ImplTaskHelper mTaskHelper = new ImplTaskHelper() {
        @Override
        public boolean isTaskCancel() {
            if (isCancelled()) {
                mTaskEntity.setState(CANCELLED);
                return true;
            }
            return false;
        }

        @Override
        public void onUIRefresh(String action, int what, Object obj) {
            publishProgress(action, what, obj);
        }
    };

    /**
     * 执行打开网址
     * @param type 类型
     */
    private void runOpenUrl(int type) {
        ConnectEntity conEntity = mTaskEntity.getConEntity();
        if (conEntity == null) {
            mTaskEntity.setState(FAILED);
            return;
        } else if (!URLUtil.isNetworkUrl(conEntity.getUrl())) {
            mTaskEntity.setState(ERROR_URL_UNKNOWN);
            return;
        }

        byte[] bytes = null;
        if (TYPE_OPEN_URL_GET == type) {
            bytes = ConnectUtils.openGetUrl(conEntity);
        } else if (TYPE_OPEN_URL_POST == type) {
            bytes = ConnectUtils.openPostUrl(conEntity);
        }
        if (isCancelled() || CANCELLED == conEntity.getState()) {
            mTaskEntity.setState(CANCELLED);
        } else if (bytes == null) {
            mTaskEntity.setState(FAILED);
        } else if (FINISHED == conEntity.getState()) {
            if (isLog) new LogEntity().append("mTaskEntity", mTaskEntity).toLogD();
            ITaskToDo taskToDo = mTaskEntity.getTaskToDo();
            if (taskToDo instanceof IParserToDo) {
                IParserToDo l = (IParserToDo) taskToDo;
                mTaskEntity.setOutEntity(
                        l.runParser(mTaskEntity.getAction(), bytes, mTaskEntity.getInEntity(),
                                mTaskHelper));
            }
            mTaskEntity.setState(FINISHED);
        } else {
            mTaskEntity.setState(conEntity.getState());
        }
    }

    /**
     * 执行下载文件
     */
    private void runDownloadFile() {
        ConnectEntity conEntity = mTaskEntity.getConEntity();
        FileEntity fileEntity = mTaskEntity.getFileEntity();
        if (conEntity == null || fileEntity == null) {
            mTaskEntity.setState(FAILED);
            return;
        } else if (!URLUtil.isNetworkUrl(conEntity.getUrl())) {
            mTaskEntity.setState(ERROR_URL_UNKNOWN);
            return;
        }

        Connect con = new Connect();
        try {
            File file = loadCacheFile(fileEntity);
            conEntity.setRequestByteIndex((int) fileEntity.getDownloadSize());
            conEntity = ConnectUtils.openGetUrl(con, conEntity);
            if (isCancelled() || CANCELLED == conEntity.getState()) {
                fileEntity.setState(STATE_DOWNLOAD_ERROR);
                mTaskEntity.setState(CANCELLED);
            } else if (FINISHED == conEntity.getState()) {
                fileEntity.setState(STATE_DOWNLOAD_FINISHED);
                fileEntity.setSize((int) conEntity.getContentLength());
                ITaskToDo taskToDo = mTaskEntity.getTaskToDo();
                if (taskToDo instanceof IDownloadToDo) {
                    IDownloadToDo l = (IDownloadToDo) taskToDo;
                    mTaskEntity.setOutEntity(l.runDownload(mTaskEntity.getAction(), fileEntity,
                            conEntity.getContentStream(), mTaskEntity.getInEntity(), mTaskHelper));
                } else {
                    saveFile(fileEntity, conEntity.getContentStream(), file);
                    FileUtils.moveFile(file, new File(fileEntity.getFilePath()));
                }
                mTaskEntity.setState(FINISHED);

            } else {
                fileEntity.setState(STATE_DOWNLOAD_ERROR);
                mTaskEntity.setState(FAILED);
            }
        } catch (IOException e) {
            LogUtils.catchException(e);
            fileEntity.setState(STATE_DOWNLOAD_ERROR);
            mTaskEntity.setState(ERROR);
        } finally {
            try {
                if (conEntity != null && conEntity.getContentStream() != null)
                    conEntity.getContentStream().close();
                conEntity = null;
            } catch (IOException e) {
                LogUtils.catchException(e);
            }
            if (con != null) con.close();
            con = null;
        }
    }

    /**
     * 执行下载文件队列
     */
    private void runDownloadFileList() {
        FileEntity fileEntity = mTaskEntity.getFileEntity();
        List<FileEntity> fileList = mTaskEntity.getFileList();
        if (fileEntity == null || EntityUtils.isEmpty(fileList)) {
            mTaskEntity.setState(FAILED);
            return;
        }

        FileEntity tmp = null;
        ConnectEntity conEntity = null;

        Connect con = new Connect();
        for (int i = 0, c = fileList.size(); i < c; i++) {
            try {
                tmp = fileList.get(i);
                if (!URLUtil.isNetworkUrl(tmp.getUrl())) {
                    tmp.setState(STATE_DOWNLOAD_ERROR);
                    continue;
                }
                File file = loadCacheFile(tmp);

                conEntity = ConnectUtils.createConnectEntity(tmp.getUrl(), null,
                        (int) tmp.getDownloadSize(), 0);
                conEntity = ConnectUtils.openGetUrl(con, conEntity);
                if (isCancelled() || CANCELLED == conEntity.getState()) {
                    tmp.setState(STATE_DOWNLOAD_ERROR);
                    mTaskEntity.setState(CANCELLED);
                    break;
                } else if (FINISHED == conEntity.getState()) {
                    tmp.setState(STATE_DOWNLOAD_FINISHED);
                    tmp.setSize((int) conEntity.getContentLength());
                    ITaskToDo taskToDo = mTaskEntity.getTaskToDo();
                    if (taskToDo instanceof IDownloadToDo) {
                        IDownloadToDo l = (IDownloadToDo) taskToDo;
                        mTaskEntity.setOutEntity(l.runDownload(mTaskEntity.getAction(), tmp,
                                conEntity.getContentStream(), mTaskEntity.getInEntity(),
                                mTaskHelper));
                    } else {
                        saveFile(tmp, conEntity.getContentStream(), file);
                        FileUtils.moveFile(file, fileEntity.getPath()
                                + File.separator
                                + tmp.getName()
                                + tmp.getExtName());
                    }

                } else {
                    tmp.setState(STATE_DOWNLOAD_ERROR);
                    mTaskEntity.setState(FAILED);
                }
            } catch (IOException e) {
                LogUtils.catchException(e);
                tmp.setState(STATE_DOWNLOAD_ERROR);
                mTaskEntity.setState(FAILED);
            } finally {
                try {
                    if (conEntity != null && conEntity.getContentStream() != null)
                        conEntity.getContentStream().close();
                    conEntity = null;
                } catch (IOException e) {
                    LogUtils.catchException(e);
                }
            }
        }
        if (DOING == mTaskEntity.getState()) mTaskEntity.setState(FINISHED);

        if (con != null) con.close();
        con = null;
    }

    /**
     * 加载文件
     * @param entity 文件对象
     * @return
     */
    private File loadFile(FileEntity entity) {
        File file = null;
        if (FileEntity.STATE_UNDOWNLOAD == entity.getState()) {
            entity.setSize(0);
            entity.setDownloadSize(0);
            file = new File(entity.getPath());
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(
                    entity.getPath() + File.separator + entity.getName() + entity.getExtName());
            if (file.exists()) {
                file.delete();
            }
        } else {
            file = new File(
                    entity.getPath() + File.separator + entity.getName() + entity.getExtName());
            if (file.exists()) {
                if (entity.getDownloadSize() != file.length()) {
                    entity.setDownloadSize(file.length());
                }
                if (entity.getDownloadSize() == entity.getSize()) {
                    entity.setState(STATE_DOWNLOAD_FINISHED);
                }
            } else {
                entity.setState(FileEntity.STATE_UNDOWNLOAD);
                file = loadFile(entity);
            }
        }

        if (isLog) new LogEntity().append("file", file)
                                  .append("getPath", file == null ? "" : file.getPath())
                                  .append("getState", entity.getState())
                                  .append("getSize", entity.getSize())
                                  .append("getDownloadSize", entity.getDownloadSize())
                                  .toLogD();
        return file;
    }

    /**
     * 加载临时文件
     * @param entity 文件对象
     * @return
     */
    private File loadCacheFile(FileEntity entity) {
        if (entity == null) return null;
        if (TxtUtils.isEmpty(entity.getCache())) {
            //TODO 可能有问题
            entity.setCache(CacheUtils.getCachePath());
        }
        File file = null;
        if (FileEntity.STATE_UNDOWNLOAD == entity.getState()) {
            entity.setSize(0);
            entity.setDownloadSize(0);
            file = new File(entity.getCache());
            if (!file.exists()) {
                file.mkdirs();
            }
            file = new File(
                    entity.getCache() + File.separator + entity.getName() + entity.getExtName());
            if (file.exists()) {
                file.delete();
            }
        } else {
            file = new File(
                    entity.getCache() + File.separator + entity.getName() + entity.getExtName());
            if (file.exists()) {
                if (entity.getDownloadSize() != file.length()) {
                    entity.setDownloadSize(file.length());
                }
                if (entity.getDownloadSize() == entity.getSize()) {
                    entity.setState(STATE_DOWNLOAD_FINISHED);
                }
            } else {
                entity.setState(FileEntity.STATE_UNDOWNLOAD);
                file = loadFile(entity);
            }
        }

        if (isLog) new LogEntity().append("file", file)
                                  .append("getPath", file == null ? "" : file.getPath())
                                  .append("getState", entity.getState())
                                  .append("getSize", entity.getSize())
                                  .append("getDownloadSize", entity.getDownloadSize())
                                  .toLogD();
        return file;
    }

    /**
     * 保存文件
     * @param entity 文件对象
     * @param is     输入流
     * @param file   文件
     * @return
     * @throws IOException
     */
    private File saveFile(FileEntity entity, InputStream is, File file)
            throws IOException {
        if (entity == null || is == null) return file;
        entity.setState(FileEntity.STATE_DOWNLOADING);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file, true));
        int size = 0;
        byte buf[] = new byte[mBuffer];
        long p = 0;
        while ((size = is.read(buf)) != -1) {
            bos.write(buf, 0, size);
            entity.setDownloadSize(entity.getDownloadSize() + size);
            if (isLog && entity.getSize() != 0) {
                long cp = entity.getDownloadSize() * 10 / entity.getSize();
                if (p != cp) {
                    p = cp;
                    new LogEntity().append("getName", entity.getName())
                                   .appendAnd(_L2, "" + p, _L0, "10", _R2)
                                   .appendAnd(_L1, "" + entity.getDownloadSize(), _L0,
                                           "" + entity.getSize(), _R1)
                                   .toLogD();
                }
            }
            if (isCancelled()) {
                is.close();
                bos.flush();
                bos.close();
                entity.setState(STATE_DOWNLOAD_ERROR);
                return file;
            }
        }
        is.close();
        bos.flush();
        bos.close();
        entity.setState(STATE_DOWNLOAD_FINISHED);
        return file;
    }

    /**
     * 执行后台任务
     */
    private void runBackgroundTask() {
        if (isCancelled()) {
            mTaskEntity.setState(CANCELLED);
        } else {
            ITaskToDo taskToDo = mTaskEntity.getTaskToDo();
            if (taskToDo instanceof IBackgroundToDo) {
                IBackgroundToDo l = (IBackgroundToDo) taskToDo;
                mTaskEntity.setOutEntity(
                        l.runBackground(mTaskEntity.getAction(), mTaskEntity.getInEntity(),
                                mTaskHelper));
            }
            mTaskEntity.setState(FINISHED);
        }
    }

}
