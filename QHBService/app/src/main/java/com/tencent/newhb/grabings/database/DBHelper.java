package com.tencent.newhb.grabings.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.tencent.newhb.grabings.entity.PackageLog;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper implements DataSchema {

    private final static String TAG_LOG = DBHelper.class.getName();

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG_LOG, "DBHelper onCreate call");
        db.execSQL(LogTable.CREATE_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        deleteAllTable(db);
        onCreate(db);
    }

    private void deleteAllTableExceptAccount(SQLiteDatabase db) {
        db.execSQL(LogTable.DROP_TABLE_SQL);
    }

    private void deleteAllTable(SQLiteDatabase db) {
        deleteAllTableExceptAccount(db);

    }

    public void beginTransaction() {
        getWritableDatabase().beginTransaction();
    }

    public void endTransaction() {
        getWritableDatabase().endTransaction();
    }

    public void setTransactionSuccessful() {
        getWritableDatabase().setTransactionSuccessful();
    }

    protected int update(String table, ContentValues values, String where, String[] whereArgs) {
        int numRows = 0;
        try {
            beginTransaction();
            numRows = getWritableDatabase().update(table, values, where, whereArgs);
            setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
            numRows = -1;
        } finally {
            endTransaction();
        }
        return numRows;
    }

    protected long insert(String table, ContentValues values) {
        long rowId = 0;
        try {
            beginTransaction();
            rowId = getWritableDatabase().insert(table, null, values);
            setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }

        return rowId;
    }

    protected Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit) {
        return getReadableDatabase().query(table, columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    protected int delete(String table, String where, String[] whereArgs) {
        int numRows = 0;
        try {
            beginTransaction();
            numRows = getWritableDatabase().delete(table, where, whereArgs);
            setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endTransaction();
        }

        return numRows;
    }

    public boolean insertLog(PackageLog log) {
        boolean succeed = false;

        try {
            beginTransaction();
            deleteLogById(log.getId());
            String sql = "insert into " + LogTable.TABLE_NAME + "(" + LogTable.LOG_SOURCE + ","
                    + LogTable.LOG_NICKNAME + "," + LogTable.LOG_MONEY + "," + LogTable.LOG_TIME +") values (?,?,?,?)";
            SQLiteStatement insert = getWritableDatabase().compileStatement(sql);
            int index = 1;

            insert.bindString(index++, log.getSource());
            insert.bindString(index++, log.getNickname());
            insert.bindString(index++, log.getMoney());
            insert.bindString(index++, log.getTime());

            insert.execute();
            insert.close();
            setTransactionSuccessful();
            succeed = true;
        } catch (SQLException e) {
            e.printStackTrace();
            succeed = false;
        } finally {
            endTransaction();
        }
        return succeed;
    }

    public ArrayList<PackageLog> getLogList() {
        Cursor cursor = query(LogTable.TABLE_NAME, null, null, null, null, null, LogTable.LOG_TIME + " DESC", "1000");
        int rowNums = cursor.getCount();
        cursor.moveToFirst();
        ArrayList<PackageLog> packageLogs = new ArrayList<>(rowNums);
        for (int i = 0; i < rowNums; i++) {
            PackageLog s = PackageLog.fromCursorHelper(new CursorHelper(cursor));
            packageLogs.add(s);
            cursor.moveToNext();
        }
        cursor.close();

        Log.d(TAG_LOG, packageLogs.toArray().toString());
        return packageLogs;
    }

    public int deleteLogById(String id) {
        return delete(LogTable.TABLE_NAME, LogTable._ID + "=?", new String[]{id});
    }

    public int deleteLogList() {
        return delete(LogTable.TABLE_NAME, null, null);
    }
}
