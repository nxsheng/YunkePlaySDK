package com.yunke.player.widget;

/**
 * 作者：滑尧伟
 * 邮箱：hyw88866@163.com
 * 公司:北京拓维信息（高能壹佰）股份制有限公司
 * 公司网址: https://www.yunke.com
 * 创建于 2016/1/19 16:33
 */
import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;
public class NoScrollGridView extends GridView {
    public NoScrollGridView(Context context) {
        super(context);

    }
    public NoScrollGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
