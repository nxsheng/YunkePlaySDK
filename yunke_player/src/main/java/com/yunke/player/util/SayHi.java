/**
 * Copyright (C) 2013 ALiang (illuspas@gmail.com)
 * <p/>
 * Licensed under the GPLv2 license. See 'COPYING' for more details.
 */
package com.yunke.player.util;

import android.util.Log;

public class SayHi {
    private static final String TAG = SayHi.class.getSimpleName();

    private static SayHi instance;

    public native void Init();

    public native void OpenPublisher(String rtmpUrl);

    public native void ClosePublisher();

    public native void OpenPlayer(String rtmpUrl);

    public native void PausePlayer();

    public native void ResumePlayer();

    public native void ClosePlayer();

    public native void Deinit();

    public OnEventCallback mOnEventCallback;

    /**
     * 单一实例
     */
    public static SayHi getInstance() {
        if (instance == null) {
            instance = new SayHi();
        }
        return instance;
    }

    public static void initInstance() {
        getInstance().Init();
    }

    public static void destoryInstance() {
        if (instance != null) {
            instance.Deinit();
            instance = null;
        }
    }

    private void onEventCallback(int event) {
        Log.d(TAG, ">>>>>>>>>>>>>>>>>> onEventCallback: " + event);
        if (mOnEventCallback != null) {
            mOnEventCallback.onEvent(event);
        }
    }

    static {
        Log.d(TAG, ">>>>>>>>>>>>>>>>>> loadLibrary");
        System.loadLibrary("sayhi");
    }

    public void setOnEventCallback(OnEventCallback onEventCallback) {
        mOnEventCallback = onEventCallback;
    }

    public interface OnEventCallback {
        public void onEvent(int event);
    }
}
