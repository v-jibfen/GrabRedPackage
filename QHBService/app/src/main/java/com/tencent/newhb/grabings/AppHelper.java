package com.tencent.newhb.grabings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.tencent.newhb.grabings.util.Md5Utils;

import java.util.List;

/**
 * Created by MSI05 on 2016/12/27.
 */
public class AppHelper {

    private static final String TAG = AppHelper.class.getSimpleName();

    public static String getAppDeviceId(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String DEVICE_ID = "";

        if (DEVICE_ID != null) {
            DEVICE_ID = telephonyManager.getDeviceId();
        }

        if (DEVICE_ID == null) {
            DEVICE_ID = "";
        }

        Log.d(TAG, "getAppDeviceId ---->" + DEVICE_ID);
        return DEVICE_ID;
    }

    public static String getWifiMacAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String wifiMacAddress = wifiManager.getConnectionInfo().getMacAddress();

        if (wifiMacAddress == null) {
            wifiMacAddress = "";
        }
        Log.d(TAG, "getWifiMacAddress ---->" + wifiMacAddress);
        return wifiMacAddress;
    }

    public static String getAndroidId(Context context) {

        String android_id = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

        if (android_id == null) {
            android_id = "";
        }

        Log.d(TAG, "getAndroidId ---->" + android_id);
        return android_id;
    }

    public static String getUUID(Context context) {
        String m_szDevIDShort = "35" + //we make this look like a valid IMEI
                Build.BOARD.length()%10 +
                Build.BRAND.length()%10 +
                Build.CPU_ABI.length()%10 +
                Build.DEVICE.length()%10 +
                Build.DISPLAY.length()%10 +
                Build.HOST.length()%10 +
                Build.ID.length()%10 +
                Build.MANUFACTURER.length()%10 +
                Build.MODEL.length()%10 +
                Build.PRODUCT.length()%10 +
                Build.TAGS.length()%10 +
                Build.TYPE.length()%10 +
                Build.USER.length()%10 ; //13 digits

        Log.d(TAG, "getUUID" + m_szDevIDShort);
        return m_szDevIDShort;
    }

    //获取唯一标识
    public static String getUniqueId(Context context) {

        String unique_id = getAppDeviceId(context) +
                getWifiMacAddress(context) +
                getAndroidId(context);

        Log.d(TAG, "getUniqueId ---->" + unique_id);

        Log.d(TAG, "getUniqueId md5 ---->" + Md5Utils.getMD5Code(unique_id));

        return Md5Utils.getMD5Code(unique_id);
    }

    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info = context.getApplicationInfo();
            return (info.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getVersionCode(Context context) {
        PackageManager pm = context.getPackageManager();
        String versioncode = null;
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(context.getPackageName(), 0);
            versioncode = String.valueOf(pi.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versioncode;
    }

    public static String getChannel(Context context) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        Log.d(TAG, "getChannel ---->" + appInfo.metaData.getString("UMENG_CHANNEL"));
        return appInfo.metaData.getString("UMENG_CHANNEL");
    }

    public static void restartApplication(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("flag", "restart");
        context.startActivity(intent);
    }

    public static String getUmsKey(Context context) {
        ApplicationInfo appInfo = null;
        try {
            appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
        Log.d(TAG, "getUmsKey ---->" + appInfo.metaData.getString("UMS_APPKEY"));
        return appInfo.metaData.getString("UMS_APPKEY");
    }

    public static boolean isWeChatAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isAlipayAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.eg.android.AlipayGphone")) {
                    return true;
                }
            }
        }

        return false;
    }
}
