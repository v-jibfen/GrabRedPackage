package com.tencent.newhb.grabings.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tencent.newhb.grabings.AppHelper;
import com.tencent.newhb.grabings.BaseActivity;
import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.Constants;
import com.tencent.newhb.grabings.PageJumpHelper;
import com.tencent.newhb.grabings.R;
import com.tencent.newhb.grabings.custom.CustomCountDownTimer;
import com.tencent.newhb.grabings.util.ApkDownloadUtils;
import com.tencent.newhb.grabings.util.JavaUtils;
import com.tencent.newhb.grabings.util.SharePreferenceHelper;
import com.umeng.analytics.MobclickAgent;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private Context _context;
    private ImageView _splashBg;
    private ImageView _splashHolder;
    private TextView _splashJump;

    private CustomCountDownTimer _customCountDownTimer;
    private int _defaultSecond = 5;
    private boolean _isManualJump = false;
    public boolean _canJump = false;

    private PackageInfo mWechatPackageInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Beta.applyTinkerPatch(getApplicationContext(), Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk");

        setContentView(R.layout.activity_splash);

        _context = SplashActivity.this;

        _splashHolder = (ImageView) findViewById(R.id.splash_holder);
        _splashBg = (ImageView) findViewById(R.id.splash_bg);
        _splashJump = (TextView) findViewById(R.id.splash_jump);

        Glide.with(_context).load(R.drawable.default_splash).centerCrop().into(_splashBg);
        Glide.with(_context).load(R.drawable.icon_holder).fitCenter().into(_splashHolder);


        updatePackageInfo();

        loadDefaultBg(null, null);
    }

    private void initJumpButton() {
        _splashJump.setText(String.format(getString(R.string.skip), _defaultSecond));

        _splashJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _isManualJump = true;
                _customCountDownTimer.cancel();
                startMainActivity();
            }
        });

        _customCountDownTimer = new CustomCountDownTimer(_defaultSecond * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String string = String.format(getString(R.string.skip), Integer.parseInt(JavaUtils.getNumberFromString(_splashJump.getText().toString())) - 1);
                _splashJump.setText(string);
            }

            @Override
            public void onFinish() {
                if (!_isManualJump) {
                    startMainActivity();
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_customCountDownTimer != null) {
            _customCountDownTimer.cancel();
        }
    }

    //防止用户返回键退出APP
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_HOME) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void next() {
        if (_canJump) {
            startMainActivity();
        } else {
            _canJump = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        _canJump = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (_canJump) {
            next();
        }
        _canJump = true;
    }

    private void loadDefaultBg(final String action_type, final String action_value) {
        Log.d(TAG, "loadDefaultBg");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initJumpButton();

                if (action_type != null && !action_type.isEmpty()) {
                    _splashBg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (action_type.equals(PageJumpHelper.LOAD_APK)) {
                                _customCountDownTimer.cancel();
                                ApkDownloadUtils.showVersionDialog(_context, new ApkDownloadUtils.DialogClickCallBack() {
                                    @Override
                                    public void onClicked(boolean isCancel) {
                                        if (isCancel) {
                                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                            intent.putExtra("action_value", action_value);
                                            startActivity(intent);
                                            finish();
                                        }
                                    }
                                });
                            } else {
                                PageJumpHelper.jump(SplashActivity.this, action_type, action_value);
                                if (!action_type.equals("none")) {
                                    _customCountDownTimer.cancel();
                                    finish();
                                }
                            }
                        }
                    });
                }

                _splashBg.setVisibility(View.VISIBLE);
                _splashJump.setVisibility(View.VISIBLE);
                _customCountDownTimer.start();
            }
        });
    }

    private void startMainActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /** 微信的包名*/
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    /** 更新微信包信息*/
    private void updatePackageInfo() {
        try {
            mWechatPackageInfo = getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /** 获取微信的版本*/
    private int getWechatVersion() {
        if(mWechatPackageInfo == null) {
            return 0;
        }
        Log.d(TAG, "getWechatVersion ------>" + mWechatPackageInfo.versionCode);
        return mWechatPackageInfo.versionCode;
    }
}
