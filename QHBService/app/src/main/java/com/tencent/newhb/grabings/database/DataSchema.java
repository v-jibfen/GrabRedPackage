package com.tencent.newhb.grabings.database;

import android.provider.BaseColumns;

public interface DataSchema {

	public static final int DATABASE_VERSION = 1;
	public static final String DROP_TABLE = "drop table if exists ";
	public static final String DATABASE_NAME = "qhbsq.db ";

	public interface LogTable extends BaseColumns {

		String TABLE_NAME = "qhbsq_log";
		String LOG_SOURCE = "log_source";
		String LOG_NICKNAME = "log_nickname";
		String LOG_MONEY = "log_money";
		String LOG_TIME = "log_time";

		String DROP_TABLE_SQL = DROP_TABLE + TABLE_NAME;
		String CREATE_TABLE_SQL = "create table " + TABLE_NAME + "(" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT,"
				+ LOG_SOURCE + " text,"
				+ LOG_NICKNAME + " text,"
				+ LOG_MONEY + " text,"
				+ LOG_TIME + " text"
				+ ");";
	}
}
