package com.tencent.newhb.grabings;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.PowerManager;

public class ScreenAndLockService extends Service {
    // 键盘管理器
    KeyguardManager mKeyguardManager;
    // 键盘锁
    private KeyguardManager.KeyguardLock mKeyguardLock;
    // 电源管理器
    private PowerManager mPowerManager;
    // 唤醒锁
    private PowerManager.WakeLock mWakeLock;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        System.out.println("----> 开启服务");
        mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

    }

    @Override
    public void onStart(Intent intent, int startId) {
        // 点亮亮屏
        mWakeLock = mPowerManager.newWakeLock
                (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "Tag");
        mWakeLock.acquire();
        // 初始化键盘锁
        mKeyguardLock = mKeyguardManager.newKeyguardLock("");
        // 键盘解锁
        mKeyguardLock.disableKeyguard();
    }

    //一定要释放唤醒锁和恢复键盘
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mWakeLock != null) {
            System.out.println("----> 终止服务,释放唤醒锁");
            mWakeLock.release();
            mWakeLock = null;
        }
        if (mKeyguardLock!=null) {
            System.out.println("----> 终止服务,重新锁键盘");
            mKeyguardLock.reenableKeyguard();
        }
    }
}
