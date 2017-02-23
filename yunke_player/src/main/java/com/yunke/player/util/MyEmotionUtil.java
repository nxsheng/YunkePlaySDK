package com.yunke.player.util;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.yunke.player.adapter.FaceGVAdapter;
import com.yunke.player.adapter.FaceVPAdapter;
import com.yunke.player.widget.MyEditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zpf on 2016/6/27.
 */
public class MyEmotionUtil {

    private ViewPager mViewPager;
    private LinearLayout mDotsLayout;
    private MyEditText input;
    private Activity context;

    private int columns = 7;
    private int rows = 4;
    public List<View> views = new ArrayList<View>();
    public List<String> staticFacesList;
    private Map<String, String> emotion;

    public MyEmotionUtil(Activity context,LinearLayout mDotsLayout, ViewPager mViewPager,MyEditText input) {
        this.context = context;
        this.mDotsLayout = mDotsLayout;
        this.mViewPager = mViewPager;
        this.input = input;

        if (null != this.mDotsLayout) {
            mViewPager.setOnPageChangeListener(new PageChange());
        }
    }

    /**
     * 初始表情
     */
    public void InitViewPager() {
        // 获取页数
        for (int i = 0; i < getPagerCount(); i++) {
            views.add(viewPagerItem(i));
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(16, 16);
            mDotsLayout.addView(dotsItem(i), params);
        }
        FaceVPAdapter mVpAdapter = new FaceVPAdapter(views);
        mViewPager.setAdapter(mVpAdapter);

        mDotsLayout.getChildAt(0).setSelected(true);
    }

    private View viewPagerItem(int position) {

        View layout = View.inflate(context, UIHelper.getResIdByName(context, UIHelper.TYPE_LAYOUT, "yunke_face_gridview"), null);
        GridView gridview = (GridView) layout.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "chart_face_gv"));
        List<String> subList = new ArrayList<String>();
        subList.addAll(staticFacesList
                .subList(position * (columns * rows - 1),
                        (columns * rows - 1) * (position + 1) > staticFacesList
                                .size() ? staticFacesList.size() : (columns
                                * rows - 1)
                                * (position + 1)));

        // 末尾添加删除图标
        subList.add("emotion_del_normal.png");
        FaceGVAdapter mGvAdapter = new FaceGVAdapter(subList, context);
        gridview.setAdapter(mGvAdapter);
        gridview.setNumColumns(columns);

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    String png = ((TextView) ((LinearLayout) view).getChildAt(1)).getText().toString();
                    if (!png.contains("emotion_del_normal")) {
                        insert(getFace(png));
                    } else {
                        delete();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return gridview;
    }

    private SpannableStringBuilder getFace(String png) {
        SpannableStringBuilder sb = new SpannableStringBuilder();
        try {
            String tempText = "#[" + png + "]#";
            sb.append(tempText);
            sb.setSpan(
                    new ImageSpan(context, BitmapFactory
                            .decodeStream(context.getAssets().open(png))), sb.length()
                            - tempText.length(), sb.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return sb;
    }

    /**
     * 向输入框里添加表情
     */
    private void insert(CharSequence text) {
        int iCursorStart = Selection.getSelectionStart((input.getText()));
        int iCursorEnd = Selection.getSelectionEnd((input.getText()));
        if (iCursorStart != iCursorEnd) {
            ((Editable) input.getText()).replace(iCursorStart, iCursorEnd, "");
        }

        int iCursor = Selection.getSelectionEnd((input.getText()));
        ((Editable) input.getText()).insert(iCursor, text);
    }

    /**
     * 删除图标执行事件
     * 注：如果删除的是表情，在删除时实际删除的是tempText即图片占位的字符串，所以必需一次性删除掉tempText，才能将图片删除
     */
    private void delete() {
        if (input.getText().length() != 0) {
            int iCursorEnd = Selection.getSelectionEnd(input.getText());
            int iCursorStart = Selection.getSelectionStart(input.getText());
            if (iCursorEnd > 0) {
                if (iCursorEnd == iCursorStart) {
                    if (isDeletePng(iCursorEnd)) {
                        String st = "#[face/00.png]#";
                        ((Editable) input.getText()).delete(
                                iCursorEnd - st.length(), iCursorEnd);
                    } else {
                        ((Editable) input.getText()).delete(iCursorEnd - 1,
                                iCursorEnd);
                    }
                } else {
                    ((Editable) input.getText()).delete(iCursorStart,
                            iCursorEnd);
                }
            }
        }
    }

    /**
     * 判断即将删除的字符串是否是图片占位字符串tempText 如果是：则讲删除整个tempText
     **/
    private boolean isDeletePng(int cursor) {
        String st = "#[face/00.png]#";
        String content = input.getText().toString().substring(0, cursor);
        if (content.length() >= st.length()) {
            String checkStr = content.substring(content.length() - st.length(),
                    content.length());
            String regex = "(\\#\\[face/)\\d{2}(.png\\]\\#)";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(checkStr);
            return m.matches();
        }
        return false;
    }

    private ImageView dotsItem(int position) {
        View layout = View.inflate(context, UIHelper.getResIdByName(context, UIHelper.TYPE_LAYOUT, "yunke_dot_image"), null);
        ImageView iv = (ImageView) layout.findViewById(UIHelper.getResIdByName(context, UIHelper.TYPE_ID, "face_dot"));
        iv.setId(position);
        return iv;
    }

    /**
     * 根据表情数量以及GridView设置的行数和列数计算Pager数量
     */
    private int getPagerCount() {
        int count = staticFacesList.size();
        return count % (columns * rows - 1) == 0 ? count / (columns * rows - 1)
                : count / (columns * rows - 1) + 1;
    }

    /**
     * 初始化表情列表staticFacesList
     */
    public void initStaticFaces() {
        try {
            staticFacesList = new ArrayList<String>();
            String[] faces = context.getAssets().list("face");
            for (int i = 0; i < faces.length; i++) {
                staticFacesList.add(faces[i]);
            }
            staticFacesList.remove("emotion_del_normal.png");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 表情页改变时，dots效果也要跟着改变
     */
    public class PageChange implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageSelected(int arg0) {

            try {
                for (int i = 0; i < mDotsLayout.getChildCount(); i++) {
                    mDotsLayout.getChildAt(i).setSelected(false);
                }
                mDotsLayout.getChildAt(arg0).setSelected(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 将图片路径转换为图片名
     *
     * @param content 输入框的内容
     * @return
     */
    public String handlerSend(String content) {

        String regex = "(\\#\\[face/)\\d{2}(.png\\]\\#)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            String num = tempText.substring("#[face/".length(), tempText.length() - ".png]#".length());
            content = content.replace("#[face/" + num + ".png]#", emotion.get(num));
        }
        return content;
    }

    /**
     * 根据Value取Key
     *
     * @param map
     * @param value
     * @return
     */
    public String getKeyByValue(Map map, Object value) {
        String keys = "";
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            Object obj = entry.getValue();
            if (obj != null && obj.equals(value)) {
                keys = (String) entry.getKey();
            }
        }
        return keys;
    }

    /**
     * 将服务端发来的表情符号，转换为本地表情的名称 setSpan
     * @param gifTextView
     * @param content
     * @return
     */
    public SpannableStringBuilder handler(final TextView gifTextView, String content) {
        SpannableStringBuilder sb = new SpannableStringBuilder(content);
        String regex = "(\\/:\\d[a-z_0-9])";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(content);
        while (m.find()) {
            String tempText = m.group();
            String num = getKeyByValue(emotion, tempText);
//            try {
//                String gif = "face/gif/f" + num + ".gif";
//                InputStream is = getActivity().getAssets().open(gif);
//
//                sb.setSpan(new AnimatedImageSpan(new AnimatedGifDrawable(is, new AnimatedGifDrawable.UpdateListener() {
//                            @Override
//                            public void update() {
//                                gifTextView.postInvalidate();
//                            }
//                        })), m.start(), m.end(),
//                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//                is.close();
//            } catch (Exception e) {

            try {
                sb.setSpan(new ImageSpan(context, BitmapFactory.decodeStream(context.getAssets().open("face/" + num + ".png"))), m.start(), m.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            } catch (IOException e1) {

                e1.printStackTrace();
            }
//                e.printStackTrace();
//            }
        }
        return sb;
    }

    /**
     *删除不完整的表情
     * @param conect
     * @return
     */
    @NonNull
    public String deleteBrokenEmotion(String conect) {
        if (conect.lastIndexOf("#[") > conect.lastIndexOf("]#")) {
            conect = conect.substring(0, conect.lastIndexOf("#["));
        }
        return conect;
    }



    public void initEmotionString() {
        emotion = new HashMap();
        emotion.put("00", "/:00");
        emotion.put("01", "/:0k");
        emotion.put("02", "/:0l");
        emotion.put("03", "/:0m");
        emotion.put("04", "/:0o");
        emotion.put("05", "/:0h");
        emotion.put("07", "/:0j");
        emotion.put("08", "/:01");
        emotion.put("09", "/:02");
        emotion.put("10", "/:0d");
        emotion.put("11", "/:0f");
        emotion.put("12", "/:03");
        emotion.put("13", "/:04");
        emotion.put("14", "/:05");
        emotion.put("15", "/:06");
        emotion.put("16", "/:07");
        emotion.put("17", "/:08");
        emotion.put("18", "/:0g");
        emotion.put("20", "/:09");
        emotion.put("21", "/:0a");
        emotion.put("22", "/:0i");
        emotion.put("19", "/:0b");
        emotion.put("23", "/:0c");
        emotion.put("24", "/:0e");
        emotion.put("25", "/:0n");
    }

}
