package com.yunke.player.util;

import android.util.Log;

public class TLog {
    public static final String LOG_TAG = TLog.class.getCanonicalName();
    public static boolean DEBUG = true;
    public static boolean OPEN = true;
    public static String LOG_PRINT_STYLE = ">>>>>>>>> ";

    public TLog() {
    }

    public static final void analytics(String log) {
        if (DEBUG)
            Log.d(LOG_TAG, LOG_PRINT_STYLE + log);
    }

    public static final void analytics(String tag, String log) {
        if (DEBUG)
            Log.d(tag, LOG_PRINT_STYLE + log);
    }

    public static final void error(String log) {
        if (DEBUG)
            Log.e(LOG_TAG, LOG_PRINT_STYLE + log);
    }

    public static final void error(String tag, String log) {
        if (DEBUG)
            Log.e(tag, LOG_PRINT_STYLE + log);
    }

    public static final void log(String log) {
//        if (DEBUG || OPEN)
        if (DEBUG)
            Log.i(LOG_TAG, LOG_PRINT_STYLE + log);
    }

    public static final void log(String tag, String log) {
//        if (DEBUG || OPEN)
        if (DEBUG)
            Log.i(tag, LOG_PRINT_STYLE + log);
    }

    public static final void logv(String log) {
        if (DEBUG)
            Log.v(LOG_TAG, LOG_PRINT_STYLE + log);
    }

    public static final void warn(String log) {
        if (DEBUG)
            Log.w(LOG_TAG, LOG_PRINT_STYLE + log);
    }
}
