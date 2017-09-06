package com.tencent.newhb.grabings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.tencent.newhb.grabings.util.SharePreferenceHelper;

import java.util.Random;

public class Config {

    public static final String ACTION_QIANGHONGBAO_SERVICE_DISCONNECT = ".ACCESSBILITY_DISCONNECT";
    public static final String ACTION_QIANGHONGBAO_SERVICE_CONNECT = ".ACCESSBILITY_CONNECT";

    public static final String ACTION_NOTIFY_LISTENER_SERVICE_DISCONNECT = ".NOTIFY_LISTENER_DISCONNECT";
    public static final String ACTION_NOTIFY_LISTENER_SERVICE_CONNECT = ".NOTIFY_LISTENER_CONNECT";

    public static final String APP_CONFIG = "APP_CONFIG";
    public static final String APP_IS_FIRST_LOAD = "APP_IS_FIRST_LOAD";
    public static final String APP_DEVICE_ID = "APP_DEVICE_ID";
    public static final String APP_UID = "APP_UID";
    public static final String WX_SELECT_KEY = "WX_SELECT_KEY";
    public static final String WX_DETAIL_UI_KEY = "WX_DETAIL_UI_KEY";
    public static final String QQ_SELECT_KEY = "QQ_SELECT_KEY";
    public static final String QQ_TOKEN_SELECT_KEY = "QQ_TIKEN_SELECT_KEY";

    public static final String PREFERENCE_NAME = "config";
    public static final String KEY_ENABLE_WECHAT = "kaiqiwx";
    public static final String KEY_WECHAT_AFTER_OPEN_HONGBAO = "KEY_WECHAT_AFTER_OPEN_HONGBAO";
    public static final String KEY_WECHAT_DELAY_TIME = "KEY_WECHAT_DELAY_TIME";
    public static final String KEY_WECHAT_AFTER_GET_HONGBAO = "KEY_WECHAT_AFTER_GET_HONGBAO";
    public static final String KEY_WECHAT_MODE = "KEY_WECHAT_MODE";

    public static final String KEY_ENABLE_QQ = "kaiqiqq";

    public static final String KEY_NOTIFY_SOUND = "KEY_NOTIFY_SOUND";
    public static final String KEY_NOTIFY_VIBRATE = "KEY_NOTIFY_VIBRATE";
    public static final String KEY_NOTIFY_NIGHT_ENABLE = "KEY_NOTIFY_NIGHT_ENABLE";

    private static final String KEY_AGREEMENT = "KEY_AGREEMENT";

    public static final int WX_AFTER_OPEN_HONGBAO = 0;
    public static final int WX_AFTER_OPEN_SEE = 1;
    public static final int WX_AFTER_OPEN_NONE = 2;

    public static final int WX_AFTER_GET_GOHOME = 0; //返回桌面
    public static final int WX_AFTER_GET_GOBACK = 1; //返回聊天界面
    public static final int WX_AFTER_GET_NONE = 2; //静静地看着

    public static final int WX_MODE_0 = 0;//自动抢
    public static final int WX_MODE_1 = 1;//抢单聊红包,群聊红包只通知
    public static final int WX_MODE_2 = 2;//抢群聊红包,单聊红包只通知
    public static final int WX_MODE_3 = 3;//通知手动抢

    public static final String KEY_SETTING_MODE_YONGJIU = "yongjiu";
    public static final String KEY_SETTING_MODE_JIASU = "jiasu";
    public static final String KEY_SETTING_MODE_ZUIJIA = "zuijia";
    public static final String KEY_SETTING_MODE_FANGFENGHAO = "fangfenghao";
    public static final String KEY_SETTING_MODE_DUOBIXIAOBAO = "duobixiaobao";
    public static final String KEY_SETTING_MODE_GANRAO = "ganrao";
    public static final String KEY_SETTING_MODE_XIPING = "xiping";
    public static final String KEY_SETTING_MODE_HUIFU = "huifu";
    public static final String KEY_SETTING_MODE_QQKOULING = "qqkouling";
    public static final String KEY_SETTING_MODE_CLOSEGUANGGAO = "closeguanggao";
    public static final String KEY_NOTIFICATION_SERVICE_ENABLE = "shenmigongneng";
    public static final String KEY_SETTING_MODE_SAOLEI = "saolei";
    public static final String KEY_SETTING_MODE_NIUNIU = "niuniu";
    public static final String KEY_SETTING_MODE_WEIHAOKONGZHI = "weihaokongzhi";

    public static final String KEY_SETTING_MODE_FANHUI = "fanhui";
    public static final String KEY_SETTING_MODE_MUSICTISHI = "musictishi";
    public static final String KEY_SETTING_MODE_TONGZHILAN = "tongzhilan";
    public static final String KEY_SETTING_MODE_ZIDONGQIANG = "zidongqiang";

    public static final String KEY_MODE_SPECIAL_USER = "KEY_MODE_SPECIAL_USER";
    public static final String KEY_REPLAY_COMMENT = "KEY_REPLAY_COMMENT";

    public static final String DEFAULT_DOWNLOAD_URL = "";
    public static final int DEFAULT_SHOW_DELAYED_TIME = 10;

    public static final String KEY_ENABLE_AD = "KEY_ENABLE_AD";

    public static final String KEY_DOWNLOAD_URL = "KEY_DOWNLOAD_URL";
    public static final String KEY_SHARE_RCODE = "KEY_SHARE_RCODE";
    public static final String KEY_SHOW_DELAYED_TIME = "KEY_SHOW_DELAYED_TIME";
    public static final String APP_MODE = "APP_MODE";
    public static final String APP_IS_TEST_MODE = "APP_IS_TEST_MODE";

    private static Config current;

    public static synchronized Config getConfig(Context context) {
        if(current == null) {
            current = new Config(context.getApplicationContext());
        }
        return current;
    }

    private SharedPreferences preferences;
    private Context mContext;

    private Config(Context context) {
        mContext = context;
        preferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    /** 是否启动微信抢红包*/
    public boolean isEnableWechat() {
        return preferences.getBoolean(KEY_ENABLE_WECHAT, true) && UmengConfig.isEnableWechat(mContext);
    }

    public boolean isEnableQQ() {
        return preferences.getBoolean(KEY_ENABLE_QQ, true) && UmengConfig.isEnableQQ(mContext);
    }

    /** 微信打开红包后的事件*/
    public int getWechatAfterOpenHongBaoEvent() {
        int defaultValue = 0;
        String result =  preferences.getString(KEY_WECHAT_AFTER_OPEN_HONGBAO, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /** 微信抢到红包后的事件*/
    public int getWechatAfterGetHongBaoEvent() {
        int defaultValue = 1; //WX_AFTER_GET_NONE
        String result =  preferences.getString(KEY_WECHAT_AFTER_GET_HONGBAO, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    public int getOpenDelayTime() {
        int max = 2;
        int min = 1;
        Random random = new Random();
        int s = random.nextInt(max)%(max-min+1) + min;
        int defaultValue = s * 1000;
        Log.d("getOpenDelayTime", "DelayTime:" + defaultValue);
        if (isEnableSpeedGrab()) {
            defaultValue = 0;
        }
        String result = preferences.getString(KEY_WECHAT_DELAY_TIME, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }


    /** 获取抢微信红包的模式*/
    public int getWechatMode() {
        int defaultValue = 0;
        String result = preferences.getString(KEY_WECHAT_MODE, String.valueOf(defaultValue));
        try {
            return Integer.parseInt(result);
        } catch (Exception e) {}
        return defaultValue;
    }

    /** 是否启动快速通知栏模式*/
    public boolean isEnableNotificationService() {
        return preferences.getBoolean(KEY_NOTIFICATION_SERVICE_ENABLE, false);
    }

    public void setNotificationServiceEnable(boolean enable) {
        preferences.edit().putBoolean(KEY_NOTIFICATION_SERVICE_ENABLE, enable).apply();
    }

    /** 是否开启声音*/
    public boolean isNotifySound() {
        return preferences.getBoolean(KEY_NOTIFY_SOUND, true);
    }

    /** 是否开启震动*/
    public boolean isNotifyVibrate() {
        return preferences.getBoolean(KEY_NOTIFY_VIBRATE, true);
    }

    /** 是否开启夜间免打扰模式*/
    public boolean isNotifyNight() {
        return preferences.getBoolean(KEY_NOTIFY_NIGHT_ENABLE, false);
    }

    /** 免费声明*/
    public boolean isAgreement() {
        return preferences.getBoolean(KEY_AGREEMENT, true);
    }

    /** 设置是否同意*/
    public void setAgreement(boolean agreement) {
        preferences.edit().putBoolean(KEY_AGREEMENT, agreement).apply();
    }

    //设置回复内容
    public void setReplayCommentPreferences(String comment) {
        preferences.edit().putString(KEY_REPLAY_COMMENT, comment).apply();
    }

    //获取回复内容
    public String getReplayCommentPreferences() {
        return preferences.getString(KEY_REPLAY_COMMENT, "");
    }

    /** 是否永久开启SVIP全部功能*/
    public boolean isEnableSVIP() {
        return preferences.getBoolean(KEY_SETTING_MODE_YONGJIU, false);
    }

    /** 是否永久开启抢红包加速功能*/
    public boolean isEnableSpeedGrab() {
        return preferences.getBoolean(KEY_SETTING_MODE_JIASU, false);
    }


    /** 是否开启提高红包最佳概率*/
    public boolean isEnableBestGrab() {
        return preferences.getBoolean(KEY_SETTING_MODE_ZUIJIA, false);
    }

    /** 是否开启防封号模式*/
    public boolean isEnableAvoidKick() {
        return preferences.getBoolean(KEY_SETTING_MODE_FANGFENGHAO, false);
    }

    /** 是否开启智能干扰竞争者*/
    public boolean isEnableInterfereOther() {
        return preferences.getBoolean(KEY_SETTING_MODE_GANRAO, false);
    }

    /** 是否开启息屏抢红包*/
    public boolean isEnableScreenGrab() {
        return preferences.getBoolean(KEY_SETTING_MODE_XIPING, false);
    }

    /** 是否开启抢红包完自动回复*/
    public boolean isEnableAutoReplay() {
        return preferences.getBoolean(KEY_SETTING_MODE_HUIFU, false);
    }

    /** 是否开启QQ口令红包*/
    public boolean isEnableQQPasswordPackage() {
        return preferences.getBoolean(KEY_SETTING_MODE_QQKOULING, false);
    }

    /** 是否开启关闭广告*/
    public boolean isEnableCloseAdvertisement() {
        return preferences.getBoolean(KEY_SETTING_MODE_CLOSEGUANGGAO, false);
    }

    /** 是否开启开启自动抢红包*/
    public boolean isEnableAutoGrab() {
        return preferences.getBoolean(KEY_SETTING_MODE_ZIDONGQIANG, true);
    }

    /** 是否开启开启音乐提示*/
    public boolean isEnableMusicNotification() {
        return preferences.getBoolean(KEY_SETTING_MODE_MUSICTISHI, true);
    }

    /** 是否开启自动返回聊天页*/
    public boolean isEnableAutoBack() {
        return preferences.getBoolean(KEY_SETTING_MODE_FANHUI, true);
    }

    /** 是否开启红包提醒通知栏*/
    public boolean isEnableNotification() {
        return preferences.getBoolean(KEY_SETTING_MODE_TONGZHILAN, true);
    }

    /**
     * 设置String类型 SharePreference
     * */
    public void setSettingPreferenceWithString(String key, String value) {
        preferences.edit().putString(key, value).apply();
    }

    /**
     * 设置String类型 SharePreference
     * */
    public void setSettingPreferenceWithInt(String key, int value) {
        preferences.edit().putInt(key, value).apply();
    }

    /**
     * 设置String类型 SharePreference
     * */
    public void setSettingPreferenceWithBoolean(String key, boolean value) {
        preferences.edit().putBoolean(key, value).apply();
    }

    public boolean getSettingPreferenceWithFalse(String key) {
        return preferences.getBoolean(key, false);
    }

    public boolean getSettingPreferenceWithTrue(String key) {
        return preferences.getBoolean(key, true);
    }

    //清空preferences
    public void clearPreference() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().commit();
    }

    /** 是否是内网模式： 默认许修改 */
    public boolean isTestMode(Context context) {
        return SharePreferenceHelper.getSharePreferenceFromBooleanWithFalse(context, Config.APP_MODE, Config.APP_IS_TEST_MODE);
    }

    public static final String KEY_IS_UN_GRAB_RED_PACKAGE = "KEY_IS_UN_GRAB_RED_PACKAGE";
    public static final String KEY_RECHARGE_GUIDE = "KEY_RECHARGE_GUIDE";
}
