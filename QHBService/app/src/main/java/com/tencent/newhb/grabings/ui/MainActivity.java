package com.tencent.newhb.grabings.ui;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.tencent.newhb.grabings.App;
import com.tencent.newhb.grabings.AppHelper;
import com.tencent.newhb.grabings.BaseActivity;
import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.QiangHongBaoService;
import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.database.DBHelper;
import com.tencent.newhb.grabings.entity.PackageLog;
import com.tencent.newhb.grabings.util.ApkDownloadUtils;
import com.tencent.newhb.grabings.util.SharePreferenceHelper;
import com.tencent.newhb.grabings.widget.dialog.RechargeGuideDialog;
import com.tencent.newhb.grabings.widget.dialog.ShareDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;

import kr.pe.burt.android.lib.faimageview.FAImageView;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private Context _context;
    private Dialog mTipsDialog;
    private FAImageView _FAImageView;
    private TextView _countsView;
    private TextView _moneysView;
    private TextView _countsTitleView;
    private TextView _moneyTitleView;
    private TextView _grabStatusTextView;

    private DBHelper _DBHelper;

    private ANIMATION_STATUS _animationStatus;
    private boolean _isActive = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate");
        _context = MainActivity.this;
        _DBHelper = new DBHelper(_context);

        String action_value = getIntent().getStringExtra("action_value");
        if (action_value != null && !action_value.isEmpty()) {
            ApkDownloadUtils.download(_context, action_value);
        }

        String version = "";
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = " v" + info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.app_package_name) + Config.ACTION_QIANGHONGBAO_SERVICE_CONNECT);
        filter.addAction(getString(R.string.app_package_name) + Config.ACTION_QIANGHONGBAO_SERVICE_DISCONNECT);
        filter.addAction(getString(R.string.app_package_name) + Config.ACTION_NOTIFY_LISTENER_SERVICE_DISCONNECT);
        filter.addAction(getString(R.string.app_package_name) + Config.ACTION_NOTIFY_LISTENER_SERVICE_CONNECT);
        registerReceiver(_serviceConnectReceiver, filter);

        final ImageView top_layout_bg = (ImageView) findViewById(R.id.top_layout_bg);

        Glide.with(_context).load(R.drawable.home_top).asBitmap().centerCrop().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                top_layout_bg.setImageBitmap(resource);
            }
        });

        Button speedButton = (Button) findViewById(R.id.speed_button);
        speedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

//        Button settingButton = (Button) findViewById(R.id.setting_button);
//        settingButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
//                intent.putExtra(Config.KEY_RECHARGE_GUIDE, "svip");
//                startActivity(intent);
//            }
//        });

        Button logButton = (Button) findViewById(R.id.log_button);
        logButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogActivity.class);
                startActivity(intent);
            }
        });

        Button shareButton = (Button) findViewById(R.id.share_button);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialog shareDialog = new ShareDialog(MainActivity.this);
                shareDialog.show();
            }
        });

        Button helpButton = (Button) findViewById(R.id.help_button);
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                startActivity(intent);
            }
        });

        _grabStatusTextView = (TextView) findViewById(R.id.grab_status_text);
        _countsTitleView = (TextView) findViewById(R.id.total_counts_title);
        _moneyTitleView = (TextView) findViewById(R.id.total_money_title);
        _countsView = (TextView) findViewById(R.id.total_counts);
        _moneysView = (TextView) findViewById(R.id.total_moneys);

        _FAImageView = (FAImageView) findViewById(R.id.animation_view);

        _FAImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccessibilityServiceSettings();
            }
        });

        //leftEvent();
        rightEvent();
//        testToast();

        //开启广告
//        showAdvertisement();

        //AppVersionHelper.checkVersionLogic(_context);
    }

    private BroadcastReceiver _serviceConnectReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(isFinishing()) {
                return;
            }
            String action = intent.getAction();
            Log.d("MainActivity", "receive-->" + action);
            if((getString(R.string.app_package_name) + Config.ACTION_QIANGHONGBAO_SERVICE_CONNECT).equals(action)) {
                if (mTipsDialog != null) {
                    mTipsDialog.dismiss();
                }

                updateAnimationStatus(ANIMATION_STATUS.OPENING);
            } else if((getString(R.string.app_package_name) + Config.ACTION_QIANGHONGBAO_SERVICE_DISCONNECT).equals(action)) {
                showOpenAccessibilityServiceDialog();
                updateAnimationStatus(ANIMATION_STATUS.IDLE);
            } else if((getString(R.string.app_package_name) + Config.ACTION_NOTIFY_LISTENER_SERVICE_CONNECT).equals(action)) {
                Log.d("MainActivity", "receive-->" + "NotificationService connect");

            } else if((getString(R.string.app_package_name) + Config.ACTION_NOTIFY_LISTENER_SERVICE_DISCONNECT).equals(action)) {
                Log.d("MainActivity", "receive-->" + "NotificationService disconnect");

            }
        }
    };

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        showRechargeReminderDialog(intent);

        //开启广告
//        showAdvertisement();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        _isActive = true;

        if(QiangHongBaoService.isRunning()) {
            if(mTipsDialog != null) {
                mTipsDialog.dismiss();
            }
            updateAnimationStatus(ANIMATION_STATUS.OPENING);
        } else {
            showOpenAccessibilityServiceDialog();
            updateAnimationStatus(ANIMATION_STATUS.IDLE);
        }

        setCountsAndMoney();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        _isActive = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        try {
            unregisterReceiver(_serviceConnectReceiver);
        } catch (Exception e) {}
        mTipsDialog = null;
    }

    private void showRechargeReminderDialog(Intent intent) {
        boolean isShow = intent.getBooleanExtra(Config.KEY_IS_UN_GRAB_RED_PACKAGE, false);
        Log.d(TAG, "isShow ---->" + isShow);
        if (isShow && !Config.getConfig(_context).isEnableSVIP()) {
            RechargeGuideDialog rechargeGuideDialog = new RechargeGuideDialog(_context);
            rechargeGuideDialog.show();
        };
    }

    /** 显示未开启辅助服务的对话框*/
    private void showOpenAccessibilityServiceDialog() {
        if(mTipsDialog != null && mTipsDialog.isShowing()) {
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.dialog_tips_layout, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.tip_image_reminder);
        Glide.with(_context).load(R.drawable.open_service_tips).centerCrop().into(imageView);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAccessibilityServiceSettings();
            }
        });
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.open_service_title);
        builder.setView(view);
        builder.setPositiveButton(R.string.open_service_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                openAccessibilityServiceSettings();
            }
        });
        mTipsDialog = builder.show();
    }

    /** 打开辅助服务的设置*/
    private void openAccessibilityServiceSettings() {
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
            Toast.makeText(this, String.format(getString(R.string.open_service_reminder_text), getString(R.string.app_name)), Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateAnimationStatus(ANIMATION_STATUS status) {
        if (_animationStatus != null && _animationStatus == status) {
            return;
        }

        _animationStatus = status;
        _FAImageView.reset();
        switch (status) {
            case IDLE:
                _FAImageView.setInterval(250);
                _FAImageView.setLoop(true);
                _FAImageView.setRestoreFirstFrameWhenFinishAnimation(false);
                _FAImageView.addImageFrame(R.drawable.icon_lucky_cat_1);
                _FAImageView.addImageFrame(R.drawable.icon_lucky_cat_2);
                _FAImageView.addImageFrame(R.drawable.icon_lucky_cat_3);
                _FAImageView.addImageFrame(R.drawable.icon_lucky_cat_4);
                _FAImageView.startAnimation();

                _grabStatusTextView.setText(getString(R.string.text_grab_status_stop));
                break;

            case OPENING:
                _FAImageView.setInterval(100);
                _FAImageView.setLoop(true);
                _FAImageView.setRestoreFirstFrameWhenFinishAnimation(false);
                _FAImageView.addImageFrame(R.drawable.icon_lucky_cat_5);
                _FAImageView.addImageFrame(R.drawable.icon_lucky_cat_6);
                _FAImageView.addImageFrame(R.drawable.icon_lucky_cat_7);
                _FAImageView.addImageFrame(R.drawable.icon_lucky_cat_8);
                _FAImageView.startAnimation();
                _grabStatusTextView.setText(getString(R.string.text_grab_status_opening));
                break;
        }
    }

    public enum ANIMATION_STATUS {
        IDLE, OPENING
    }

    private void setCountsAndMoney() {
        _moneysView.setText(String.format(getString(R.string.total_count_text), String.valueOf(getTotalMoney())));
        _countsView.setText(String.format(getString(R.string.total_count_text), String.valueOf(getRedPackages())));
    }

    //获取已经抢到的红包个数
    private int getRedPackages() {
        if (_DBHelper == null) {
            return 0;
        }

        return _DBHelper.getLogList().size();
    }

    private String getTotalMoney() {
        if (_DBHelper == null) {
            return "0.00";
        }
        ArrayList<PackageLog> packageLogs = _DBHelper.getLogList();
        float totalMoney = (float) 0.00;
        for (int i = 0; i < packageLogs.size(); i ++) {
            try {
                totalMoney += Float.parseFloat(packageLogs.get(i).getMoney());
            } catch (Exception e) {
                _DBHelper.deleteLogById(packageLogs.get(i).getId());
            }

        }

        DecimalFormat decimalFormat = new DecimalFormat("0.00");

        String s = decimalFormat.format(totalMoney);
        Log.d(TAG, "getTotalMoney : " + s);

        return s;
    }

    private void showAdvertisement() {
        int ad_on_off = SharePreferenceHelper.getSharePreferenceFromInteger(_context, Config.APP_CONFIG, Config.KEY_ENABLE_AD);
        if (ad_on_off == 1 && !Config.getConfig(_context).isEnableCloseAdvertisement()) {
            int delayedTime = SharePreferenceHelper.getSharePreferenceFromInteger(_context, Config.APP_CONFIG, Config.KEY_SHOW_DELAYED_TIME);
            if (delayedTime == 0) {
                delayedTime = Config.DEFAULT_SHOW_DELAYED_TIME;
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //广告
                }
            }, delayedTime * 1000);
        }
    }

    //成为超级用户
    private void leftEvent() {

        _countsTitleView.setOnClickListener(new View.OnClickListener() {
            int time = 0;
            int count = 0;
            int lastTime = 0;

            @Override
            public void onClick(View v) {
                switch (count) {
                    case 0:
                        time = (int) System.currentTimeMillis();
                        count++;
                        break;
                    case 1:
                        lastTime = (int) System.currentTimeMillis();
                        if (lastTime - time < 200)
                            count++;
                        else count = 0;
                        break;
                    case 2:
                        time = (int) System.currentTimeMillis();
                        if (time - lastTime < 200)
                            count++;
                        else count = 0;
                        break;
                    case 3:
                        lastTime = (int) System.currentTimeMillis();
                        if (lastTime - time < 200)
                            count++;
                        else count = 0;
                        break;
                    case 4:
                        count = 0;
                        boolean isSpecialUser = Config.getConfig(_context).getSettingPreferenceWithFalse(Config.KEY_MODE_SPECIAL_USER);
                        if (!isSpecialUser) {
                            Config.getConfig(_context).setSettingPreferenceWithBoolean(Config.KEY_MODE_SPECIAL_USER, true);
                            Toast.makeText(_context, "您已成为超级用户", Toast.LENGTH_LONG).show();
                        } else {
                            Config.getConfig(_context).setSettingPreferenceWithBoolean(Config.KEY_MODE_SPECIAL_USER, false);
                            Toast.makeText(_context, "您不再是超级用户", Toast.LENGTH_LONG).show();
                        }
                        break;
                }
            }
        });
    }

    private void rightEvent() {
        _moneyTitleView.setOnClickListener(new View.OnClickListener() {
            int time = 0;
            int count = 0;
            int lastTime = 0;

            @Override
            public void onClick(View v) {
                switch (count) {
                    case 0:
                        time = (int) System.currentTimeMillis();
                        count++;
                        break;
                    case 1:
                        lastTime = (int) System.currentTimeMillis();
                        if (lastTime - time < 200)
                            count++;
                        else count = 0;
                        break;
                    case 2:
                        time = (int) System.currentTimeMillis();
                        if (time - lastTime < 200)
                            count++;
                        else count = 0;
                        break;
                    case 3:
                        lastTime = (int) System.currentTimeMillis();
                        if (lastTime - time < 200)
                            count++;
                        else count = 0;
                        break;
                    case 4:
                        count = 0;

                        Config.getConfig(_context).clearPreference();

                        if (Config.getConfig(_context).isTestMode(_context)) {
                            SharePreferenceHelper.saveSharePreferenceFromBoolean(_context, Config.APP_MODE, Config.APP_IS_TEST_MODE, false);
                            Toast.makeText(_context, "切换到外网", Toast.LENGTH_LONG).show();
                        } else {
                            SharePreferenceHelper.saveSharePreferenceFromBoolean(_context, Config.APP_MODE, Config.APP_IS_TEST_MODE, true);
                            Toast.makeText(_context, "切换到内网", Toast.LENGTH_LONG).show();
                        }

                        AppHelper.restartApplication(App.getInstance());
                        break;
                }
            }
        });
    }
}
