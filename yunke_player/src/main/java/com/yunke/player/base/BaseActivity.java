package com.yunke.player.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.yunke.player.interf.BaseViewInterface;
import com.yunke.player.util.TDevice;

import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements BaseViewInterface {

    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }

        // 通过注解绑定控件
        ButterKnife.bind(this);

        initView();
        initData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TDevice.hideSoftKeyboard(this, getCurrentFocus());
    }

    protected void onResume() {
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
    }

    @Override
    public void initData() {

    }

    @Override
    public void initView() {
    }

    /**
     * 初始化布局
     */
    protected int getLayoutId() {
        return 0;
    }
}
