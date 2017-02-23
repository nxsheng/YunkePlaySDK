package com.yunke.player.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yunke.player.MyWebSocketManager;
import com.yunke.player.base.CommonFragment;
import com.yunke.player.bean.Constants;
import com.yunke.player.bean.WebSocketEnty;
import com.yunke.player.ui.PlayVideoActivity;
import com.yunke.player.util.TLog;

import com.yunke.player.adapter.GroupChatAdapter;
import com.yunke.player.util.TDevice;
import com.yunke.player.util.UIHelper;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class GroupChatFragment extends CommonFragment {
    private static final String TAG = GroupChatFragment.class.getSimpleName();

    private TextView chat_promat;
    private RecyclerView chatList;
    private LinearLayout noMessage;
    private LinearLayout loading;
    private LinearLayout chat_press;
    private TextView chat_press_textview;
    private RelativeLayout group_chat_new;
    private TextView group_chat_new_text;
    private ImageView chat_disappear;
    private SwipeRefreshLayout refresh;

    private int noReadMessageBottom;//滑动查看时如果有新消息的差值
    private HaveNewWebSocketData mReceiverOut;
    private final int FILLUI = 103;
    private boolean notalkIsShowOnce = false; // 确保如果没有聊天信息，则提示只显示一次
    private GroupChatAdapter adapter;
    private PlayVideoActivity mPlayVideoActivity;
    private PopupWindow popWindow;

    private List<WebSocketEnty.ResultEntity> tempAllGroupChatListData = new ArrayList<>();//聊天

    @Override
    protected int getLayoutId() {
        return UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_LAYOUT, "yunke_fragment_group_chat");
    }

    private boolean isCover = false;

    @Override
    public void initView(View view) {
        super.initView(view);

        mPlayVideoActivity = (PlayVideoActivity) getActivity();

        chat_promat = (TextView)view.findViewById(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_ID, "chat_promat"));
        chatList = (RecyclerView)view.findViewById(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_ID, "chat_list"));
        noMessage = (LinearLayout)view.findViewById(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_ID, "no_message"));
        loading = (LinearLayout)view.findViewById(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_ID, "loading"));
        chat_press = (LinearLayout)view.findViewById(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_ID, "chat_press"));
        chat_press_textview = (TextView)view.findViewById(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_ID, "chat_press_textview"));
        group_chat_new = (RelativeLayout)view.findViewById(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_ID, "group_chat_new"));
        group_chat_new_text = (TextView)view.findViewById(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_ID, "group_chat_new_text"));
        chat_disappear = (ImageView)view.findViewById(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_ID, "chat_disappear"));
        refresh = (SwipeRefreshLayout)view.findViewById(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_ID, "refresh"));

        chat_press.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayVideoActivity.changeChatPressStats();
//                isCover = !isCover;
//                setGroupChatContentVisibal(isCover);
            }
        });

        group_chat_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                group_chat_new.setVisibility(View.INVISIBLE);
                chatList.scrollToPosition(MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.size() - noReadMessageBottom);
                startRecordMessageNumBottom();
            }
        });

        chat_disappear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPlayVideoActivity.setFloatLayerChatStatus(false);
            }
        });

        if (!mPlayVideoActivity.isApplyCourse()) {
            chat_press.setEnabled(false);
            chat_press_textview.setText(mPlayVideoActivity.getString(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_STRING, "yunke_group_chat_express_promt_trail")));
        }

        pullToRefresh();
    }

    // 删除某条聊天记录
    public void delOneChatRecordById(String id) {
        try {
            if ((null != MyWebSocketManager.getInstance(getActivity()).allGroupChatListData) && (MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.size() > 0)) {
                for (WebSocketEnty.ResultEntity entity : MyWebSocketManager.getInstance(getActivity()).allGroupChatListData) {
                    if (entity.getSt() == Integer.valueOf(id)) {
                        MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.remove(entity);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return;
    }

    // 设置聊天内容是否可见
    public void setGroupChatContentVisibal(boolean isVisibal) {
        try {
            if (null != adapter) {
                adapter.setContentVisibal(isVisibal);

                if (isVisibal) {
                    chat_promat.setVisibility(View.INVISIBLE);
                } else {
                    chat_promat.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 设置全体是否能发言
    public void setAllCanSpeak(boolean isCan) {
        try {
            if (!mPlayVideoActivity.isApplyCourse()) {
                return;
            }
            if (isCan) {
                chat_press.setEnabled(true);
                chat_press_textview.setText(mPlayVideoActivity.getString(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_STRING, "yunke_group_chat_express_promt_normal")));
            } else {
                chat_press.setEnabled(false);
                chat_press_textview.setText(mPlayVideoActivity.getString(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_STRING, "yunke_group_chat_express_promt_allforbid")));
            }
        } catch (Exception e) {
                e.printStackTrace();
            }
    }

    // 设置自己是否能发言
    public void setICanSpeak(boolean isCan) {
        try {
            if (!mPlayVideoActivity.isApplyCourse()) {
                return;
            }
            if (isCan) {
                chat_press.setEnabled(true);
                chat_press_textview.setText(mPlayVideoActivity.getString(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_STRING, "yunke_group_chat_express_promt_normal")));
            } else {
                chat_press.setEnabled(false);
                chat_press_textview.setText(mPlayVideoActivity.getString(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_STRING, "yunke_group_chat_express_promt_iforbid")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initData() {
        registRceiver();
        listViewOnScroll();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter = null;
        }
        getActivity().unregisterReceiver(mReceiverOut);
    }

    private void listViewOnScroll() {
        chatList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                dissmissPopWindow();
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (RecyclerView.SCROLL_STATE_IDLE == chatList.getScrollState()) {
                    if (lm.findViewByPosition(lm.findFirstVisibleItemPosition()).getTop() == 0 && lm.findFirstVisibleItemPosition() == 0) {
                        goToLastMessage();
                    }
                } else {
                    int showIdx = lm.findFirstVisibleItemPosition();
                    if (MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.size() - noReadMessageBottom == showIdx) {
                        noReadMessageBottom = 0;
                        group_chat_new.setVisibility(View.INVISIBLE);
                    }

                    if (lm.findLastVisibleItemPosition() >= (lm.getItemCount() - 1)) {
                        startRecordMessageNumBottom();
                    }/* else {
                        showNewMessageNumBottom(0);
                    }*/
                }
            }
        });
    }

    /**
     * 开始记录新消息上边显示
     */
    private void startRecordMessageNumBottom() {
        MyWebSocketManager.getInstance(getActivity()).inChatListSize = MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.size();
        group_chat_new.setVisibility(View.INVISIBLE);
    }

    private void showNewMessageNumBottom(int i) {
        noReadMessageBottom = MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.size() - MyWebSocketManager.getInstance(getActivity()).inChatListSize;
        if (noReadMessageBottom > i) {
            group_chat_new.setVisibility(View.VISIBLE);
            group_chat_new_text.setText(noReadMessageBottom + "条新消息");
        } else {
            group_chat_new.setVisibility(View.INVISIBLE);
        }
    }

    private Handler mHandlerMsg = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FILLUI:
                    fillUI(false);
                    break;
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        fillUI(true);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        TLog.analytics(TAG, "onHiddenChanged() hidden=" + hidden);
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    private synchronized void fillUI(final boolean flag) {
        showLoading();

        // 显示布局
        if (MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.size() == 0) {
            showNoMessage();
            if (adapter != null) {
                tempAllGroupChatListData.clear();
                chatList.removeAllViews();
            }
            return;
        } else {
            showListView();
            tempAllGroupChatListData.clear();
            tempAllGroupChatListData.addAll(MyWebSocketManager.getInstance(getActivity()).allGroupChatListData);

            if (adapter == null) {
                adapter = new GroupChatAdapter(mPlayVideoActivity, tempAllGroupChatListData);
                adapter.setOnRecyclerViewListener(new GroupChatAdapter.OnRecyclerViewListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                    }

                    @Override
                    public boolean onItemLongClick(View view, int position) {
                        return true;
                    }
                });
                chatList.setLayoutManager(new LinearLayoutManager(mPlayVideoActivity));
                int spacingInPixels = getResources().getDimensionPixelSize(UIHelper.getResIdByName(getActivity(), UIHelper.TYPE_DIMEN, "yunke_space_div_height"));
                chatList.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
                chatList.setItemAnimator(new DefaultItemAnimator());
                chatList.setAdapter(adapter);
            } else {
                adapter.notifyDataSetChanged();
            }

            // 计算 chatListll 高度
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    try {
                        float height = TDevice.getScreenHeight(getActivity());
                        int view_h = chatList.getMeasuredHeight();
                        if (view_h > (int) (height * 7 / 10)) {
                            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) chatList.getLayoutParams();
                            param.height = (int) (height * 7 / 10);
                            chatList.setLayoutParams(param);
                        }

                        if (flag) {
                            goToLastMessage();
                        } else {
                            try {
                                int idx = MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.size() - 1;
                                WebSocketEnty.ResultEntity item = MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.get(idx);
                                if (isMyselfByUid(item.getUf())) {
                                    goToLastMessage();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 100);
        }
    }

    private boolean isMyselfByUid(int uid) {
        boolean flag = false;

        if (PlayVideoActivity.uid == uid) {
            flag = true;
        }

        return  flag;
    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration{
        private int space;
        public SpaceItemDecoration(int space) {
            this.space = space;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            if(parent.getChildPosition(view) != 0)
                outRect.top = space;
        }
    }

    /**
     * 跳到最新一条消息
     */
    public void goToLastMessage() {
        try {
            if (MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.size() > 0) {
                chatList.scrollToPosition(MyWebSocketManager.getInstance(getActivity()).allGroupChatListData.size()-1);
                startRecordMessageNumBottom();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registRceiver() {
        mReceiverOut = new HaveNewWebSocketData();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.HAVE_NEW_WEBSOCKET_DATA);
        filter.addAction(Constants.INTENT_ACTION_CLASS_STUDENT_STATUS_CHANGE);
        getActivity().registerReceiver(mReceiverOut, filter);
    }

    public class HaveNewWebSocketData extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            showListView();
            mHandlerMsg.sendEmptyMessageDelayed(FILLUI, 0);

            try {
                LinearLayoutManager lm = (LinearLayoutManager) chatList.getLayoutManager();
                // 如果在底部，则继续滚动到当前最新的底部
                if (lm.findLastVisibleItemPosition() >= (lm.getItemCount() - 1)) {
                    goToLastMessage();
                } else { // 如果列表没在底部，则显示新消息
                    showNewMessageNumBottom(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showLoading() {
        if (loading == null || chatList == null || noMessage == null) {
            return;
        }
        loading.setVisibility(View.VISIBLE);
        chatList.setVisibility(View.INVISIBLE);
        noMessage.setVisibility(View.INVISIBLE);
    }

    public void showListView() {
        if (loading == null || chatList == null || noMessage == null) {
            return;
        }
        chatList.setVisibility(View.VISIBLE);
        loading.setVisibility(View.INVISIBLE);
        noMessage.setVisibility(View.INVISIBLE);
    }

    public void showNoMessage() {
        if (loading == null || chatList == null || noMessage == null) {
            return;
        }
        loading.setVisibility(View.INVISIBLE);
        chatList.setVisibility(View.INVISIBLE);
        if (false == notalkIsShowOnce) {
            notalkIsShowOnce = true;
            noMessage.setVisibility(View.VISIBLE);

            // 延迟隐藏
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        TranslateAnimation hidani = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
                        hidani.setDuration(500);
                        noMessage.startAnimation(hidani);
                        noMessage.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 2000);
        } else {
        }
    }

    private void dissmissPopWindow() {
        if (popWindow != null && popWindow.isShowing()) {
            popWindow.dismiss();
        }
    }

    private void pullToRefresh() {
        // 顶部刷新的样式
        refresh.setColorSchemeResources(android.R.color.holo_blue_light);
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh.setRefreshing(true);
                PlayVideoActivity playVideoActivity = (PlayVideoActivity)getActivity();
                MyWebSocketManager.getInstance(getActivity()).loadMoreMessage(refresh);
                refresh.setRefreshing(false);
            }
        });
    }


}


