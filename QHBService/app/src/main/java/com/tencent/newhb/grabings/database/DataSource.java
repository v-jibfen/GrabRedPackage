package com.tencent.newhb.grabings.database;

import android.content.Context;

public class DataSource {

	private Context mAppContext;
	private DBHelper dbhelper;

	public DataSource(Context app) {
		mAppContext = app;
		getDb();
	}

	private synchronized DBHelper getDb() {
		if (dbhelper == null) {
			dbhelper = new DBHelper(mAppContext);
		}
		return dbhelper;
	}
}
