package com.yunke.player.api.remote;

import android.content.Context;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yunke.player.util.UIHelper;


public class GN100Image {

    public static void updateImageView(String uri, ImageView imageView, DisplayImageOptions options) {
        if (uri == null || uri.length() <= 0 || imageView == null)
            return;
        try {
            String imageURL = uri;
            ImageLoader.getInstance().displayImage(imageURL, imageView, options);
        } catch (Exception e) {
        }
    }

    /**
     * 更新头像图片
     *
     * @param uri
     * @param imageView
     */
    public static void updateCycleAvatarImageView(Context context, String uri, ImageView imageView) {
        DisplayImageOptions defCycleAvatarOptions = new DisplayImageOptions.Builder()
                .showImageOnLoading(UIHelper.getResIdByName(context, UIHelper.TYPE_DRAWABLE, "yunke_default_cycle_avatar_icon")) // 设置图片在下载期间显示的图片
                .showImageForEmptyUri(UIHelper.getResIdByName(context, UIHelper.TYPE_DRAWABLE, "yunke_default_cycle_avatar_icon")) // 设置图片Uri为空或是错误的时候显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                .build(); // 构建完成

        updateImageView(uri, imageView, defCycleAvatarOptions);
    }

}

