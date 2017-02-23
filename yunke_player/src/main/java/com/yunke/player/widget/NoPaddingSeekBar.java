package com.yunke.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.SeekBar;

/**
 * author: wangyanan on 2016/11/22 18:36
 * email: 13001111269@163.com
 */
public class NoPaddingSeekBar extends SeekBar {

    public NoPaddingSeekBar(Context context) {
        super(context, null);
    }

    public NoPaddingSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoPaddingSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
