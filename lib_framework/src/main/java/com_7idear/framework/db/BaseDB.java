package com_7idear.framework.db;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com_7idear.framework.application.BaseApplication;
import com_7idear.framework.config.BaseConfig;
import com_7idear.framework.log.LogUtils;
import com_7idear.framework.utils.TxtUtils;

import java.util.List;


/**
 * 基础数据库类（抽象父类）
 * @author iEclipse 2019/8/18
 * @description 实现数据库实例、数据库升降级帮助、数据表增、删、改、查和数据事务处理
 */
public abstract class BaseDB
        extends BaseConfig {

    protected     Context         mContext; //环境对象
    private final DBHelper        mHelper; //DB帮助类
    private       SQLiteDatabase  mDB; //DB
    private       ContentResolver mResolver; //内容提供者

    public BaseDB() {
        mContext = BaseApplication.getAppContext();
        mHelper = new DBHelper(mContext);
        mDB = mHelper.getWritableDatabase();
        mResolver = mContext.getContentResolver();
        log().append("BaseDB", this)
             .append("mContext", mContext)
             .append("mHelper", mHelper)
             .append("mDB", mDB)
             .append("mResolver", mResolver)
             .toLogD("BaseDB");
    }

    /**
     * 内部DB帮助类
     */
    private class DBHelper
            extends SQLiteOpenHelper {

        public DBHelper(Context context) {
            super(context, getRealDatabaseName(), null, getRealDatabaseVersion());
            log().append("getRealDatabaseName", getRealDatabaseName())
                 .append("getRealDatabaseVersion", getRealDatabaseVersion())
                 .toLogD("DBHelper");
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            log().append("SQLiteDatabase", db)
                 .append("getRealDatabaseName", getRealDatabaseName())
                 .append("getRealDatabaseVersion", getRealDatabaseVersion())
                 .toLogD("onCreate");
            onDataBaseCreate(db);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            log().append("SQLiteDatabase", db)
                 .append("oldVersion", oldVersion)
                 .append("newVersion", newVersion)
                 .toLogD("onUpgrade");
            onDataBaseUpgrade(db, oldVersion, newVersion);
        }

        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            log().append("SQLiteDatabase", db)
                 .append("oldVersion", oldVersion)
                 .append("newVersion", newVersion)
                 .toLogD("onDowngrade");
            onDataBaseDowngrade(db, oldVersion, newVersion);
        }
    }

    /**
     * 获取真实的数据库名称
     * @return
     */
    protected abstract String getRealDatabaseName();

    /**
     * 获取真实的数据库版本号
     * @return
     */
    protected abstract int getRealDatabaseVersion();

    /**
     * 创建数据库
     * @param db 数据库
     */
    protected abstract void onDataBaseCreate(SQLiteDatabase db);

    /**
     * 数据库升级
     * @param db         数据库
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    protected abstract void onDataBaseUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * 数据库降级
     * @param db         数据库
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     */
    protected abstract void onDataBaseDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);

    /**
     * 打开数据库
     * @return
     */
    private SQLiteDatabase openDB() {
        if (mDB == null || !mDB.isOpen()) mDB = mHelper.getWritableDatabase();
        return mDB;
    }

    /**
     * 关闭数据库
     */
    private void closeDB() {
        try {
            mHelper.close();
        } catch (Exception e) {
            LogUtils.catchException(e);
        }
    }

    /**
     * 获取内容提供者
     * @return
     */
    private ContentResolver getResolver() {
        if (mResolver == null) mResolver = mContext.getContentResolver();
        return mResolver;
    }

    /**
     * 检查版本以便升级
     * @param dbVersion  需要比较的版本号
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     * @return
     */
    protected static boolean checkUpgradeVersion(int dbVersion, int oldVersion, int newVersion) {
        return oldVersion < dbVersion && newVersion >= dbVersion;
    }

    /**
     * 检查版本以便降级
     * @param dbVersion  需要比较的版本号
     * @param oldVersion 旧版本号
     * @param newVersion 新版本号
     * @return
     */
    protected static boolean checkDowngradeVersion(int dbVersion, int oldVersion, int newVersion) {
        return oldVersion >= dbVersion && newVersion < dbVersion;
    }

    /**
     * 执行SQL
     * @param db  数据库
     * @param sql SQL语句
     * @return
     */
    protected boolean runExecSQL(SQLiteDatabase db, String sql) {
        if (db == null || TxtUtils.isEmpty(sql)) return false;
        try {
            db.execSQL(sql);
        } catch (Exception e) {
            LogUtils.catchException(e);
        }
        return true;
    }

    /**
     * 执行事务
     * @param table    表
     * @param list     数据
     * @param listener 监听器
     * @param <T>
     */
    protected <T> void runTransactionEach(TableEntity table, List<T> list,
            ITransactionListener<T> listener) {
        if (table != null && list != null && list.size() > 0) {
            synchronized (list) {
                openDB().beginTransaction();
                try {
                    for (int i = 0, c = list.size(); i < c; i++) {
                        listener.onTransaction(table, list.get(i), i);
                    }
                    openDB().setTransactionSuccessful();
                } catch (Exception e) {
                    LogUtils.catchException(e);
                } finally {
                    openDB().endTransaction();
                }
            }
        }
    }

    /**
     * 查询
     * @param table 表
     * @return
     */
    public Object query(TableEntity table) {
        if (table == null) return null;
        Cursor cursor = null;
        try {
            if (table.getTableUri() == null) {
                cursor = openDB().query(table.isDistinct(), table.getTableName(),
                        table.getColumns(), table.getWhereClause(), table.getWhereArgs(),
                        table.getGroupBy(), table.getHaving(), table.getOrderBy(),
                        table.getLimit());
            } else {
                cursor = getResolver().query(table.getTableUri(), table.getColumns(),
                        table.getWhereClause(), table.getWhereArgs(), table.getOrderBy());
            }
            log().appendLine("count", cursor.getCount()).append(table).toLogD("query");

            if (table.getQueryListener() != null) {
                return table.getQueryListener().onQueryFinished(cursor, cursor.getCount());
            }
        } catch (Exception e) {
            LogUtils.catchException(e);
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            cursor = null;
        }
        return null;
    }

    /**
     * 查询数量
     * @param table 表
     * @return
     */
    public int queryCount(TableEntity table) {
        if (table == null) return 0;
        Cursor cursor = null;
        try {
            if (table.getTableUri() == null) {
                cursor = openDB().query(false, table.getTableName(), null, table.getWhereClause(),
                        table.getWhereArgs(), table.getGroupBy(), null, null, null);
            } else {
                cursor = getResolver().query(table.getTableUri(), null, table.getWhereClause(),
                        table.getWhereArgs(), null);
            }
            log().appendLine("count", cursor.getCount()).append(table).toLogD("queryCount");

            return cursor.getCount();
        } catch (Exception e) {
            LogUtils.catchException(e);
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            cursor = null;
        }
        return 0;
    }

    /**
     * 添加数据
     * @param table 表
     * @return
     */
    public long insert(TableEntity table) {
        if (table == null) return -1;
        long id = openDB().insert(table.getTableName(), null, table.getValues());
        log().appendLine("id", id).append(table).toLogD("insert");

        return id;
    }

    /**
     * 添加或替换数据（按表默认_id）
     * @param table 表
     * @return
     */
    public long insertOrUpdate(TableEntity table) {
        long id = openDB().replace(table.getTableName(), null, table.getValues());
        log().appendLine("id", id).append(table).toLogD("insertOrUpdate");

        return id;
    }

    /**
     * 更新数据
     * @param table 表
     * @return
     */
    public long update(TableEntity table) {
        long count = openDB().update(table.getTableName(), table.getValues(),
                table.getWhereClause(), table.getWhereArgs());
        log().appendLine("count", count).append(table).toLogD("update");

        return count;
    }

    /**
     * 删除数据
     * @param table 表
     * @return
     */
    public long delete(TableEntity table) {
        long count = openDB().delete(table.getTableName(), table.getWhereClause(),
                table.getWhereArgs());
        log().appendLine("count", count).append(table).toLogD("delete");

        return count;
    }

    /**
     * 查询内容
     * @param table 表
     * @return
     */
    public Object queryUri(TableEntity table) {
        if (table == null || table.getTableUri() == null) return null;
        Cursor cursor = null;
        try {
            cursor = getResolver().query(table.getTableUri(), table.getColumns(),
                    table.getWhereClause(), table.getWhereArgs(), table.getOrderBy());
            log().appendLine("count", cursor.getCount()).append(table).toLogD("queryUri");

            if (table.getQueryListener() != null) {
                return table.getQueryListener().onQueryFinished(cursor, cursor.getCount());
            }
        } catch (Exception e) {
            LogUtils.catchException(e);
        } finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
            cursor = null;
        }
        return null;
    }

    /**
     * 添加内容
     * @param table 表
     * @return
     */
    public Uri insertUri(TableEntity table) {
        if (table == null || table.getTableUri() == null) return null;
        Uri uri = getResolver().insert(table.getTableUri(), table.getValues());
        log().appendLine("uri", uri).append(table).toLogD("insertUri");

        return uri;
    }

    /**
     * 更新内容
     * @param table 表
     * @return
     */
    public long updateUri(TableEntity table) {
        long count = getResolver().update(table.getTableUri(), table.getValues(),
                table.getWhereClause(), table.getWhereArgs());
        log().appendLine("count", count).append(table).toLogD("updateUri");

        return count;
    }

    /**
     * 删除内容
     * @param table 表
     * @return
     */
    public long deleteUri(TableEntity table) {
        long count = getResolver().delete(table.getTableUri(), table.getWhereClause(),
                table.getWhereArgs());
        log().appendLine("count", count).append(table).toLogD("deleteUri");

        return count;
    }

    /**
     * 获取字符
     * @param cursor 游标
     * @param tag    标识
     * @return
     */
    public String getCursorString(Cursor cursor, String tag) {
        return cursor == null
                ? ""
                : TxtUtils.isEmpty(cursor.getString(cursor.getColumnIndex(tag)), "");
    }

    /**
     * 获取数值
     * @param cursor 游标
     * @param tag    标识
     * @return
     */
    public int getCursorInt(Cursor cursor, String tag) {
        return cursor == null ? 0 : cursor.getInt(cursor.getColumnIndex(tag));
    }

    /**
     * 获取数值
     * @param cursor 游标
     * @param tag    标识
     * @return
     */
    public long getCursorLong(Cursor cursor, String tag) {
        return cursor == null ? 0 : cursor.getLong(cursor.getColumnIndex(tag));
    }

    /**
     * 获取浮点数
     * @param cursor 游标
     * @param tag    标识
     * @return
     */
    public float getCursorFloat(Cursor cursor, String tag) {
        return cursor == null ? 0f : cursor.getFloat(cursor.getColumnIndex(tag));
    }

    /**
     * 获取数值
     * @param cursor 游标
     * @param tag    标识
     * @return
     */
    public double getCursorDouble(Cursor cursor, String tag) {
        return cursor == null ? 0d : cursor.getDouble(cursor.getColumnIndex(tag));
    }

    /**
     * 获取字节
     * @param cursor 游标
     * @param tag    标识
     * @return
     */
    public byte[] getCursorBlob(Cursor cursor, String tag) {
        return cursor == null ? null : cursor.getBlob(cursor.getColumnIndex(tag));
    }

    /**
     * 查询接口
     * @param <T>
     */
    public interface IQueryListener<T> {
        T onQueryFinished(Cursor cursor, int count);
    }

    /**
     * 事务接口
     * @param <T>
     */
    public interface ITransactionListener<T> {
        void onTransaction(TableEntity table, T entity, int index);
    }
}
