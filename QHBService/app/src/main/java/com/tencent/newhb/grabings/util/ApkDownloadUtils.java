package com.tencent.newhb.grabings.util;

import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.tencent.newhb.grabings.Config;
import com.tencent.newhb.grabings.R;

import java.util.List;

public class ApkDownloadUtils {
    public static final String TAG = ApkDownloadUtils.class.getSimpleName();

    private static final String KEY_DOWNLOAD_ID = "downloadId";

    private DialogClickCallBack _dialogClickCallBack;

    public static void download(Context context, String url, String title) {
        long downloadId = SharePreferenceHelper.getSharePreferenceFromLong(context, Config.APP_CONFIG, KEY_DOWNLOAD_ID);
        if (downloadId != -1L) {
            FileDownloadManager fdm = FileDownloadManager.getInstance(context);
            int status = fdm.getDownloadStatus(downloadId);
            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                //启动更新界面
                Uri uri = fdm.getDownloadUri(downloadId);
                if (uri != null) {
                    if (compare(getApkInfo(context, uri.getPath()), context)) {
                        startInstall(context, uri);
                        return;
                    } else {
                        fdm.getDm().remove(downloadId);
                    }
                }
                start(context, url, title);
            } else if (status == DownloadManager.STATUS_FAILED) {
                start(context, url, title);
            } else {
                Log.d(TAG, "apk is already downloading");
            }
        } else {
            start(context, url, title);
        }
    }

    private static void start(Context context, String url, String title) {
        long id = FileDownloadManager.getInstance(context).startDownload(context, url,
                title, "下载完成后点击打开");
        SharePreferenceHelper.saveSharePreferenceFromLong(context, Config.APP_CONFIG, KEY_DOWNLOAD_ID, id);
        Log.d(TAG, "apk start download " + id);
    }

    public static void startInstall(Context context, Uri uri) {
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(install);
    }


    /**
     * 获取apk程序信息[packageName,versionName...]
     *
     * @param context Context
     * @param path    apk path
     */
    private static PackageInfo getApkInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            //String packageName = info.packageName;
            //String version = info.versionName;
            //Log.d(TAG, "packageName:" + packageName + ";version:" + version);
            //String appName = pm.getApplicationLabel(appInfo).toString();
            //Drawable icon = pm.getApplicationIcon(appInfo);//得到图标信息
            return info;
        }
        return null;
    }


    /**
     * 下载的apk和当前程序版本比较
     *
     * @param apkInfo apk file's packageInfo
     * @param context Context
     * @return 如果当前应用版本小于apk的版本则返回true
     */
    private static boolean compare(PackageInfo apkInfo, Context context) {
        if (apkInfo == null) {
            return false;
        }
        String localPackage = context.getPackageName();
        if (apkInfo.packageName.equals(localPackage)) {
            try {
                PackageInfo packageInfo = context.getPackageManager().getPackageInfo(localPackage, 0);
                if (apkInfo.versionCode > packageInfo.versionCode) {
                    return true;
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private static void showDownloadSetting(Context context) {
        String packageName = "com.android.providers.downloads";
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        if (intentAvailable(context, intent)) {
            context.startActivity(intent);
        }
    }

    private static boolean intentAvailable(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        List list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }


    private static boolean canDownloadState(Context context) {
        try {
            int state = context.getPackageManager().getApplicationEnabledSetting("com.android.providers.downloads");

            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void download(Context context, String url) {
        if (!canDownloadState(context)) {
            Toast.makeText(context, "下载服务不可用,请您启用", Toast.LENGTH_SHORT).show();
            showDownloadSetting(context);
            return;
        }
//        String url;
//        if (TextUtils.isEmpty(editText.getText().toString())) {
//            url = "http://releases.b0.upaiyun.com/hoolay.apk";
//        } else {
//            url = editText.getText().toString();
//        }
        download(context, url, context.getResources().getString(R.string.app_name));
    }

    public static void showVersionDialog(final Context context, final DialogClickCallBack dialogClickCallBack) {
        View view_input = LayoutInflater.from(context).inflate(R.layout.dialog_upload_apk, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(context.getString(R.string.upload_apk_title))
                .setView(view_input)
                .setNegativeButton(context.getString(R.string.upload_cancel_title),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialogClickCallBack.onClicked(false);
                    }
                });
        builder.setPositiveButton(context.getString(R.string.upload_create_title),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //下载app
                        dialogClickCallBack.onClicked(true);
                    }
                });
        builder.show();
    }

    public interface DialogClickCallBack {
        void onClicked(boolean isCancel);
    }
}
