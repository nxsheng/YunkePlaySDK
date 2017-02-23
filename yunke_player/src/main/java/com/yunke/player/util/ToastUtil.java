package com.yunke.player.util;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;



public class ToastUtil {

    private static Toast lastToast;
    private static String lastToastMessage = "";
    private static long lastToastTime;

    public static void showToast(Context context, String message) {
        showToast(context, message, Toast.LENGTH_SHORT, 0, Gravity.BOTTOM);
    }

    public static void showToast(Context context, String message, int duration, int icon,
                                 int gravity) {
        if (message != null && !message.equalsIgnoreCase("")) {
            long time = System.currentTimeMillis();
            if (!message.equalsIgnoreCase(lastToastMessage)
                    || Math.abs(time - lastToastTime) > 2000) {
                View view = LayoutInflater.from(context).inflate(
                        UIHelper.getResIdByName(context, UIHelper.TYPE_LAYOUT, "yunke_view_toast"), null);
                ((TextView) view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "tv_title"))).setText(message);
                if (icon != 0) {
                    ((ImageView) view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "iv_icon")))
                            .setImageResource(icon);
                    ((ImageView) view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "iv_icon")))
                            .setVisibility(View.VISIBLE);
                } else {
                    ((ImageView) view.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "iv_icon")))
                            .setVisibility(View.GONE);
                }
                if (lastToast != null) lastToast.cancel();
                Toast toast = new Toast(context);
                toast.setView(view);
                if (gravity == Gravity.CENTER) {
                    toast.setGravity(gravity, 0, 0);
                } else {
                    toast.setGravity(gravity, 0, (int) TDevice.dpToPixel(context, 60));
                }

                toast.setDuration(duration);
                toast.show();
                lastToast = toast;
                lastToastMessage = message;
                lastToastTime = System.currentTimeMillis();
            }
        }
    }
}
