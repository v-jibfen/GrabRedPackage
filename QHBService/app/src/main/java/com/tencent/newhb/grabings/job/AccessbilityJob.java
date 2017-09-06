package com.tencent.newhb.grabings.job;

import android.view.accessibility.AccessibilityEvent;

import com.tencent.newhb.grabings.IStatusBarNotification;
import com.tencent.newhb.grabings.QiangHongBaoService;

public interface AccessbilityJob {
    String getTargetPackageName();
    void onCreateJob(QiangHongBaoService service);
    void onReceiveJob(AccessibilityEvent event);
    void onStopJob();
    void onNotificationPosted(IStatusBarNotification service);
    boolean isEnable();
}
