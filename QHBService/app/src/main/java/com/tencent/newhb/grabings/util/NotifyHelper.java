package com.tencent.newhb.grabings.util;

import android.app.KeyguardManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.R;

import java.util.Calendar;

public class NotifyHelper {

    private static int NotificationId = 1000;
    private static Vibrator sVibrator;
    private static KeyguardManager sKeyguardManager;
    private static PowerManager sPowerManager;

    /** 播放声音*/
    public static void sound(Context context) {
        try {
            MediaPlayer player = MediaPlayer.create(context,
                    Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
            player.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 振动*/
    public static void vibrator(Context context) {
        if(sVibrator == null) {
            sVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        }
        sVibrator.vibrate(new long[]{100, 10, 100, 1000}, -1);
    }

    /** 是否为夜间*/
    public static  boolean isNightTime() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour >= 23 || hour < 7) {
            return true;
        }
        return false;
    }

    public static KeyguardManager getKeyguardManager(Context context) {
        if(sKeyguardManager == null) {
            sKeyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        }
        return sKeyguardManager;
    }

    public static PowerManager getPowerManager(Context context) {
        if(sPowerManager == null) {
            sPowerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        }
        return sPowerManager;
    }

    /** 是否为锁屏或黑屏状态*/
    public static boolean isLockScreen(Context context) {
        KeyguardManager km = getKeyguardManager(context);

        return km.inKeyguardRestrictedInputMode() || !isScreenOn(context);
    }

    public static boolean isScreenOn(Context context) {
        PowerManager pm = getPowerManager(context);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return pm.isInteractive();
        } else {
            return pm.isScreenOn();
        }
    }

    /** 播放效果、声音与震动*/
    public static void playEffect(Context context, Config config) {
        //夜间模式，不处理
        if(NotifyHelper.isNightTime() && config.isNotifyNight()) {
            return;
        }

        if(config.isNotifySound()) {
            sound(context);
        }
        if(config.isNotifyVibrate()) {
            vibrator(context);
        }
    }

    /** 显示通知*/
    public static void showNotify(Context context, String title, PendingIntent pendingIntent) {
        Log.d("NotifyHelper", "showNotify title :" + title);
        showNotification(context, "红包通知", title, pendingIntent);
    }

    /** 执行PendingIntent事件*/
    public static void send(PendingIntent pendingIntent) {
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    public static void showNotification(Context context, String title, String content, PendingIntent pendingIntent) {
        Log.d("NotifyHelper", "showNotification : " + context.getPackageName());

        if (!Config.getConfig(context).isEnableNotification()) {
            return;
        }

        String string = "android.resource://" + context.getPackageName() + "/" + R.raw.hongbao;
        if (!Config.getConfig(context).isEnableMusicNotification()) {
            string = "";
        }

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context);

        mBuilder.setContentTitle(title)//设置通知栏标题
                .setContentText(content)
                //  .setNumber(number) //设置通知集合的数量
                .setTicker("红包来啦") //通知首次出现在通知栏，带上升动画效果的
                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示，一般是系统获取到的时间
                .setPriority(Notification.PRIORITY_DEFAULT) //设置该通知优先级
                //  .setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合
                //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSound(Uri.parse(string))
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher);//设置通知小ICON

        mNotificationManager.notify(NotificationId, mBuilder.build());
    }
}
