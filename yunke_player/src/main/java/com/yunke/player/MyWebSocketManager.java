package com.yunke.player;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;

import com.google.gson.Gson;
import com.yunke.player.ui.PlayVideoActivity;
import com.yunke.player.util.SendBroadcastUtils;
import com.yunke.player.util.ToastUtil;
import com.yunke.player.api.ApiHttpClient;
import com.yunke.player.bean.Constants;
import com.yunke.player.bean.SendGroupMessageBean;
import com.yunke.player.bean.WebSocketBackEnty;
import com.yunke.player.bean.WebSocketBackResult;
import com.yunke.player.bean.WebSocketEnty;
import com.yunke.player.bean.WebSocketParamsEnty;
import com.yunke.player.util.ThreadUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import de.tavendo.autobahn.WebSocket;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketOptions;

public class MyWebSocketManager {

    public static final String SIGNAL = "signal";
    public static final String TEXT = "text";
    public static final String GOOD = "good";

    public static final int CT_STUDENT_ANSWER = 1018;
    public static final int CT_ON_OFF_LINE = 1006;
    public static final int CT_START_CLOSE = 1008;
    public static final int CT_CHAT_TEXT = 1;
    public static final int CT_CALL = 6;
    public static final int CT_REPLY = 7;
    public static final int CT_ASK_CANCEL = 1002;
    public static final int CT_AGREE_REFUSE = 1004;
    public static final int CT_PATTERN = 1010;
    public static final int CT_SINGLE_NOTALK = 1014;
    public static final int CT_GOOD = 100;
    public static final int CT_ASEED_TO_COMMENT = 1020;//老师请求学生评价
    public static final int CT_GOOD_DATA = 1024;//获取点赞信息
    public static final int CT_TEACHER_QUESTION = 1016;//老师出题
    public static final int CT_TEACHER_ANSWER = 1017;//老师公布答案
    public static final int CT_RETRIEVE_ANSWER_CARD = 1019;//强制收回答题卡
    public static final int CT_NOTICE_BOARD = 1030;//公告
    public static final int CT_DELETE_TEXT = 1022;
    public static final int CT_CAMERA = 1026;
    public static final int CT_MODIFY_STUDENT = 1200;
    public static final int CT_MICROPHONE_TEST = 700;
    public static final int CT_MICROPHONE_RESULT = 1012;
    public static final int CT_GIFT = 1040;
    public static final int CT_GIFT_CLEAR = 1041;

    private static MyWebSocketManager webSocketManager;
    private Context context;
    private WebSocketConnection wsc;
    private final int longConnection = 1000;
    private final int LONG_CONNECT = 10000;//长链接
    public int isFristGetConnData = 0;
    private final String TAG = MyWebSocketManager.class.getCanonicalName();
    public List<WebSocketEnty.ResultEntity> publicListData = new ArrayList<>();//服务端获取的信号
    public List<WebSocketEnty.ResultEntity> allGroupChatListData = new ArrayList<>();//聊天
    public String plan_id = "0";
    public int inChatListSize;//进入前集合长度
    public int outChatListSize;//退出前集合长度

    private int MAX_START_SIGNAL_ID;
    private int MAX_START_GOOD_ID;
    private int MAX_START_TEXT_ID;
    private int MIN_START_TEXT_ID;

    private final int NOTICE_CT = 1030;

    private MyWebSocketManager(Context context) {
        this.context = context;
    }

    public synchronized static MyWebSocketManager getInstance(Context context) {
        if (webSocketManager == null) {
            webSocketManager = new MyWebSocketManager(context);
        }
        return webSocketManager;
    }

    /**
     * 建立WebSocket通信
     * 重连用最大ID StartTextId
     * 加载用最小ID StartTextId
     * isLoadMore 加载 true，刷新false
     */
    private void connReceiveWebSocketData(String planId, boolean isLoadMore) {
        plan_id = planId;
        if (null == wsc) {
            wsc = new WebSocketConnection();
        }
        WebSocketOptions mWebSocketOptions = new WebSocketOptions();
        mWebSocketOptions.setMaxFramePayloadSize(1024 * 1024 * 2);
//        mWebSocketOptions.setMaskClientFrames(true);
//        mWebSocketOptions.setMaxMessagePayloadSize(1024 * 1024 * 2);
//        mWebSocketOptions.setReceiveTextMessagesRaw(true);
        mWebSocketOptions.setReconnectInterval(1000 * 10);//重连间隔

        final String message = getJsonString("", 0, isLoadMore);

        try {
            wsc.connect(ApiHttpClient.WEBSOCKET_URL, new WebSocket.ConnectionHandler() {

                @Override
                public void onOpen() {

                    if (onWebSocketStatusChanged != null) {
                        onWebSocketStatusChanged.onOpen();
                    }

                    if (wsc != null && message != null) {
                        try {
                            wsc.sendTextMessage(message);
                            Log.i(TAG, "第一次请求 ： " + message + " \n MAX_START_SIGNAL_ID ： " + MAX_START_SIGNAL_ID + " \n MAX_START_GOOD_ID : " + MAX_START_GOOD_ID + " \n MAX_START_TEXT_ID" + MAX_START_TEXT_ID);
                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.i(TAG, "e.printStackTrace();");
                        }
                    }

                    handler.removeMessages(longConnection);
                    handler.sendEmptyMessageDelayed(longConnection, LONG_CONNECT);

                }

                @Override
                public void onRawTextMessage(byte[] payload) {
                }

                @Override
                public void onBinaryMessage(byte[] bytes) {
                }

                @Override
                public void onTextMessage(final String payload) {

                    isFristGetConnData++;
                    Log.i(TAG, payload + " ： payload");
                    if (refresh != null) {
                        refresh.setRefreshing(false);
                    }

                    ThreadUtils.runInThread(new Runnable() {
                        @Override
                        public void run() {
                            WebSocketEnty mWebSocketEnty = null;
                            final String jsonString = "{" + "result :" + payload + "}";
                            try {
                                mWebSocketEnty = new Gson().fromJson(jsonString, WebSocketEnty.class);
                            } catch (Exception e) {
                                e.printStackTrace();
                                try {
                                    WebSocketBackEnty webSocketBackEnty = new Gson().fromJson(jsonString, WebSocketBackEnty.class);
                                    WebSocketBackResult webSocketBackResult = new Gson().fromJson(webSocketBackEnty.getResult().getInfo(), WebSocketBackResult.class);
                                    if (webSocketBackEnty.getResult().getKey().equals(TEXT)) {
                                        Intent intent = new Intent();
                                        intent.setAction(Constants.PLAY_VIDEO_GROUP_CHAT_PRESS_CLOSE);
                                        context.sendBroadcast(intent);
                                    } else if (webSocketBackEnty.getResult().getKey().equals(SIGNAL) && webSocketBackResult.getType() == CT_ASK_CANCEL) {
                                        if (onWebSocketStatusChanged != null) {
                                            onWebSocketStatusChanged.onWebSocketBackSignal();
                                        }
                                    }
                                } catch (Exception e1) {
                                    e1.printStackTrace();
                                }
                            }

                            if (mWebSocketEnty == null) {
                                return;
                            }

                            //清空信号 避免信号重新发送
                            publicListData = mWebSocketEnty.getResult();

                            try {
                                if ((null != publicListData) && (publicListData.size() > 0)) {
                                    for (int i = 0; i < publicListData.size(); i++) {
                                        WebSocketEnty.ResultEntity mResultEntity = publicListData.get(i);
                                        if (mResultEntity != null) {
                                            Log.i(TAG, "isFristGetConnData = " + isFristGetConnData);
                                            //过滤信号
                                            if (isFristGetConnData == 1 && (mResultEntity.getCt() == CT_CALL ||
                                                    mResultEntity.getCt() == CT_MICROPHONE_TEST || mResultEntity.getCt() == CT_START_CLOSE || mResultEntity.getCt() == CT_MICROPHONE_RESULT ||
                                                    mResultEntity.getCt() == CT_RETRIEVE_ANSWER_CARD || mResultEntity.getCt() == CT_TEACHER_QUESTION || mResultEntity.getCt() == CT_TEACHER_ANSWER ||
                                                    mResultEntity.getCt() == CT_ASEED_TO_COMMENT || mResultEntity.getCt() == CT_DELETE_TEXT || mResultEntity.getCt() == CT_GOOD_DATA ||
                                                    mResultEntity.getCt() == CT_GIFT || mResultEntity.getCt() == CT_GIFT_CLEAR || mResultEntity.getCt() == CT_MODIFY_STUDENT || mResultEntity.getCt() == CT_AGREE_REFUSE)) {
                                            } else if (mResultEntity.getMt().equals(TEXT)) {
                                                if (isFristGetConnData == 1) {
                                                    allGroupChatListData.add(mResultEntity);//添加文本
                                                    sortByStSgSs(St);
                                                    MIN_START_TEXT_ID = allGroupChatListData.get(0).getSt();//最小id
                                                    MAX_START_TEXT_ID = allGroupChatListData.get(allGroupChatListData.size() - 1).getSt();
                                                } else if (MAX_START_TEXT_ID < mResultEntity.getSt()) {
                                                    Log.i(TAG, "刷新 MAX_START_TEXT_ID = " + MAX_START_TEXT_ID);
                                                    allGroupChatListData.add(mResultEntity);//添加文本
                                                    MAX_START_TEXT_ID = mResultEntity.getSt();
                                                } else if ((MIN_START_TEXT_ID > mResultEntity.getSt())) {
                                                    Log.i(TAG, "加载 MIN_START_TEXT_ID = " + MIN_START_TEXT_ID);
                                                    allGroupChatListData.add(0, mResultEntity);
                                                    MIN_START_TEXT_ID = mResultEntity.getSt();
                                                }
                                                if (onWebSocketStatusChanged != null) {
                                                    onWebSocketStatusChanged.onAddText(mResultEntity, true);
                                                }
                                            } else if (mResultEntity.getMt().equals(SIGNAL)) {
                                                Log.i(TAG, "刷新 MAX_START_SIGNAL_ID = " + MAX_START_SIGNAL_ID);
//                                                if (mResultEntity.getSs() > MAX_START_SIGNAL_ID) {
                                                MAX_START_SIGNAL_ID = mResultEntity.getSs();
//                                                if (onWebSocketStatusChanged != null && (isFristGetConnData != 1 || mResultEntity.getCt() == NOTICE_CT)) {
                                                if (onWebSocketStatusChanged != null) {
                                                    onWebSocketStatusChanged.onAddSignal(mResultEntity);
                                                }
//                                                }
                                            } else if (mResultEntity.getMt().equals(GOOD)) {
                                                MAX_START_GOOD_ID = mResultEntity.getSg();
                                            }

                                            if (onWebSocketStatusChanged != null) {
                                                onWebSocketStatusChanged.onExecuteSignal(mResultEntity);
                                            }
                                        }
                                    }
                                    publicListData.clear();
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            SendBroadcastUtils.sendBroadcast(context, Constants.HAVE_NEW_WEBSOCKET_DATA);//发广播 通知群消息刷新
                        }
                    });

                }

                @Override
                public void onClose(int code, String reason) {
                    Log.i(TAG, "掉线 reason = " + reason + "；code = " + code);
                    if (onWebSocketStatusChanged != null) {
                        onWebSocketStatusChanged.onClose();
                    }

                }

            }, mWebSocketOptions);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getJsonString(String Content, int ContentType, boolean isLoadMore) {
        WebSocketParamsEnty mWebSocketParamsEnty = new WebSocketParamsEnty();

        mWebSocketParamsEnty.MessageType = "get";
        mWebSocketParamsEnty.UserIdFrom = PlayVideoActivity.uid;
        mWebSocketParamsEnty.UserFlagFrom = PlayVideoActivity.token.substring(0, 5);
        mWebSocketParamsEnty.PlanId = Integer.parseInt(plan_id);
        if (isLoadMore) {
            mWebSocketParamsEnty.StartTextId = MIN_START_TEXT_ID;
        } else {
            mWebSocketParamsEnty.StartTextId = MAX_START_TEXT_ID;
        }
        mWebSocketParamsEnty.StartSignalId = MAX_START_SIGNAL_ID;
        mWebSocketParamsEnty.StartGoodId = MAX_START_GOOD_ID;
        mWebSocketParamsEnty.Content = Content;
        mWebSocketParamsEnty.ContentType = ContentType;
        mWebSocketParamsEnty.TextLimit = -20;

        Gson gson = new Gson();
        String message = gson.toJson(mWebSocketParamsEnty);
        return message;
    }

    public boolean isRunInPlayVideoActivity = false;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case longConnection:
                    try {
                        if (isRunInPlayVideoActivity && wsc != null && wsc.isConnected()) {
                            wsc.sendTextMessage("{}");
                            Log.i(TAG, "长连接");
                            handler.sendEmptyMessageDelayed(longConnection, LONG_CONNECT);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };

    public boolean ifWscIsConn() {
        if (null != wsc && wsc.isConnected()) {
            return true;
        }
        return false;
    }

    /**
     * 打开WebSocket
     *
     * @param planId
     */
    public void openWebSocket(String planId) {
        connReceiveWebSocketData(planId, false);
    }

    private SwipeRefreshLayout refresh;

    /**
     * 加载更多消息
     */
    public void loadMoreMessage(SwipeRefreshLayout refresh) {
        this.refresh = refresh;
        if (null != wsc) {
            wsc.sendTextMessage(getJsonString("", 0, true));
        }
    }

    public void sendMessage(final String message_type, final String content, final int type, final int user_to_id) {
        Log.i(TAG, beanToJson(message_type, content, type, user_to_id));
        if (null != wsc) {
            ThreadUtils.runInThread(new Runnable() {
                @Override
                public void run() {
                    wsc.sendTextMessage(beanToJson(message_type, content, type, user_to_id));
                }
            });
        } else {
            ToastUtil.showToast(context, "发送失败");
        }
    }

    private String beanToJson(String message_type, String content, int type, int user_to_id) {
        SendGroupMessageBean sendGroupMessageBean = new SendGroupMessageBean();
        sendGroupMessageBean.content = content;
        sendGroupMessageBean.message_type = message_type;
        if (null != plan_id) {
            sendGroupMessageBean.plan_id = Integer.parseInt(plan_id);
        }
        sendGroupMessageBean.type = type;
        sendGroupMessageBean.user_to_id = user_to_id;
        sendGroupMessageBean.user_from_id = PlayVideoActivity.uid;
        sendGroupMessageBean.user_from_token = PlayVideoActivity.token;
        Gson gson = new Gson();
        String json = gson.toJson(sendGroupMessageBean);
        return json;
    }

    /**
     * 关闭WebSocket
     */
    public void closeWebSocket() {
        if (wsc != null) {
            wsc.disconnect();
        }
        MAX_START_SIGNAL_ID = 0;
        MAX_START_GOOD_ID = 0;
        MAX_START_TEXT_ID = 0;
        MIN_START_TEXT_ID = 0;
        isFristGetConnData = 0;
        wsc = null;
        publicListData = null;
        allGroupChatListData = null;
        plan_id = null;
        inChatListSize = 0;
        outChatListSize = 0;
        webSocketManager = null;
    }

    private OnWebSocketStatusChanged onWebSocketStatusChanged;

    public void setOnWebSocketStatusChanged(OnWebSocketStatusChanged onWebSocketStatusChanged) {
        this.onWebSocketStatusChanged = onWebSocketStatusChanged;
    }

    public interface OnWebSocketStatusChanged {
        void onOpen();

        void onClose();

        void onAddText(WebSocketEnty.ResultEntity mResultEntity, boolean b);

        void onAddSignal(WebSocketEnty.ResultEntity mResultEntity);

        void onExecuteSignal(WebSocketEnty.ResultEntity mResultEntity);

        void onWebSocketBackSignal();
    }

    private final int St = 1;
    private final int Ss = 2;
    private final int Sg = 3;

    private void sortByStSgSs(final int i) {
        Collections.sort(allGroupChatListData, new Comparator<WebSocketEnty.ResultEntity>() {

            @Override
            public int compare(WebSocketEnty.ResultEntity lhs, WebSocketEnty.ResultEntity rhs) {
                int a1 = 0;
                int a2 = 0;
                try {
                    if (i == 1) {
                        a1 = lhs.getSt();
                        a2 = rhs.getSt();
                    } else if (i == 2) {
                        a1 = lhs.getSs();
                        a2 = rhs.getSs();
                    } else if (i == 3) {
                        a1 = lhs.getSg();
                        a2 = rhs.getSg();
                    }

                    if (a1 > a2) {
                        return 1;
                    }
                    if (a1 == a2) {
                        return 0;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return -1;
            }

        });
    }

}
