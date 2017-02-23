package com.yunke.player.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.SeekBar;

import com.yunke.player.util.UIHelper;


/**
 * author: wangyanan on 2016/11/13 16:14
 * email: 13001111269@163.com
 */
public class PlayerSeekBar extends FrameLayout implements SeekBar.OnSeekBarChangeListener {

    private SeekBar mSeekBarThumb;
    private PlayerProgressBar mProgressBar;
    private SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener;

    public PlayerSeekBar(Context context) {
        this(context, null);
    }

    public PlayerSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PlayerSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    private void initView(Context context) {
        View.inflate(context, UIHelper.getResIdByName(context, UIHelper.TYPE_LAYOUT, "yunke_view_player_seekbar"), this);
        mProgressBar = (PlayerProgressBar) findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "player_progressbar"));
        mSeekBarThumb = (SeekBar) findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "player_seekbar_thumb"));
        mSeekBarThumb.setOnSeekBarChangeListener(this);
    }

    public void setMax(int max) {
        mProgressBar.setMax(max);
        mSeekBarThumb.setMax(max);
    }

    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
        mSeekBarThumb.setProgress(progress);
    }

    public void setOnSeekBarChangeListener(SeekBar.OnSeekBarChangeListener l) {
        mOnSeekBarChangeListener = l;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        mProgressBar.setProgress(progress);
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onProgressChanged(seekBar, progress, fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStartTrackingTouch(seekBar);
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (mOnSeekBarChangeListener != null) {
            mOnSeekBarChangeListener.onStopTrackingTouch(seekBar);
        }
    }
}
