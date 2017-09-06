package com.tencent.newhb.grabings.ui;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.tencent.newhb.grabings.AppHelper;
import com.tencent.newhb.grabings.BaseActivity;
import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.QiangHongBaoService;
import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.entity.Payment;
import com.tencent.newhb.grabings.entity.Plans;
import com.tencent.newhb.grabings.ui.adapter.SettingAdapter;
import com.tencent.newhb.grabings.util.SharePreferenceHelper;
import com.tencent.newhb.grabings.widget.NavigationTopBar;

import java.util.ArrayList;

public class SettingActivity extends BaseActivity {

    private static final String TAG = SettingActivity.class.getSimpleName();
    private Context _context;
    private NavigationTopBar _navigationTopBar;
    private RecyclerView _settingRecyclerView;
    private SettingAdapter _settingAdapter;

    private ArrayList<Plans> _plansData = new ArrayList<>();
    private boolean _notificationChangeByUser;
    private String _payWords;
    private ProgressDialog mProgressDialog;
    private String _currentOrderId;
    private ArrayList<Payment> _paymentsData = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        _context = SettingActivity.this;
        SharePreferenceHelper.saveSharePreferenceFromString(getApplication(), Config.APP_CONFIG,
                Config.APP_DEVICE_ID, AppHelper.getUniqueId(getApplication()));

        _navigationTopBar = (NavigationTopBar) getSupportFragmentManager().findFragmentByTag(NavigationTopBar.class.getSimpleName());
        _navigationTopBar.setText(NavigationTopBar.NAVIGATION_VIEWS.TITLE, R.string.setting_text);
        _navigationTopBar.setViewVisibility(NavigationTopBar.NAVIGATION_VIEWS.RIGHT, View.GONE);

        _navigationTopBar.setClickListener(NavigationTopBar.NAVIGATION_VIEWS.LEFT, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        _settingRecyclerView = (RecyclerView) findViewById(R.id.setting_recycler_view);
        _settingRecyclerView.setLayoutManager(new LinearLayoutManager(_context));


        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.app_package_name) + Config.ACTION_NOTIFY_LISTENER_SERVICE_DISCONNECT);
        filter.addAction(getString(R.string.app_package_name) + Config.ACTION_NOTIFY_LISTENER_SERVICE_CONNECT);
        registerReceiver(_serviceConnectReceiver, filter);

        generateSettingData();
        generateSettingAdapter();
    }

    private void settingItemClickAction(final Plans plans, boolean isCanCheck) {
        if (isCanCheck) {
            boolean flag;
            switch (plans.getWords()) {
                case Config.KEY_ENABLE_QQ:
                case Config.KEY_ENABLE_WECHAT:
                case Config.KEY_SETTING_MODE_FANHUI:
                case Config.KEY_SETTING_MODE_MUSICTISHI:
                case Config.KEY_SETTING_MODE_TONGZHILAN:
                case Config.KEY_SETTING_MODE_ZIDONGQIANG:
                    flag = getConfig().getSettingPreferenceWithTrue(plans.getWords());
                    break;
                default:
                    flag = getConfig().getSettingPreferenceWithFalse(plans.getWords());
                    break;
            }
            getConfig().setSettingPreferenceWithBoolean(plans.getWords(), !flag);

            //神秘功能特别处理
            if (plans.getWords().equals(Config.KEY_NOTIFICATION_SERVICE_ENABLE)) {
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    Toast.makeText(_context, "该功能只支持安卓4.3以上的系统", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (flag) {
                    //关闭快速通知栏模式
                    updateNotifyPreference();
                    Toast.makeText(_context, "神秘模式已关闭", Toast.LENGTH_SHORT).show();
                }

                if(!_notificationChangeByUser) {
                    _notificationChangeByUser = true;
                    return;
                }

                getConfig().setNotificationServiceEnable(!flag);

                if(!flag && !QiangHongBaoService.isNotificationServiceRunning()) {
                    //开启快速通知栏模式
                    openNotificationServiceSettings();
                    return;
                }
            }

            //处理只有确定的时候特别开关
            if (!flag) {
                switch (plans.getWords()) {
                    //自动回复
                    case Config.KEY_SETTING_MODE_HUIFU:
                        showCommentInputDialog(getConfig().getReplayCommentPreferences());
                        break;
                }
            }
        }
    }

    private void generateSettingAdapter() {
        _settingAdapter = new SettingAdapter(_context, _plansData);
        _settingRecyclerView.setAdapter(_settingAdapter);

        _settingAdapter.setSettingItemClickListener(new SettingAdapter.SettingItemClickListener() {
            @Override
            public void onItemClick(Plans plans, boolean isCanCheck) {

                settingItemClickAction(plans, isCanCheck);

            }
        });

        for (int i = 0; i < _plansData.size(); i++) {
            if (_plansData.get(i).getWords().equals(Config.KEY_SETTING_MODE_YONGJIU)) {
                if (_plansData.get(i).getActive() == 1) {
                    String is_first_load = SharePreferenceHelper.getSharePreferenceFromString(_context, Config.APP_CONFIG, Config.APP_IS_FIRST_LOAD);
                    if (is_first_load.isEmpty()) {
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_YONGJIU, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_JIASU, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_ZUIJIA, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_FANGFENGHAO, false);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_GANRAO, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_XIPING, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_QQKOULING, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_HUIFU, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_DUOBIXIAOBAO, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_CLOSEGUANGGAO, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_SAOLEI, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_NIUNIU, true);
                        getConfig().setSettingPreferenceWithBoolean(Config.KEY_SETTING_MODE_WEIHAOKONGZHI, true);

                        SharePreferenceHelper.saveSharePreferenceFromString(_context, Config.APP_CONFIG, Config.APP_IS_FIRST_LOAD, Config.APP_IS_FIRST_LOAD);
                    }
                }
            }
        }
    }

    private void generateSettingData() {
        _plansData.clear();
        String[] setting_plans = getResources().getStringArray(R.array.setting_type_plan_id);
        String[] setting_names = getResources().getStringArray(R.array.setting_type_names);
        String[] setting_prices = getResources().getStringArray(R.array.setting_type_price);
        String[] setting_actives = getResources().getStringArray(R.array.setting_type_active);
        String[] setting_words = getResources().getStringArray(R.array.setting_type_words);
        for (int i = 0; i < setting_plans.length; i ++) {
            Plans plans = new Plans();
            plans.setPlanId(setting_plans[i]);
            plans.setName(setting_names[i]);
            plans.setPrice(setting_prices[i]);
            plans.setActive(Integer.parseInt(setting_actives[i]));
            plans.setWords(setting_words[i]);
            _plansData.add(plans);
        }
    }

    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;

    public Config getConfig() {
        return Config.getConfig(this);
    }

    private void showCommentInputDialog(String text) {
        View view_input = LayoutInflater.from(this).inflate(R.layout.layout_input_view, null);
        final EditText inputServer = (EditText) view_input.findViewById(R.id.input_view);
        inputServer.setText(text);
        inputServer.setFocusable(true);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.dialog_modify_text))
                .setView(view_input)
                .setNegativeButton(getString(R.string.dialog_cancel_text), null);
        builder.setPositiveButton(getString(R.string.dialog_confirm_text),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String comment = inputServer.getText().toString();
                        getConfig().setReplayCommentPreferences(comment);
                    }
                });
        builder.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateNotifyPreference();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(_serviceConnectReceiver);
        } catch (Exception e) {}
    }

    /** 更新快速读取通知的设置*/
    public void updateNotifyPreference() {
        boolean running = QiangHongBaoService.isNotificationServiceRunning();
        boolean enable = getConfig().isEnableNotificationService();
        if( enable && running) {
            _notificationChangeByUser = false;
        } else if((!enable || !running)) {
            _notificationChangeByUser = false;
        }
    }

    /** 打开通知栏设置*/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    private void openNotificationServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, R.string.open_service_reminder_text, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver _serviceConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isFinishing()) {
                return;
            }
            String action = intent.getAction();
            Log.d(TAG, "receive-->" + action);
            if((getString(R.string.app_package_name) + Config.ACTION_NOTIFY_LISTENER_SERVICE_CONNECT).equals(action)) {
                Log.d(TAG, "receive-->" + "NotificationService connect");

                updateNotifyPreference();
            } else if((getString(R.string.app_package_name) + Config.ACTION_NOTIFY_LISTENER_SERVICE_DISCONNECT).equals(action)) {
                Log.d(TAG, "receive-->" + "NotificationService disconnect");

                updateNotifyPreference();
            }
        }
    };
}
