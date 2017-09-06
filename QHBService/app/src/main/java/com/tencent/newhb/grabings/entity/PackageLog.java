package com.tencent.newhb.grabings.entity;

import android.view.accessibility.AccessibilityNodeInfo;

import com.tencent.newhb.grabings.database.CursorHelper;
import com.tencent.newhb.grabings.database.DataSchema;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PackageLog {

    private String id;
    private String source;
    private String nickname;
    private String money;
    private String time;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public static PackageLog fromCursorHelper(CursorHelper helper) {
        PackageLog packageLog = new PackageLog();
        packageLog.setId(helper.getString(DataSchema.LogTable._ID));
        packageLog.setSource(helper.getString(DataSchema.LogTable.LOG_SOURCE));
        packageLog.setNickname(helper.getString(DataSchema.LogTable.LOG_NICKNAME));
        packageLog.setMoney(helper.getString(DataSchema.LogTable.LOG_MONEY));
        packageLog.setTime(helper.getString(DataSchema.LogTable.LOG_TIME));

        return packageLog;
    }

    public static PackageLog getWeChatInfo(AccessibilityNodeInfo node, String source) {
        PackageLog packageLog = new PackageLog();
        AccessibilityNodeInfo hongbaoNode = node.getParent();
        if (hongbaoNode == null) {
            return null;
        }
        if (hongbaoNode.getChildCount() > 0) {
            if (hongbaoNode.getChild(0) != null && hongbaoNode.getChild(0).getText() != null) {
                packageLog.setNickname(hongbaoNode.getChild(0).getText().toString().replace("的红包", ""));
            }
        }

        if (hongbaoNode.getChildCount() > 2) {
            if (hongbaoNode.getChild(2) != null && hongbaoNode.getChild(2).getText() != null) {
                packageLog.setMoney(hongbaoNode.getChild(2).getText().toString());
            }
        }

        packageLog.setTime(getStringTime());
        packageLog.setSource(source);

        if (packageLog.getNickname() == null) {
            packageLog.setNickname("未知");
        }
        if (packageLog.getNickname().isEmpty()) {
            packageLog.setNickname("未知");
        }
        return packageLog;
    }

    public static PackageLog getQQInfo(AccessibilityNodeInfo node, String source, int versionCode) {

        PackageLog packageLog = new PackageLog();
        AccessibilityNodeInfo hongbaoNode = node.getParent();
        if (hongbaoNode == null) {
            return null;
        }
        if (versionCode > 450) { //6.6.2
            if (hongbaoNode.getChildCount() > 5) {
                if (hongbaoNode.getChild(5) != null && hongbaoNode.getChild(5).getText() != null) {
                    packageLog.setNickname(hongbaoNode.getChild(5).getText().toString().replace("的红包", ""));
                }
            }

        }else {
            if (hongbaoNode.getChildCount() > 1) {
                if (hongbaoNode.getChild(1) != null && hongbaoNode.getChild(1).getText() != null) {
                    packageLog.setNickname(hongbaoNode.getChild(1).getText().toString().replace("来自", ""));
                }
            }
        }

        if (versionCode > 450) { //6.6.2
            if (hongbaoNode.getChildCount() > 2) {
                if (hongbaoNode.getChild(2) != null && hongbaoNode.getChild(2).getText() != null) {
                    packageLog.setMoney(hongbaoNode.getChild(2).getText().toString());
                }
            }

        } else {
            if (hongbaoNode.getChildCount() > 3) {
                if (hongbaoNode.getChild(3) != null && hongbaoNode.getChild(3).getText() != null) {
                    packageLog.setMoney(hongbaoNode.getChild(3).getText().toString());
                }
            }
        }

        packageLog.setTime(getStringTime());
        packageLog.setSource(source);

        if (packageLog.getNickname() == null) {
            packageLog.setNickname("未知");
        }

        if (packageLog.getNickname().isEmpty()) {
            packageLog.setNickname("未知");
        }
        return packageLog;
    }

    private static String getStringTime() {
        Date date = new Date();
        return getTimeOfHourMinute(date);
    }

    public static String getTimeOfHourMinute(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm");
        return format.format(date);
    }
}
