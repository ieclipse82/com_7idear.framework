package com_7idear.framework.db;

import android.content.ContentValues;
import android.net.Uri;

import com_7idear.framework.intface.IFormat;
import com_7idear.framework.log.LogEntity;
import com_7idear.framework.utils.TxtUtils;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 数据表实体类
 * @author iEclipse 2019/8/18
 * @description 实现数据表构建、设置字段、设置查询条件等能力
 */
public class TableEntity
        implements IFormat {

    private static final String CREATE_TABLE               = "CREATE TABLE IF NOT EXISTS ";
    private static final String DROP_TABLE                 = "DROP TABLE ";
    private static final String TYPE_INTEGER_AUTOINCREMENT = " INTEGER PRIMARY KEY AUTOINCREMENT";
    private static final String TYPE_INTEGER               = " INTEGER";
    private static final String TYPE_FLOAT                 = " FLOAT";
    private static final String TYPE_LONG                  = " LONG";
    private static final String TYPE_REAL                  = " REAL";
    private static final String TYPE_TEXT                  = " TEXT";
    private static final String TYPE_BLOB                  = " BLOB";
    private static final String ORDER_BY_ASC               = " ASC";
    private static final String ORDER_BY_DESC              = " DESC";
    private static final String COL__ID                    = "_id";

    private Uri          tableUri; //表URI
    private String       tableName; //表名
    private List<String> tableColumns = new LinkedList<String>(); //字段列表

    private boolean               distinct; //是否去重
    private String[]              columns; //列数据
    private String                whereClause; //条件
    private String[]              whereArgs; //条件
    private String                groupBy; //GROUP
    private String                having; //HAVING
    private String                orderBy; //ORDER
    private String                limit; //限制数量
    private ContentValues         values; //内容数据
    private BaseDB.IQueryListener queryListener; //查询监听器

    public TableEntity(Uri uri) {
        this.tableUri = uri;
    }

    public TableEntity(String table) {
        this.tableName = table;
    }

    /**
     * 添加数值字段
     * @param colName 字段名称
     * @return
     */
    public TableEntity addIntegerColumns(String colName) {
        tableColumns.add(colName + TYPE_INTEGER);
        return this;
    }

    /**
     * 添加数值字段
     * @param colName 字段名称
     * @return
     */
    public TableEntity addFloatColumns(String colName) {
        tableColumns.add(colName + TYPE_FLOAT);
        return this;
    }

    /**
     * 添加数值字段
     * @param colName 字段名称
     * @return
     */
    public TableEntity addLongColumns(String colName) {
        tableColumns.add(colName + TYPE_LONG);
        return this;
    }

    /**
     * 添加浮点字段
     * @param colName 字段名称
     * @return
     */
    public TableEntity addRealColumns(String colName) {
        tableColumns.add(colName + TYPE_REAL);
        return this;
    }

    /**
     * 添加文本字段
     * @param colName 字段名称
     * @return
     */
    public TableEntity addTextColumns(String colName) {
        tableColumns.add(colName + TYPE_TEXT);
        return this;
    }

    /**
     * 添加字节字段
     * @param colName 字段名称
     * @return
     */
    public TableEntity addBlobColumns(String colName) {
        tableColumns.add(colName + TYPE_BLOB);
        return this;
    }

    /**
     * 获取创建表语句
     * @return
     */
    public String getCreateTableSql() {
        StringBuffer sb = new StringBuffer(CREATE_TABLE + tableName);
        sb.append(" ( ").append(COL__ID).append(TYPE_INTEGER_AUTOINCREMENT);
        for (int i = 0, c = tableColumns.size(); i < c; i++) {
            sb.append(",").append(tableColumns.get(i));
        }
        sb.append(" );");
        new LogEntity().append(sb.toString().replaceAll(",", ",\r\n")).toLogD("getCreateTableSql");
        return sb.toString();
    }

    /**
     * 获取删除表语句
     * @return
     */
    public String getDropTableSql() {
        StringBuffer sb = new StringBuffer(DROP_TABLE + tableName);
        new LogEntity().append(sb.toString()).toLogD("getDropTableSql");
        return sb.toString();
    }

    public Uri getTableUri() {
        return tableUri;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public TableEntity setDistinct(boolean distinct) {
        this.distinct = distinct;
        return this;
    }

    public String[] getColumns() {
        return columns;
    }

    public TableEntity setColumns(String[] columns) {
        this.columns = columns;
        return this;
    }

    public String getWhereClause() {
        return whereClause;
    }

    public TableEntity setWhereClause(String whereClause) {
        this.whereClause = whereClause;
        return this;
    }

    public TableEntity setWhereClause(String whereClause, String[] whereArgs) {
        this.whereClause = whereClause;
        this.whereArgs = whereArgs;
        return this;
    }

    public String[] getWhereArgs() {
        return whereArgs;
    }

    public String getGroupBy() {
        return groupBy;
    }

    public TableEntity setGroupBy(String groupBy) {
        this.groupBy = groupBy;
        return this;
    }

    public String getHaving() {
        return having;
    }

    public TableEntity setHaving(String having) {
        this.having = having;
        return this;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public TableEntity setOrderBy(String orderBy) {
        this.orderBy = orderBy;
        return this;
    }

    public TableEntity setOrderByAsc(String orderBy) {
        if (TxtUtils.isEmpty(orderBy)) return this;
        this.orderBy = orderBy + ORDER_BY_ASC;
        return this;
    }

    public TableEntity setOrderByDesc(String orderBy) {
        if (TxtUtils.isEmpty(orderBy)) return this;
        this.orderBy = orderBy + ORDER_BY_DESC;
        return this;
    }

    public String getLimit() {
        return limit;
    }

    public TableEntity setLimit(String limit) {
        this.limit = limit;
        return this;
    }

    public TableEntity setValues(ContentValues values) {
        this.values = values;
        return this;
    }

    public ContentValues getValues() {
        return values;
    }

    public BaseDB.IQueryListener getQueryListener() {
        return queryListener;
    }

    public TableEntity setQueryListener(BaseDB.IQueryListener queryListener) {
        this.queryListener = queryListener;
        return this;
    }

    @Override
    public String toString() {
        LogEntity log = new LogEntity().append("tableName", tableName)
                                       .append("distinct", distinct)
                                       .append("columns", columns)
                                       .append("whereClause", whereClause)
                                       .append("whereArgs", whereArgs)
                                       .append("groupBy", groupBy)
                                       .append("having", having)
                                       .append("orderBy", orderBy)
                                       .append("limit", limit);
        if (values != null && values.valueSet() != null) {
            log.appendLine("values");
            Iterator<Map.Entry<String, Object>> it = values.valueSet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> tmp = it.next();
                log.append(tmp.getKey(), tmp.getValue());
            }
        }
        return log.toString();
    }
}
