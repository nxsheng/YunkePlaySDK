package com.yunke.player.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ProgressBar;

public class PlayerProgressBar extends ProgressBar {

    public PlayerProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight());
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        TLog.analytics(TAG, "onDraw");
        super.onDraw(canvas);
    }
}