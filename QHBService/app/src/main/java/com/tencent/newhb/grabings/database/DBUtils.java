package com.tencent.newhb.grabings.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBUtils {

	public static final long NO_ID = -1;

	public static long replaceValues(SQLiteDatabase db, String table,
                                     ContentValues values) {
		try {
			return db.replaceOrThrow(table, null, values);
		} catch (Exception e) {
			Log.e("DBUtils", "insert " + values + " into table " + table
					+ " faile", e);
		}
		return NO_ID;
	}

}
