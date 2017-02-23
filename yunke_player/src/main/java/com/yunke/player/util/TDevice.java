package com.yunke.player.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TDevice {

    // 手机网络类型
    public static final int NETTYPE_NONE = 0x00;
    public static final int NETTYPE_WIFI = 0x01;
    public static final int NETTYPE_CMWAP = 0x02;
    public static final int NETTYPE_CMNET = 0x03;

    public static boolean GTE_HC;
    public static boolean GTE_ICS;
    public static boolean PRE_HC;
    private static Boolean _hasBigScreen = null;
    private static Boolean _hasCamera = null;
    private static Boolean _isTablet = null;
    private static Integer _loadFactor = null;

    private static int _pageSize = -1;
    public static float displayDensity = 0.0F;

    static {
        GTE_ICS = Build.VERSION.SDK_INT >= 14;
        GTE_HC = Build.VERSION.SDK_INT >= 11;
        PRE_HC = Build.VERSION.SDK_INT < 11;
    }

    public TDevice() {
    }

    public static float dpToPixel(Context context, float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getDisplayMetrics(context));
    }

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        ((WindowManager) context.getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
                displaymetrics);
        return displaymetrics;
    }

    public static float getScreenHeight(Context context) {
        return getDisplayMetrics(context).heightPixels;
    }

    public static void hideSoftKeyboard(Context context, View view) {
        if (view == null)
            return;
        ((InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                view.getWindowToken(), 0);
    }

    public static void setFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow()
                .getAttributes();
        params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        activity.getWindow().setAttributes(params);
        activity.getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static void cancelFullScreen(Activity activity) {
        WindowManager.LayoutParams params = activity.getWindow()
                .getAttributes();
        params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
        activity.getWindow().setAttributes(params);
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

    public static boolean isWifiNetwork(Context context) {
        NetworkInfo activeNetwork = getActiveNetwork(context);
        return activeNetwork != null && activeNetwork.getType() == ConnectivityManager.TYPE_WIFI;
    }

    private static NetworkInfo getActiveNetwork(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo();
    }

    public static String getPhoneType() {
        return Build.MODEL;
    }

}
