package com.yunke.player.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * 通用的碎片
 * 使用ButterKnife
 */
public abstract class CommonFragment extends BaseFragment {

    public CommonFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (getLayoutId() != 0) {
            view = inflater.inflate(getLayoutId(), container, false);
        }
        ButterKnife.bind(this, view);
        initView(view);
        initData();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
