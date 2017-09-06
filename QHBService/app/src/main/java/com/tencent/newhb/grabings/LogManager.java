package com.tencent.newhb.grabings;

import android.content.Context;

import com.tencent.newhb.grabings.database.DBHelper;
import com.tencent.newhb.grabings.entity.PackageLog;

import java.util.ArrayList;

public class LogManager {

    private static LogManager INSTANCE = new LogManager();

    private DBHelper _DBHelper;

    private LogManager() {

    }

    private LogManager getInstance(Context context) {
        _DBHelper = new DBHelper(context);

        return INSTANCE;
    }

    public void insertLog(String source, String nickname, String money, String time) {
        PackageLog packageLog = new PackageLog();
        packageLog.setSource(source);
        packageLog.setNickname(nickname);
        packageLog.setMoney(money);
        packageLog.setTime(time);

        _DBHelper.insertLog(packageLog);
    }

    public ArrayList<PackageLog> getLogs() {
        return _DBHelper.getLogList();
    }
}
