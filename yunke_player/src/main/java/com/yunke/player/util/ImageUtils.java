package com.yunke.player.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.io.FileInputStream;

/**
 * 图片操作工具包
 */
public class ImageUtils {
    /**
     * 根据传入的inputStream和rect解出Bitmap
     */
    public static Bitmap getBitmapFromInputstreamAndReect(FileInputStream is, Rect rect) {
        try {
            BitmapRegionDecoder bitmapRegionDecoder = BitmapRegionDecoder.newInstance(is, false);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            Bitmap bitmap = bitmapRegionDecoder.decodeRegion(rect, options);
            return bitmap;
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 将Drawable转化为Bitmap
     *
     * @param drawable
     * @return
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if(drawable instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) drawable;
            return bd.getBitmap();
        }
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
                .getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;

    }
}
