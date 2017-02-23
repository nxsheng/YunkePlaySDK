package com.yunke.player.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;


import com.yunke.player.util.ImageUtils;
import com.yunke.player.util.UIHelper;

/**
 * author: wangyanan on 2016/1/14 17:41
 * email: 13001111269@163.com
 */
public class RoundImageView extends ImageView {

    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;

    /**
     * 圆角大小的默认值
     */
    private static final int BODER_RADIUS_DEFAULT = 10;

    /**
     * 圆角的大小
     */
    private int mBorderRadius;
    /**
     * 图片的类型，圆形or圆角
     */
    private int type;
    /**
     * 3x3矩阵，主要用于缩小和放大
     */
    private Matrix mMatrix;
    /**
     * 绘图的Paint
     */
    private final Paint mBitmapPaint;
    /**
     * view的宽度
     */
    private int mWidth;
    /**
     * 圆角的半径
     */
    private int mRadius;
    private RectF mRoundRect;
    /**
     * 渲染图像，使用图像为绘制图形着色
     */
    private BitmapShader mBitmapShader;

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mMatrix = new Matrix();
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, UIHelper.getStyleableArray(context, "yunke_RoundImageView"));
        mBorderRadius = typedArray.getDimensionPixelSize(UIHelper.getStyleable(context, "yunke_RoundImageView_yunke_borderRadius"), (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, BODER_RADIUS_DEFAULT, getResources().getDisplayMetrics()));
        type = typedArray.getInt(UIHelper.getStyleable(context, "yunke_RoundImageView_yunke_type"), TYPE_CIRCLE);
        typedArray.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        /**
         * 如果类型是圆形，则强制改变view的宽高，以小值为准
         */
        if (type == TYPE_CIRCLE) {
            mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());
            mRadius = mWidth / 2;
            setMeasuredDimension(mWidth, mWidth);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (getDrawable() == null) {
            return;
        }
        setUpShader();

        if (type == TYPE_ROUND) {
            canvas.drawRoundRect(mRoundRect, mBorderRadius, mBorderRadius, mBitmapPaint);
        } else {
            canvas.drawCircle(mRadius, mRadius, mRadius, mBitmapPaint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        // 圆角图片的范围
        if (type == TYPE_ROUND) {
            mRoundRect = new RectF(0, 0, getWidth(), getHeight());
        }
    }

    /**
     * 初始化BitmapShader
     */
    private void setUpShader() {
        Drawable drawable = getDrawable();
        Bitmap bitmap = ImageUtils.drawableToBitmap(drawable);
        // 将bitmap作为作色器，就是在指定区域内绘制bitmap
        mBitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        float scale = 1.0f;
        if (type == TYPE_CIRCLE) {
            // 拿到bitmap宽或高的最小值
            int bSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
            scale = mWidth * 1.0f / bSize;
        } else if (type == TYPE_ROUND) {
            // 如果图片的宽或高与view的宽或高不匹配，计算出需要的缩放比例；
            // 缩放后的图片的宽高，一定大于我们view的宽高；所以我们这里取大值
            scale = Math.max(getWidth() * 1.0f / bitmap.getWidth(), getHeight() * 1.0f / bitmap.getHeight());
        }
        // shader的变换矩阵，我们这里主要用于放大或者缩小
        mMatrix.setScale(scale, scale);
        // 设置变换矩阵
        mBitmapShader.setLocalMatrix(mMatrix);
        // 设置shader
        mBitmapPaint.setShader(mBitmapShader);
    }

    /**
     * 设置图片的类型
     *
     * @param type
     */
    public void setType(int type) {
        if (this.type != type) {
            this.type = type;
            requestLayout();
        }
    }

    // --------------------- 缓存状态操作 ---------------------
    private static final String STATE_INSTANCE = "state_instance";
    private static final String STATE_TYPE = "state_type";
    private static final String STATE_BORDER_RADIUS = "state_border_radius";

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(STATE_INSTANCE, super.onSaveInstanceState());
        bundle.putInt(STATE_TYPE, type);
        bundle.putInt(STATE_BORDER_RADIUS, mBorderRadius);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState((Parcelable) bundle.get(STATE_INSTANCE));
            type = bundle.getInt(STATE_TYPE);
            mBorderRadius = bundle.getInt(STATE_BORDER_RADIUS);
        } else {
            super.onRestoreInstanceState(state);
        }
    }
}
