package com.tencent.newhb.grabings.util;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by MSI05 on 2016/12/26.
 */
public class ScreenUtils {
    private static PowerManager mPowerManager = null;
    private static KeyguardManager mKeyguardManager = null;
    // 点亮亮屏
    private static PowerManager.WakeLock mWakeLock = null;
    // 初始化键盘锁
    private static KeyguardManager.KeyguardLock mKeyguardLock = null;

    public static void openScreen(Context context) {
        mPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        mWakeLock = mPowerManager.newWakeLock
                (PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        mKeyguardLock = mKeyguardManager.newKeyguardLock("unlock");
        mWakeLock.acquire(1000);
        mWakeLock.release();

        // 键盘解锁
        mKeyguardLock.disableKeyguard();
    }

    public static void recoverScreen() {
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

    public static boolean isLockScreen(Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     *
     * @param context
     * @param dipValue 要转换的dp值
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     *
     * @param context
     * @param pxValue 要转换的px值
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 获得屏幕宽度
     *
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context) {
        if (context == null) {
            return 0;
        }
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 获得屏幕高度
     *
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.heightPixels;
    }
}
