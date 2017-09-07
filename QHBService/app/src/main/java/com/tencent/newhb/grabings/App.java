package com.tencent.newhb.grabings;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.widget.Toast;

import com.tencent.newhb.grabings.util.SharePreferenceHelper;
import com.umeng.analytics.MobclickAgent;

public class App extends MultiDexApplication {
    private static App _app;

    public static App getInstance() {
        return _app;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        _app = this;

        SharePreferenceHelper.saveSharePreferenceFromString(this, Config.APP_CONFIG,
                Config.APP_DEVICE_ID, AppHelper.getUniqueId(this));
        MobclickAgent.onProfileSignIn(SharePreferenceHelper.getSharePreferenceFromString(this,
                Config.APP_CONFIG, Config.APP_DEVICE_ID));
        //注册广播接收器，用户网络请求是弹出Toast
        IntentFilter intentFilter = new IntentFilter(Constants.SHOW_TOAST);
        registerReceiver(_broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver _broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context, intent.getStringExtra("msg"), Toast.LENGTH_SHORT).show();
        }
    };
}
