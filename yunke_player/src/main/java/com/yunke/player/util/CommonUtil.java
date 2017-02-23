package com.yunke.player.util;

import android.view.View;
import android.view.animation.TranslateAnimation;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CommonUtil {

    private static final java.lang.String TAG = CommonUtil.class.getCanonicalName();

    private static long fastClickTime = 0;

    /**
     * MD5加密
     *
     * @param string
     * @return
     */
    public static String md5(String string) {
        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Huh, MD5 should be supported?", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Huh, UTF-8 should be supported?", e);
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }

        return hex.toString();
    }

    /**
     * 从右边滑入滑出动画
     *
     * @return
     */
    public static void translateAnimationRight(boolean isShow, View view) {
        TranslateAnimation translateAnimation = null;
        int width = view.getWidth();
        if (isShow) {
            translateAnimation = new TranslateAnimation(width, 0, 0, 0);
            translateAnimation.setDuration(200);
            view.startAnimation(translateAnimation);

        } else {
            translateAnimation = new TranslateAnimation(0, width, 0, 0);
            translateAnimation.setDuration(200);
            view.startAnimation(translateAnimation);
        }
    }

    /**
     * 从左边滑入滑出动画
     *
     * @return
     */
    public static void translateAnimationLeft(boolean isShow, View view) {
        TranslateAnimation translateAnimation = null;
        int width = view.getWidth();
        if (isShow) {
            translateAnimation = new TranslateAnimation(-width, 0, 0, 0);
            translateAnimation.setDuration(200);
            view.startAnimation(translateAnimation);
        } else {
            translateAnimation = new TranslateAnimation(0, -width, 0, 0);
            translateAnimation.setDuration(200);
            view.startAnimation(translateAnimation);
        }
    }
}
