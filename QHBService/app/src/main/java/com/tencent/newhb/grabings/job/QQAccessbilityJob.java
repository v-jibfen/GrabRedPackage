package com.tencent.newhb.grabings.job;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.IStatusBarNotification;
import com.tencent.newhb.grabings.QiangHongBaoService;
import com.tencent.newhb.grabings.database.DBHelper;
import com.tencent.newhb.grabings.entity.PackageLog;
import com.tencent.newhb.grabings.ui.MainActivity;
import com.tencent.newhb.grabings.util.AccessibilityHelper;
import com.tencent.newhb.grabings.util.NotifyHelper;
import com.tencent.newhb.grabings.util.ScreenUtils;
import com.tencent.newhb.grabings.util.SharePreferenceHelper;

import java.util.ArrayList;
import java.util.List;

public class QQAccessbilityJob extends BaseAccessbilityJob {

    private static final String TAG = "QQAccessbilityJob";

    /** 红包消息的关键字*/
    private static final String QQ_TYPE = "QQ";
    private static final String HONGBAO_TEXT_KEY = "[QQ红包]";
    private static final String WECHAT_OPEN_EN = "Open";
    private static final String WECHAT_OPENED_EN = "You've opened";
    private final static String QQ_DEFAULT_CLICK_OPEN = "点击拆开";
    private final static String QQ_HONG_BAO_PASSWORD = "口令红包";
    private final static String QQ_CLICK_TO_PASTE_PASSWORD = "点击输入口令";
    private final static String QQ_RED_PACKAGE_SAVE = "已存入余额";
    private final static String QQ_PASSWORD_HONG_BAO_CHECKED = "口令红包已拆开";
    private final static String QQ_RED_PACKET_UN_SAVE = "来晚一步";

    private AccessibilityNodeInfo rootNodeInfo;
    private boolean mLuckyMoneyReceived;
    private String lastFetchedHongbaoId = null;
    private long lastFetchedTime = 0;
    private static final int MAX_CACHE_TOLERANCE = 5000;
    private PackageInfo _packageInfo;
    private List<AccessibilityNodeInfo> mReceiveNode;
    private DBHelper _DBHelper;
    private Handler _handler;

    private boolean isReceivingHongbao;
    private boolean _isGrabRedPackage = true;
    private boolean _isAutoGrab = false;

    /** qq的包名*/
    public static final String QQ_PACKAGENAME = "com.tencent.mobileqq";

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新安装包信息
            updatePackageInfo();
        }
    };

    @Override
    public String getTargetPackageName() {
        return QQ_PACKAGENAME;
    }

    @Override
    public void onReceiveJob(AccessibilityEvent event) {
        final int eventType = event.getEventType();
        //通知栏事件
        if(eventType == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
            Parcelable data = event.getParcelableData();
            if(data == null || !(data instanceof Notification)) {
                return;
            }
            if(QiangHongBaoService.isNotificationServiceRunning() && getConfig().isEnableNotificationService()) { //开启快速模式，不处理
                return;
            }
            List<CharSequence> texts = event.getText();
            if(!texts.isEmpty()) {
                String text = String.valueOf(texts.get(0));
                notificationEvent(text, (Notification) data);
            }
        } else if(eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
            openRed(event);
        } else if(eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            if(checkHongbaoNodeInfo(event)) {
                openRed(event);
            }
            if (_isAutoGrab) {
                AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
                if (nodeInfo != null) {
                    AccessibilityNodeInfo nodeInfo0 = AccessibilityHelper.findNodeInfosByTexts(nodeInfo, new String[]{QQ_RED_PACKET_UN_SAVE});

                    if (nodeInfo0 != null) {
                        //没抢到红包
                        Log.d(TAG, "没抢到红包");
                        _isGrabRedPackage = false;
                    }
                }
            }
        }
    }

    @Override
    public void onCreateJob(QiangHongBaoService service) {
        super.onCreateJob(service);

        updatePackageInfo();

        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");
        filter.addAction("android.intent.action.PACKAGE_ADDED");
        filter.addAction("android.intent.action.PACKAGE_REPLACED");
        filter.addAction("android.intent.action.PACKAGE_REMOVED");

        getContext().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onStopJob() {
        try {
            getContext().unregisterReceiver(broadcastReceiver);
        } catch (Exception e) {}
    }

    @Override
    public void onNotificationPosted(IStatusBarNotification service) {
        Notification nf = service.getNotification();
        String text = String.valueOf(service.getNotification().tickerText);
        notificationEvent(text, nf);
    }

    /** 通知栏事件*/
    private void notificationEvent(String ticker, Notification nf) {
        String text = ticker;
        int index = text.indexOf(":");
        if(index != -1) {
            text = text.substring(index + 1);
        }
        text = text.trim();
        if(text.contains(getQQSelectTextKey())) { //红包消息
            newHongBaoNotification(nf);
        }
    }

    /** 打开通知栏消息*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void newHongBaoNotification(Notification notification) {
//        isReceivingHongbao = true;
        //以下是精华，将微信的通知栏消息打开
        PendingIntent pendingIntent = notification.contentIntent;
        boolean lock = NotifyHelper.isLockScreen(getContext());

        if(!lock) {
            NotifyHelper.send(pendingIntent);
        } else {
            //是否开启息屏抢红包
            if (getConfig().isEnableScreenGrab()) {
                ScreenUtils.openScreen(getContext());
                NotifyHelper.send(pendingIntent);
            }
            NotifyHelper.showNotify(getContext(), String.valueOf(notification.tickerText), pendingIntent);
        }

        if(lock || getConfig().getWechatMode() != Config.WX_MODE_0) {
            NotifyHelper.playEffect(getContext(), getConfig());
        }
    }

    @Override
    public boolean isEnable() {
        return getConfig().isEnableQQ();
    }

    private void openRed(AccessibilityEvent event) {
        Log.d(TAG, "openRed");
        this.rootNodeInfo = event.getSource();
        if (rootNodeInfo == null) {
            return;
        }
        mReceiveNode = null;
        checkNodeInfo();

        if (!_isGrabRedPackage) {
            if (!getConfig().isEnableSVIP()) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.putExtra(Config.KEY_IS_UN_GRAB_RED_PACKAGE, true);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
            _isAutoGrab = false;
            _isGrabRedPackage = true; //恢复默认
        }

        //如果已经接收到红包并且还没有戳开
        if (mLuckyMoneyReceived && (mReceiveNode != null)) {
            int size = mReceiveNode.size();
            if (size > 0) {
                String id = getHongbaoText(mReceiveNode.get(size - 1));
                long now = System.currentTimeMillis();
                if (this.shouldReturn(id, now - lastFetchedTime)) {

                    logQQPackage();
                    return;
                }

                lastFetchedHongbaoId = id;
                lastFetchedTime = now;
                final AccessibilityNodeInfo cellNode = mReceiveNode.get(size - 1);
                if (cellNode != null && cellNode.getText() != null &&
                        cellNode.getText().toString().equals(QQ_PASSWORD_HONG_BAO_CHECKED)) {
                    return;
                }

                //判断是否开启房封号模式
                if (getConfig().isEnableAvoidKick()) {
                    return;
                }
                //判断是否开启自动抢红包
                if (!getConfig().isEnableAutoGrab()) {
                    return;
                }

                long sDelayTime = getConfig().getOpenDelayTime();
                if(sDelayTime != 0) {
                    getHandler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            AccessibilityHelper.performClick(cellNode.getParent());
                            _isAutoGrab = true;
                        }
                    }, sDelayTime);
                } else {
                    AccessibilityHelper.performClick(cellNode.getParent());
                    _isAutoGrab = true;
                }

                if (getConfig().isEnableQQPasswordPackage()
                        && cellNode != null && cellNode.getText() != null &&
                            cellNode.getText().toString().equals(getQQTokenSelectTextKey())) {
                    if(sDelayTime != 0) {
                        getHandler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AccessibilityNodeInfo rowNode = getService().getRootInActiveWindow();
                                if (rowNode == null) {
                                    return;
                                } else {
                                    recycle(rowNode);
                                }
                            }
                        }, sDelayTime);
                    } else {
                        AccessibilityNodeInfo rowNode = getService().getRootInActiveWindow();
                        if (rowNode == null) {
                            return;
                        } else {
                            recycle(rowNode);
                        }
                    }
                }
                mLuckyMoneyReceived = false;
            }
        } else {
            logQQPackage();
        }
    }

    //记录红包信息
    private void logQQPackage() {
        if (getQQVersion() > 450 ) { //6.6.5版本以上红包有动画
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        if (nodeInfo == null) {
            return;
        }
        //红包已经打开
        AccessibilityNodeInfo nodeInfo1 = AccessibilityHelper.findNodeInfosByTexts(nodeInfo, new String[]{QQ_RED_PACKAGE_SAVE});
        if (nodeInfo1 != null) {
            PackageLog packageLog = PackageLog.getQQInfo(nodeInfo1, QQ_TYPE, getQQVersion());

            //判断是否要返回
            int afterGetHongbao = getConfig().getWechatAfterGetHongBaoEvent();
            if(afterGetHongbao == Config.WX_AFTER_GET_GOHOME) { //返回主界面，以便收到下一次的红包通知
                AccessibilityHelper.performHome(getService());
            } else if (getConfig().isEnableAutoBack() && afterGetHongbao == Config.WX_AFTER_GET_GOBACK) {
                AccessibilityHelper.performBack(getService());
            }

            if (packageLog == null) {
                return;
            }
            Log.d(TAG, "红包日志:" + packageLog.getSource());
            Log.d(TAG, "红包日志:" + packageLog.getMoney());
            Log.d(TAG, "红包日志:" + packageLog.getNickname());
            Log.d(TAG, "红包日志:" + packageLog.getTime());

            if (_DBHelper == null) {
                _DBHelper = new DBHelper(getContext());
            }
            _DBHelper.insertLog(packageLog);


        }
    }

    private void checkNodeInfo() {
        if (rootNodeInfo == null) {
            return;
        }
        /* 聊天会话窗口，遍历节点匹配“点击拆开”，“口令红包”，“点击输入口令” */
        List<AccessibilityNodeInfo> nodes1 = findAccessibilityNodeInfosByTexts(this.rootNodeInfo, new String[]{QQ_DEFAULT_CLICK_OPEN, QQ_HONG_BAO_PASSWORD, QQ_CLICK_TO_PASTE_PASSWORD, "发送"});
        if (!nodes1.isEmpty()) {
            String nodeId = Integer.toHexString(System.identityHashCode(this.rootNodeInfo));
            if (!nodeId.equals(lastFetchedHongbaoId)) {
                mLuckyMoneyReceived = true;
                mReceiveNode = nodes1;
            }    return;
        }
    }

    private boolean checkHongbaoNodeInfo(AccessibilityEvent event) {
        this.rootNodeInfo = event.getSource();
        if (rootNodeInfo == null) {
            return false;
        }

        boolean isHasHongBao = false;
        /* 聊天会话窗口，遍历节点匹配“点击拆开”，“口令红包”，“点击输入口令” */
        List<AccessibilityNodeInfo> nodes1 = findAccessibilityNodeInfosByTexts(this.rootNodeInfo, new String[]{QQ_DEFAULT_CLICK_OPEN, QQ_HONG_BAO_PASSWORD, QQ_CLICK_TO_PASTE_PASSWORD, "发送"});
        if (!nodes1.isEmpty()) {
            String nodeId = Integer.toHexString(System.identityHashCode(this.rootNodeInfo));
            if (!nodeId.equals(lastFetchedHongbaoId)) {
                isHasHongBao = true;
            } else {
                isHasHongBao = false;
            }
        }

        return isHasHongBao;
    }

    private String getHongbaoText(AccessibilityNodeInfo node) {
        //获取红包上的文本
        String content;
        try {
            AccessibilityNodeInfo i = node.getParent().getChild(0);
            if (i == null) {
                return null;
            }
            if (i.getText() == null) {
                return null;
            }
            content = i.getText().toString();
        } catch (NullPointerException npe) {
            return null;
        }
        return content;
    }

    private boolean shouldReturn(String id, long duration) {
        // ID为空
        if (id == null) return true;
        // 名称和缓存不一致
        if (duration < MAX_CACHE_TOLERANCE && id.equals(lastFetchedHongbaoId)) {
            return true;
        }
        return false;
    }

    private List<AccessibilityNodeInfo> findAccessibilityNodeInfosByTexts(AccessibilityNodeInfo nodeInfo, String[] texts) {
        for (String text : texts) {
            if (text == null) continue;
            List<AccessibilityNodeInfo> nodes = nodeInfo.findAccessibilityNodeInfosByText(text);
            if (!nodes.isEmpty()) {
                if (text.equals(WECHAT_OPEN_EN) && !nodeInfo.findAccessibilityNodeInfosByText(WECHAT_OPENED_EN).isEmpty()) {
                    continue;
                }
                return nodes;
            }
        }
        return new ArrayList<>();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public void recycle(AccessibilityNodeInfo info) {
        if (info.getChildCount() == 0) {
            //这个if代码的作用是：匹配“点击输入口令的节点，并点击这个节点”
            if(info != null && info.getText() != null && info.getText().toString().equals(QQ_CLICK_TO_PASTE_PASSWORD)) {
                info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
            //这个if代码的作用是：匹配文本编辑框后面的发送按钮，并点击发送口令
            if (info != null && info.getText() != null && info.getClassName() != null
                    && info.getClassName().toString().equals("android.widget.Button") && info.getText().toString().equals("发送")) {
                info.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            for (int i = 0; i < info.getChildCount(); i++) {
                if (info.getChild(i) != null) {
                    recycle(info.getChild(i));
                }
            }
        }
    }

    /** 获取微信的版本*/
    private int getQQVersion() {
        if(_packageInfo == null) {
            return 0;
        }
        return _packageInfo.versionCode;
    }

    /** 更新微信包信息*/
    private void updatePackageInfo() {
        try {
            _packageInfo = getContext().getPackageManager().getPackageInfo(QQ_PACKAGENAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Handler getHandler() {
        if(_handler == null) {
            _handler = new Handler(Looper.getMainLooper());
        }
        return _handler;
    }

    private String getQQSelectTextKey() {
        String key;
        key = SharePreferenceHelper.getSharePreferenceFromString(getContext(), Config.APP_CONFIG, Config.QQ_SELECT_KEY);
        if (key == null) {
            return HONGBAO_TEXT_KEY;
        }

        if (key.isEmpty()) {
            return HONGBAO_TEXT_KEY;
        }
        Log.d(TAG, "getQQSelectTextKey :" + key);
        return key;
    }

    private String getQQTokenSelectTextKey() {
        String key;
        key = SharePreferenceHelper.getSharePreferenceFromString(getContext(), Config.APP_CONFIG, Config.QQ_TOKEN_SELECT_KEY);
        if (key == null) {
            return QQ_HONG_BAO_PASSWORD;
        }

        if (key.isEmpty()) {
            return QQ_HONG_BAO_PASSWORD;
        }

        Log.d(TAG, "getQQTokenSelectTextKey :" + key);
        return key;
    }
}
