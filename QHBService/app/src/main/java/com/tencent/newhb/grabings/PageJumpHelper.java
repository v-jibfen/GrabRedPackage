package com.tencent.newhb.grabings;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.tencent.newhb.grabings.ui.MainActivity;
import com.tencent.newhb.grabings.webView.WebViewActivity;

public class PageJumpHelper {

    private static final String TAG = PageJumpHelper.class.getSimpleName();

    public static final String INTERNET = "internet";
    public static final String LOAD_APK = "apk";
    public static final String WEBVIEW = "webview";
    public static final String NONE = "none";

    public static void jump(Context context, String action_type, String action_value) {
        Log.v(TAG, "action_type" + " =  " + action_type);
        Log.v(TAG, "action_value" + " =  " + action_value);
        Intent intent = new Intent();
        if (!(context instanceof Activity))
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        switch (action_type) {
            case WEBVIEW:
                jumpToWebViewInternal(context, action_value, intent);
                break;
            case LOAD_APK:
//                jumpToApk(context, action_value, intent);
                break;

            case INTERNET:
                jumpToInternet(context, action_value, intent);
                break;
            case NONE:

                break;
        }
    }

    private static void jumpToWebViewInternal(Context context, String action_value, Intent intent) {
        additionMainActivity(context);

        intent.setClass(context, WebViewActivity.class);
        intent.putExtra("web_url", action_value);
        context.startActivity(intent);
    }

    private static void jumpToInternet(Context context, String file_path, Intent intent) {
        additionMainActivity(context);
        intent.setAction("android.intent.action.VIEW");
        Uri content_url = Uri.parse(file_path);
        intent.setData(content_url);
        context.startActivity(intent);
    }

    /**
     * run mainActivity while it isn't exist
     *
     * @param context
     */
    private static void additionMainActivity(Context context) {
        Intent main = new Intent(context, MainActivity.class);
        if (!(context instanceof Activity))
            main.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(main);
    }
}
