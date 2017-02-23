package com.yunke.player.fragment;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.yunke.player.base.CommonFragment;
import com.yunke.player.ui.PlayVideoActivity;
import com.yunke.player.util.TLog;
import com.yunke.player.util.ToastUtil;
import com.yunke.player.MyWebSocketManager;
import com.yunke.player.util.MyEmotionUtil;
import com.yunke.player.util.StringToHtmlString;
import com.yunke.player.util.UIHelper;
import com.yunke.player.widget.MyEditText;

import butterknife.Bind;

public class GroupChatPressFragment extends CommonFragment {

    private LinearLayout sendMessage;
    private ImageView biaoqing;
    private ImageView ivKeyboard;
    private LinearLayout chat_face_container;
    private ViewPager mViewPager;
    private LinearLayout mDotsLayout;
    private MyEditText input;

    private static final String TAG = GroupChatPressFragment.class.getSimpleName();
    private final int messageCount = 5;

    private PlayVideoActivity mPlayVideoActivity;
    private final String SIGNAL = "signal";
    private final int SHOW_BIAOQ = 100;
    private final int SHOW_KEYBOARD = 102;
    private final int TEXT = 101;
    private final int SEND_CHAT_TYPE = 1;
    private final int delayTime = 200;

    private boolean ifCanSpeak;
    private MyEmotionUtil myEmotionUtil;

    @Override
    protected int getLayoutId() {
        return UIHelper.getResIdByName(getContext(), UIHelper.TYPE_LAYOUT, "yunke_fragment_group_chat_press");
    }

    @Override
    public void initView(View view) {
        super.initView(view);

        sendMessage = (LinearLayout)view.findViewById(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_ID, "send_message"));
        biaoqing = (ImageView)view.findViewById(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_ID, "biaoqing"));
        ivKeyboard = (ImageView)view.findViewById(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_ID, "iv_keyboard"));
        chat_face_container = (LinearLayout)view.findViewById(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_ID, "chat_face_container"));
        mViewPager = (ViewPager)view.findViewById(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_ID, "face_viewpager"));
        mDotsLayout = (LinearLayout)view.findViewById(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_ID, "face_dots_container"));
        input = (MyEditText)view.findViewById(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_ID, "shu_ru_kuang"));

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ifCanSpeak) {
                    sendMessage();
                }
            }
        });
        biaoqing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideSoftInputView();
                mHandlerMsg.sendEmptyMessageDelayed(SHOW_BIAOQ, delayTime);
            }
        });
        ivKeyboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivKeyboard.setVisibility(View.GONE);
                biaoqing.setVisibility(View.VISIBLE);
                handleControllerAnimationIsShow(false);
                mHandlerMsg.sendEmptyMessageDelayed(SHOW_KEYBOARD, delayTime);
            }
        });
    }

    @Override
    public void initData() {
        myEmotionUtil = new MyEmotionUtil(getActivity(), mDotsLayout, mViewPager, input);
        mPlayVideoActivity = (PlayVideoActivity) getActivity();

        myEmotionUtil.initStaticFaces();
        myEmotionUtil.InitViewPager();

        editOnClickListener();
        myEmotionUtil.initEmotionString();

        input.addTextChangedListener(mTextWatcher);

        showNoSendMessage();
    }

    private void showNoSendMessage() {
        input.setText(PlayVideoActivity.noSendMessage);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void editOnClickListener() {

        input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (chat_face_container.getVisibility() == View.VISIBLE) {
                    hideEmotionContainer();
                }

                WindowManager.LayoutParams params = mPlayVideoActivity.getWindow().getAttributes();
                if (params.softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE) {
                    imm = (InputMethodManager) mPlayVideoActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    imm.showSoftInput(input, InputMethodManager.SHOW_FORCED);
                }
            }
        });
    }

    private boolean ifCanSpeak() {
        //游客
        if (!mPlayVideoActivity.isApplyCourse()) {
            input.setHint("请报名后再聊天");
            return false;
        }
        return true;
    }

    private Handler mHandlerMsg = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_BIAOQ:
                    showEmotionContainer();
                    break;
                case TEXT:
                    showSoftInputView();
                    break;
                case SHOW_KEYBOARD:
                    hideEmotionContainer();
                    showSoftInputView();
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        ifCanSpeak = ifCanSpeak();//判断游客

        if (ifCanSpeak) {
            if (TextUtils.isEmpty(PlayVideoActivity.noSendMessage)) {
                input.setHint(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_STRING, "yunke_chat_hint"));
            } else {
                input.setText(PlayVideoActivity.noSendMessage);
            }

            input.requestFocus();
            showSoftInputView();
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        TLog.analytics(TAG, "onHiddenChanged() hidden=" + hidden);
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStop() {
        super.onStop();
        myEmotionUtil.staticFacesList.clear();
        myEmotionUtil.views.clear();
    }

    private void sendMessage() {

        String conect = input.getText().toString().trim();
        PlayVideoActivity.noSendMessage = "";
        input.setText("");

        //删除不完整的表情
        conect = myEmotionUtil.deleteBrokenEmotion(conect);

        SpannableString spannableString = new SpannableString(conect);
        String body = StringToHtmlString.stringToHtmlString(spannableString.toString());

        if (TextUtils.isEmpty(body)) {
            ToastUtil.showToast(getActivity(), getActivity().getString(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_STRING, "yunke_no_message")));
            return;
        }

        String content = myEmotionUtil.handlerSend(body);
        MyWebSocketManager.getInstance(getActivity()).sendMessage("text",content,SEND_CHAT_TYPE,0);
        hideEmotionContainer();
        hideSoftInputView();
        ivKeyboard.setVisibility(View.GONE);
        biaoqing.setVisibility(View.VISIBLE);
    }

    /**
     * 强制隐藏键盘
     */
    public void hideSoftInputView() {
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
    }

    private InputMethodManager imm;

    public void showSoftInputView() {
        ivKeyboard.setVisibility(View.GONE);
        biaoqing.setVisibility(View.VISIBLE);
        imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(input, InputMethodManager.SHOW_FORCED);
    }

    private void showEmotionContainer() {
        ivKeyboard.setVisibility(View.VISIBLE);
        biaoqing.setVisibility(View.GONE);
        chat_face_container.setVisibility(View.VISIBLE);
        handleControllerAnimationIsShow(true);
    }

    private void hideEmotionContainer() {
        chat_face_container.setVisibility(View.GONE);
    }


    private void handleControllerAnimationIsShow(boolean isShow) {
        TranslateAnimation translateAnimation = null;
        int height = chat_face_container.getHeight();
        int width = chat_face_container.getWidth();
        if (isShow) {
            translateAnimation = new TranslateAnimation(0, 0, height, 0);
            translateAnimation.setDuration(delayTime);
            chat_face_container.startAnimation(translateAnimation);

        } else {
            translateAnimation = new TranslateAnimation(0, 0, 0, height);
            translateAnimation.setDuration(delayTime);
            chat_face_container.startAnimation(translateAnimation);
        }
    }

    TextWatcher mTextWatcher = new TextWatcher() {
        private CharSequence temp;
        private int editStart;
        private int editEnd;

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            PlayVideoActivity.noSendMessage = s + "";
            temp = s;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            editStart = input.getSelectionStart();
            editEnd = input.getSelectionEnd();
            if (temp.length() >= 300) {
                ToastUtil.showToast(getActivity(), "你输入的字数已经超过了限制！");
                s.delete(editStart - 1, editEnd);
                int tempSelection = editStart;
                input.setText(s);
                input.setSelection(tempSelection);
            }
        }
    };

    // 设置全体禁言状态
    public void setAllForbidStatus(boolean isForbid) {
        if ((null == sendMessage) || (null == input)
                || (null == biaoqing) || (null == ivKeyboard)) {
            return;
        }

        if (isForbid) {
            sendMessage.setEnabled(false);
            biaoqing.setEnabled(false);
            ivKeyboard.setEnabled(false);

            input.setHint(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_STRING, "yunke_chat_forbid_all_hint"));
            input.setText("");
            input.setEnabled(false);
        } else {
            sendMessage.setEnabled(true);
            biaoqing.setEnabled(true);
            ivKeyboard.setEnabled(true);

            input.setHint(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_STRING, "yunke_chat_hint"));
            input.setEnabled(true);
        }
        return;
    }

    // 设置自己禁言状态
    public void setIForbidStatus(boolean isForbid) {
        if ((null == sendMessage) || (null == input)
                || (null == biaoqing) || (null == ivKeyboard)) {
            return;
        }

        if (isForbid) {
            sendMessage.setEnabled(false);
            biaoqing.setEnabled(false);
            ivKeyboard.setEnabled(false);

            input.setHint(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_STRING, "yunke_chat_forbid_i_hint"));
            input.setText("");
            input.setEnabled(false);
        } else {
            sendMessage.setEnabled(true);
            biaoqing.setEnabled(true);
            ivKeyboard.setEnabled(true);

            input.setHint(UIHelper.getResIdByName(getContext(), UIHelper.TYPE_STRING, "yunke_chat_hint"));
            input.setEnabled(true);
        }
        return;
    }
}


