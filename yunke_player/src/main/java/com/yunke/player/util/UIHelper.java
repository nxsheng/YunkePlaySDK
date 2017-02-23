package com.yunke.player.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import com.yunke.player.ui.PlayVideoActivity;

import java.lang.reflect.Field;

/**
 * 界面帮助类
 */
public class UIHelper {
    public static final int TYPE_DRAWABLE = 0;
    public static final int TYPE_LAYOUT = 1;
    public static final int TYPE_ID = 2;
    public static final int TYPE_DIMEN = 3;
    public static final int TYPE_STRING = 4;
    public static final int TYPE_STYLE = 5;

    public static int getResIdByName(Context context, int type, String name) {
        int id = 0;

        Resources res = context.getResources();

        switch (type) {
            case TYPE_DRAWABLE:
                id = res.getIdentifier(name,"drawable",context.getPackageName());
                break;
            case TYPE_LAYOUT:
                id = res.getIdentifier(name,"layout",context.getPackageName());
                break;
            case TYPE_ID:
                id = res.getIdentifier(name,"id",context.getPackageName());
                break;
            case TYPE_DIMEN:
                id = res.getIdentifier(name,"dimen",context.getPackageName());
                break;
            case TYPE_STRING:
                id = res.getIdentifier(name,"string",context.getPackageName());
                break;
            case TYPE_STYLE:
                id = res.getIdentifier(name,"style",context.getPackageName());
                break;
        }

        return id;
    }

    /**
     * 对于 context.getResources().getIdentifier 无法获取的数据 , 或者数组
     * 资源反射值
     * @paramcontext
     * @param name
     * @param type
     * @return
     */
    private static Object getResourceIdUseReflact(Context context,String name, String type) {
        String className = context.getPackageName() +".R";
        try {
            Class<?> cls = Class.forName(className);
            for (Class<?> childClass : cls.getClasses()) {
                String simple = childClass.getSimpleName();
                if (simple.equals(type)) {
                    for (Field field : childClass.getFields()) {
                        String fieldName = field.getName();
                        if (fieldName.equals(name)) {
                            System.out.println(fieldName);
                            return field.get(null);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *context.getResources().getIdentifier 无法获取到 styleable 的数据
     * @paramcontext
     * @param name
     * @return
     */
    public static int getStyleable(Context context, String name) {
        return ((Integer)getResourceIdUseReflact(context, name,"styleable")).intValue();
    }

    /**
     * 获取 styleable 的 ID 号数组
     * @paramcontext
     * @param name
     * @return
     */
    public static int[] getStyleableArray(Context context,String name) {
        return (int[])getResourceIdUseReflact(context, name,"styleable");
    }

    /**
     * 网络播放器
     *
     * @param context
     * @param uid 用戶id
     * @param token 用戶token
     * @param planId 播放id
     * @param sectionName 章节名
     */
    public static void showYunkePlayVideoActivity(Context context,  int uid, @NonNull String token,
                                             @NonNull String planId, String sectionName) {
        if (TextUtils.isEmpty(planId)) return;
        Intent intent = new Intent(context, PlayVideoActivity.class);
        intent.putExtra(PlayVideoActivity.EXTRA_KEY_USER_ID, uid);
        intent.putExtra(PlayVideoActivity.EXTRA_KEY_USER_TOKEN, token);
        intent.putExtra(PlayVideoActivity.EXTRA_KEY_PLAN_ID, planId);
        intent.putExtra(PlayVideoActivity.EXTRA_KEY_SECTION_NAME, sectionName);
        context.startActivity(intent);
    }

}
