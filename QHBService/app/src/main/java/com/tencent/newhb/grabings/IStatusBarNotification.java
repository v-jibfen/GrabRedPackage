package com.tencent.newhb.grabings;

import android.app.Notification;

public interface IStatusBarNotification {

    String getPackageName();
    Notification getNotification();
}
