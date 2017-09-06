package com.tencent.newhb.grabings.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by MSI05 on 2016/12/26.
 */
public class SharePreferenceHelper {

    public static void saveSharePreferenceFromString(Context context, String name, String key, String value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public static void saveSharePreferenceFromBoolean(Context context, String name, String key, boolean value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    public static void saveSharePreferenceFromInteger(Context context, String name, String key, int value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public static void saveSharePreferenceFromLong(Context context, String name, String key, long value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public static boolean getSharePreferenceFromBoolean(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static boolean getSharePreferenceFromBooleanWithTrue(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, true);
    }

    public static boolean getSharePreferenceFromBooleanWithFalse(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }

    public static String getSharePreferenceFromString(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");

    }

    public static long getSharePreferenceFromLong(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, -1L);
    }

    public static int getSharePreferenceFromInteger(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        try {
            return sharedPreferences.getInt(key, 0);
        } catch (Exception e) {
            // TODO: handle exception
            return 0;
        }

    }

    public static int getSharePreferenceFromNegativeInteger(Context context, String name, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
        try {
            return sharedPreferences.getInt(key, -1);
        } catch (Exception e) {
            // TODO: handle exception
            return -1;
        }

    }
}
