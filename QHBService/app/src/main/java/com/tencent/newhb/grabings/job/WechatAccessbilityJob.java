package com.tencent.newhb.grabings.job;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.support.v4.BuildConfig;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.IStatusBarNotification;
import com.tencent.newhb.grabings.QiangHongBaoService;
import com.tencent.newhb.grabings.database.DBHelper;
import com.tencent.newhb.grabings.entity.HongbaoSignature;
import com.tencent.newhb.grabings.entity.PackageLog;
import com.tencent.newhb.grabings.ui.MainActivity;
import com.tencent.newhb.grabings.util.AccessibilityHelper;
import com.tencent.newhb.grabings.util.NotifyHelper;
import com.tencent.newhb.grabings.util.ScreenUtils;
import com.tencent.newhb.grabings.util.SharePreferenceHelper;

import java.util.ArrayList;
import java.util.List;

public class WechatAccessbilityJob extends BaseAccessbilityJob {

    private static final String TAG = "WechatAccessbilityJob";

    private HongbaoSignature hongbaoSignature = new HongbaoSignature();

    /** 微信的包名*/
    public static final String WECHAT_PACKAGENAME = "com.tencent.mm";

    public static final String WECHAT_TYPE = "微信";

    /** 红包消息的关键字*/
    private static final String HONGBAO_TEXT_KEY = "[微信红包]";

    private static final String RED_PACKET_SAVE = "已存入零钱";

    private static final String RED_PACKET_UN_SAVE = "手慢了，红包派完了";

    private static final String BUTTON_CLASS_NAME = "android.widget.Button";

    private static final String WX_DETAIL_CLASS_NAME_6_4_5 = "com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI";

    private static final String WX_DETAIL_CLASS_NAME_6_4_6 = "com.tencent.mm.plugin.luckymoney.ui.En_fba4b94f";

    /** 不能再使用文字匹配的最小版本号 */
    private static final int USE_ID_MIN_VERSION = 700;// 6.3.8 对应code为680,6.3.9对应code为700

    private static final int WINDOW_NONE = 0;
    private static final int WINDOW_LUCKYMONEY_RECEIVEUI = 1;
    private static final int WINDOW_LUCKYMONEY_DETAIL = 2;
    private static final int WINDOW_LAUNCHER = 3;
    private static final int WINDOW_OTHER = -1;

    private int mCurrentWindow = WINDOW_NONE;

    private boolean isNewHongbao = false;
    private boolean isReceivingHongbao;
    private PackageInfo mWechatPackageInfo = null;
    private Handler mHandler = null;
    private DBHelper _DBHelper;

    private boolean _isGrabRedPackage = true;

    private List<AccessibilityNodeInfo> _accessibilityNodeInfos = new ArrayList<>();

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //更新安装包信息
            updatePackageInfo();
        }
    };

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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public void onNotificationPosted(IStatusBarNotification sbn) {
        Log.d(TAG, "onNotificationPosted");
        Notification nf = sbn.getNotification();
        String text = String.valueOf(sbn.getNotification().tickerText);
        notificationEvent(text, nf);
    }

    @Override
    public boolean isEnable() {
        return getConfig().isEnableWechat();
    }

    @Override
    public String getTargetPackageName() {
        return WECHAT_PACKAGENAME;
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
            openHongBao(event);
        } else if(eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
            Log.d(TAG, "mCurrentWindow :" + mCurrentWindow);

            AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
            if (nodeInfo != null) {

                if (isNewHongbao) {
                    AccessibilityNodeInfo nodeInfo0 = AccessibilityHelper.findNodeInfosByTexts(nodeInfo, new String[]{RED_PACKET_UN_SAVE});

                    if (nodeInfo0 != null) {
                        //没抢到红包
                        Log.d(TAG, "没抢到红包");
                        _isGrabRedPackage = false;
                    }
                }

                if (!isNewHongbao) {
                    AccessibilityNodeInfo nodeInfo1 = AccessibilityHelper.findNodeInfosByTexts(nodeInfo, new String[]{RED_PACKET_SAVE, RED_PACKET_UN_SAVE});
                    if (nodeInfo1 != null) {
                        AccessibilityHelper.performBack(getService());

                        return;
                    }
                }

            }

            if(mCurrentWindow != WINDOW_LAUNCHER) { //不在聊天界面或聊天列表，不处理
                return;
            }

            handleChatListHongBao();

        }
    }

    /** 是否为群聊天*/
    private boolean isMemberChatUi(AccessibilityNodeInfo nodeInfo) {
        if(nodeInfo == null) {
            return false;
        }
        String id = "com.tencent.mm:id/ces";
        int wv = getWechatVersion();
        if(wv <= 680) {
            id = "com.tencent.mm:id/ew";
        } else if(wv <= 700) {
            id = "com.tencent.mm:id/cbo";
        }
        String title = null;
        AccessibilityNodeInfo target = AccessibilityHelper.findNodeInfosById(nodeInfo, id);
        if(target != null) {
            title = String.valueOf(target.getText());
        }
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("返回");

        if(list != null && !list.isEmpty()) {
            AccessibilityNodeInfo parent = null;
            for(AccessibilityNodeInfo node : list) {
                if(!"android.widget.ImageView".equals(node.getClassName())) {
                    continue;
                }
                String desc = String.valueOf(node.getContentDescription());
                if(!"返回".equals(desc)) {
                    continue;
                }
                parent = node.getParent();
                break;
            }
            if(parent != null) {
                parent = parent.getParent();
            }
            if(parent != null) {
                if( parent.getChildCount() >= 2) {
                    AccessibilityNodeInfo node = parent.getChild(1);
                    if("android.widget.TextView".equals(node.getClassName())) {
                        title = String.valueOf(node.getText());
                    }
                }
            }
        }


        if(title != null && title.endsWith(")")) {
            return true;
        }
        return false;
    }

    /** 通知栏事件*/
    private void notificationEvent(String ticker, Notification nf) {
        String text = ticker;
        int index = text.indexOf(":");
        if(index != -1) {
            text = text.substring(index + 1);
        }
        text = text.trim();
        if(text.contains(getWxSelectTextKey())) { //红包消息
            newHongBaoNotification(nf);
        }
    }

    /** 打开通知栏消息*/
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void newHongBaoNotification(Notification notification) {
        //isReceivingHongbao = true;
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void openHongBao(AccessibilityEvent event) {

        int wechatVersion = getWechatVersion();
        Log.d(TAG, "wechatVersion :" + wechatVersion);

        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();

        if(getWxDetailUITextKey().equals(event.getClassName())) {
            Log.d(TAG, "mCurrentWindow :" + "1");

            mCurrentWindow = WINDOW_LUCKYMONEY_RECEIVEUI;

            //判断是否开启房封号模式
            if (getConfig().isEnableAvoidKick()) {
                return;
            }
            //判断是否开启自动抢红包
            if (!getConfig().isEnableAutoGrab()) {
                return;
            }

            //点中了红包，下一步就是去拆红包
            handleLuckyMoneyReceive();
        } else if("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI".equals(event.getClassName())) {
            Log.d(TAG, "mCurrentWindow :" + "2");
            mCurrentWindow = WINDOW_LUCKYMONEY_DETAIL;
            //拆完红包后看详细的纪录界面

            //如果是新红包，则记录红包
            if (isNewHongbao) {

                AccessibilityNodeInfo nodeInfo1 = AccessibilityHelper.findNodeInfosByTexts(nodeInfo, new String[]{RED_PACKET_SAVE});
                if (nodeInfo1 != null) {

                    PackageLog packageLog = PackageLog.getWeChatInfo(nodeInfo1, WECHAT_TYPE);

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

                hongbaoSignature.commentString = getConfig().getReplayCommentPreferences();
                Log.d(TAG, "变包 :" + isNewHongbao);
                isNewHongbao = false;
            }

        } else if("com.tencent.mm.ui.LauncherUI".equals(event.getClassName())) {
            Log.d(TAG, "mCurrentWindow :" + "3");

            mCurrentWindow = WINDOW_LAUNCHER;
            //在聊天界面,去点中红包

            if (getConfig().isEnableAutoReplay()
                    && hongbaoSignature.commentString != null
                    && !hongbaoSignature.commentString.isEmpty()) {
                sendComment();
                hongbaoSignature.commentString = null;
            }

            if (!_isGrabRedPackage) {
                if (!getConfig().isEnableSVIP()) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra(Config.KEY_IS_UN_GRAB_RED_PACKAGE, true);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }

                _isGrabRedPackage = true; //恢复默认
                isNewHongbao = false;

                return;
            }
            handleChatListHongBao();
        } else {
            Log.d(TAG, "mCurrentWindow :" + "4");
            mCurrentWindow = WINDOW_OTHER;
        }

    }

    /**
     * 点击聊天里的红包后，显示的界面
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void handleLuckyMoneyReceive() {
        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }

        AccessibilityNodeInfo targetNode = null;

        int event = getConfig().getWechatAfterOpenHongBaoEvent();
        int wechatVersion = getWechatVersion();
        if(event == Config.WX_AFTER_OPEN_HONGBAO) { //拆红包
            if (wechatVersion < USE_ID_MIN_VERSION) {
                targetNode = AccessibilityHelper.findNodeInfosByText(nodeInfo, "拆红包");
            } else {
                String buttonId = "com.tencent.mm:id/b43";

                if(wechatVersion == 700) {
                    buttonId = "com.tencent.mm:id/b2c";
                }

                if(buttonId != null) {
                    targetNode = AccessibilityHelper.findNodeInfosById(nodeInfo, buttonId);
                }

                if(targetNode == null) {
                    //分别对应固定金额的红包 拼手气红包
                    AccessibilityNodeInfo textNode = AccessibilityHelper.findNodeInfosByTexts(nodeInfo, "发了一个红包", "给你发了一个红包", "发了一个红包，金额随机");

                    if(textNode != null) {
                        for (int i = 0; i < textNode.getChildCount(); i++) {
                            AccessibilityNodeInfo node = textNode.getChild(i);
                            if (BUTTON_CLASS_NAME.equals(node.getClassName())) {
                                targetNode = node;
                                break;
                            }
                        }
                    }
                }

                if(targetNode == null) { //通过组件查找
                    targetNode = AccessibilityHelper.findNodeInfosByClassName(nodeInfo, BUTTON_CLASS_NAME);
                }
            }
        } else if(event == Config.WX_AFTER_OPEN_SEE) { //看一看
            if(getWechatVersion() < USE_ID_MIN_VERSION) { //低版本才有 看大家手气的功能
                targetNode = AccessibilityHelper.findNodeInfosByText(nodeInfo, "看看大家的手气");
            }
        } else if(event == Config.WX_AFTER_OPEN_NONE) {
            return;
        }

        if(targetNode != null) {
            isNewHongbao = true;

            final AccessibilityNodeInfo n = targetNode;
            long sDelayTime = getConfig().getOpenDelayTime();
            if(sDelayTime != 0) {
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AccessibilityHelper.performClick(n);
                    }
                }, sDelayTime);
            } else {
                AccessibilityHelper.performClick(n);
            }
        } else {
            if (isNewHongbao) {
                AccessibilityNodeInfo nodeInfo0 = AccessibilityHelper.findNodeInfosByTexts(nodeInfo, new String[]{RED_PACKET_UN_SAVE});

                if (nodeInfo0 != null) {
                    //没抢到红包
                    Log.d(TAG, "没抢到红包");
                    _isGrabRedPackage = false;
                }
            }
        }
    }

    /**
     * 收到聊天里的红包
     * */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private void handleChatListHongBao() {
        int mode = getConfig().getWechatMode();
        if(mode == Config.WX_MODE_3) { //只通知模式
            return;
        }

        AccessibilityNodeInfo nodeInfo = getService().getRootInActiveWindow();
        if(nodeInfo == null) {
            Log.w(TAG, "rootWindow为空");
            return;
        }

        if(mode != Config.WX_MODE_0) {
            boolean isMember = isMemberChatUi(nodeInfo);
            if(mode == Config.WX_MODE_1 && isMember) {//过滤群聊
                return;
            } else if(mode == Config.WX_MODE_2 && !isMember) { //过滤单聊
                return;
            }
        }

        final List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("领取红包");

        if(list != null && list.isEmpty()) {
            // 从消息列表查找红包
            AccessibilityNodeInfo node = AccessibilityHelper.findNodeInfosByText(nodeInfo, getWxSelectTextKey());
            if(node != null) {
                if(BuildConfig.DEBUG) {
                    Log.i(TAG, "-->微信红包:" + node);
                }
                //isReceivingHongbao = true;
                AccessibilityHelper.performClick(nodeInfo);
            }
        } else if(list != null) {

            if (!isReceivingHongbao) {
                isReceivingHongbao = true;
                AccessibilityNodeInfo node = list.get(list.size() - 1);
                AccessibilityHelper.performClick(node);
                //isReceivingHongbao = false;
                getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isReceivingHongbao = false;
                    }
                }, 3000);
            }
        }
    }

    private Handler getHandler() {
        if(mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
        return mHandler;
    }

    /** 获取微信的版本*/
    private int getWechatVersion() {
        if(mWechatPackageInfo == null) {
            return 0;
        }
        return mWechatPackageInfo.versionCode;
    }

    /** 更新微信包信息*/
    private void updatePackageInfo() {
        try {
            mWechatPackageInfo = getContext().getPackageManager().getPackageInfo(WECHAT_PACKAGENAME, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void sendComment() {
        try {
            if (fillInputBar(hongbaoSignature.commentString)) {
                findAndPerformAction("android.widget.Button", "发送");

            }
        } catch (Exception e) {
//            // Not supported
        }
    }

    /**
     * 填充输入框
     */
    private boolean fillInputBar(String reply) {
        AccessibilityNodeInfo rootNode = getService().getRootInActiveWindow();
        if (rootNode != null) {
            return findInputBar(rootNode, reply);
        }
        return false;
    }



    /**
     * 查找EditText控件
     * @param rootNode 根结点
     * @param reply 回复内容
     * @return 找到返回true, 否则返回false
     */
    private boolean findInputBar(AccessibilityNodeInfo rootNode, String reply) {
        int count = rootNode.getChildCount();
        Log.i(TAG, "root class=" + rootNode.getClassName() + ", " + rootNode.getText() + ", child: " + count);
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo node = rootNode.getChild(i);
            if ("android.widget.EditText".equals(node.getClassName())) {   // 找到输入框并输入文本
                Log.i(TAG, "****found the EditText");
                setText(node, reply);
                return true;
            }

            if (findInputBar(node, reply)) {    // 递归查找
                return true;
            }
        }
        return false;
    }


    /**
     * 设置文本
     */
    private void setText(AccessibilityNodeInfo node, String reply) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, "set text");
            Bundle args = new Bundle();
            args.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE,
                    reply);
            node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, args);
        } else {
            ClipData data = ClipData.newPlainText("reply", reply);
            ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
            clipboardManager.setPrimaryClip(data);
            node.performAction(AccessibilityNodeInfo.ACTION_FOCUS); // 获取焦点
            node.performAction(AccessibilityNodeInfo.ACTION_PASTE); // 执行粘贴
        }
    }

    /**
     * 查找UI控件并点击
     * @param widget 控件完整名称, 如android.widget.Button, android.widget.TextView
     * @param text 控件文本
     */
    private void findAndPerformAction(String widget, String text) {
        // 取得当前激活窗体的根节点
        if (getService().getRootInActiveWindow() == null) {
            return;
        }

        // 通过文本找到当前的节点
        List<AccessibilityNodeInfo> nodes = getService().getRootInActiveWindow().findAccessibilityNodeInfosByText(text);
        if(nodes != null) {
            for (AccessibilityNodeInfo node : nodes) {
                if (node.getClassName().equals(widget) && node.isEnabled()) {
                    node.performAction(AccessibilityNodeInfo.ACTION_CLICK); // 执行点击
                    break;
                }
            }
        }
    }

    private String getWxSelectTextKey() {
        String key;
        key = SharePreferenceHelper.getSharePreferenceFromString(getContext(), Config.APP_CONFIG, Config.WX_SELECT_KEY);
        if (key == null) {
            return HONGBAO_TEXT_KEY;
        }

        if (key.isEmpty()) {
            return HONGBAO_TEXT_KEY;
        }

        Log.d(TAG, "getWxSelectTextKey :" + key);
        return key;
    }

    private String getWxDetailUITextKey() {
        String key;
        int version = getWechatVersion();
        key = SharePreferenceHelper.getSharePreferenceFromString(getContext(), Config.APP_CONFIG, Config.WX_DETAIL_UI_KEY);
        if (key == null) {

            if (version < 1020) {
                return WX_DETAIL_CLASS_NAME_6_4_5;
            } else{
                return WX_DETAIL_CLASS_NAME_6_4_6;
            }
        }

        if (key.isEmpty()) {
            if (version < 1020) {
                return WX_DETAIL_CLASS_NAME_6_4_5;
            } else{
                return WX_DETAIL_CLASS_NAME_6_4_6;
            }
        }

        Log.d(TAG, "getWxDetailUITextKey :" + key);
        return key;
    }

}
