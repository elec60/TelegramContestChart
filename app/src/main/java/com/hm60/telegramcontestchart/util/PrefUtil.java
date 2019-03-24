package com.hm60.telegramcontestchart.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.support.annotation.StringRes;

import com.ApplicationLoader;

public final class PrefUtil {

    public static boolean getBoolean(@StringRes int id, boolean defaultValue) {
        return getBoolean(getKey(id), defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return getPreferences().getBoolean(key, defaultValue);
    }

    public static String getKey(@StringRes int id, Object... formatArgs) {
        return getResources().getString(id, formatArgs);
    }

    private static Resources getResources() {
        return getContext().getResources();
    }

    private static Context getContext() {
        return ApplicationLoader.getInstance();
    }

    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(getContext());
    }

    public static void setBoolean(@StringRes int id, boolean value) {
        setBoolean(getKey(id), value);
    }

    public static void setBoolean(String key, boolean value) {
        edit().putBoolean(key, value).apply();
    }

    private static SharedPreferences.Editor edit() {
        return getPreferences().edit();
    }
}
