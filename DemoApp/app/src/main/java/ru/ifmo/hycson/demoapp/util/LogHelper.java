package ru.ifmo.hycson.demoapp.util;

import android.util.Log;

import ru.ifmo.hycson.demoapp.BuildConfig;

public class LogHelper {
    private final static String TAG_GLOBAL = "LogGlobal";
    public final static String TAG_MODULE = "LogModule";
    public final static String TAG_IMAGE = "LogImage";
    public final static String TAG_ACTIVITY = "LogActivity";
    public final static String TAG_FRAGMENT = "LogFragment";

    public static void d(String TAG, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, message + " Thread: " + Thread.currentThread().getId());
        }
    }

    public static void d(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG_GLOBAL, message + " Thread: " + Thread.currentThread().getId());
        }
    }

    public static void e(String TAG, String message, Throwable e) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, message, e);
        }
    }
}