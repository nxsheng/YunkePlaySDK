package com.yunke.player.util;

import android.content.Context;
import android.content.Intent;

import com.yunke.player.bean.Constants;

/**
 * Created by zpf on 2015/10/27.
 */
public class SendBroadcastUtils {

    public static void sendBroadcast(Context context, String action, int stuId) {

        if (null != context) {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra(Constants.GOOD_STUID, stuId);
            context.sendBroadcast(intent);
        }

    }

    public static void sendBroadcast(Context context, String action) {

        if (null != context) {
            Intent intent = new Intent();
            intent.setAction(action);
            context.sendBroadcast(intent);
        }

    }

}
