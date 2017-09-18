package com.less.downloadmanager.lib.util;

import android.util.Log;

import com.less.downloadmanager.lib.Constants;

public class L {
    private static final String TAG = "DownloadManager";

    public static void d(String msg) {
        d(TAG, msg);
    }

    public static void d(String tag, String msg) {
        if (Constants.CONFIG.DEBUG) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        i(TAG, msg);
    }

    public static void i(String tag, String msg) {
        if (Constants.CONFIG.DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (Constants.CONFIG.DEBUG) {
            Log.e(tag, msg);
        }
    }

    public static void w(String msg) {
        w(TAG, msg);
    }

    public static void w(String tag, String msg) {
        if (Constants.CONFIG.DEBUG) {
            Log.w(tag, msg);
        }
    }
}
