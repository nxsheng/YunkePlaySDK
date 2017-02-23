package com.yunke.player.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;

import com.yunke.player.AnswerCardDialogManger;
import com.yunke.player.api.remote.GN100Api;
import com.yunke.player.bean.PlayPlanInfoResult;
import com.yunke.player.fragment.PlayVideoMoreFragmen;
import com.yunke.player.util.DateTimeUtil;
import com.yunke.player.util.ToastUtil;
import com.yunke.player.MyWebSocketManager;
import com.yunke.player.api.ApiHttpClient;
import com.yunke.player.api.remote.GN100Image;
import com.yunke.player.base.BaseActivity;
import com.yunke.player.bean.Constants;
import com.yunke.player.bean.GetGoodDataBean;
import com.yunke.player.bean.NoticeBean;
import com.yunke.player.bean.PlayVideoLogParams;
import com.yunke.player.bean.StudentCommentResult;
import com.yunke.player.bean.TeacherQuestionEnty;
import com.yunke.player.bean.WebSocketEnty;
import com.yunke.player.fragment.GroupChatFragment;
import com.yunke.player.fragment.GroupChatPressFragment;
import com.yunke.player.interf.IMediaPlayerListener;
import com.yunke.player.util.CommonUtil;
import com.yunke.player.util.DialogUtil;
import com.yunke.player.util.ImageUtils;
import com.yunke.player.util.SayHi;
import com.yunke.player.util.StringUtil;
import com.yunke.player.util.TDevice;
import com.yunke.player.util.TLog;
import com.yunke.player.util.ThreadUtils;
import com.yunke.player.util.ThreadWork;
import com.yunke.player.util.UIHelper;
import com.yunke.player.widget.PlayerSeekBar;
import com.yunke.player.widget.media.IjkVideoView;

import org.apache.http.Header;
import org.apache.http.util.TextUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * 视频播放界面
 */
public class PlayVideoActivity extends BaseActivity implements IMediaPlayerListener {

    private static final String PLAYER_TAG = "PLAYER";
    private static final String PLAYER_LOG_TAG = "PLAYER_LOG";
    private static final String SAYHI_TAG = "SAYHI";

    public static final String EXTRA_KEY_USER_ID = "EXTRA_KEY_USER_ID";
    public static final String EXTRA_KEY_USER_TOKEN = "EXTRA_KEY_USER_TOKEN";
    public static final String EXTRA_KEY_PLAN_ID = "EXTRA_KEY_PLAN_ID";
    public static final String EXTRA_KEY_SECTION_NAME = "EXTRA_KEY_SECTION_NAME";

    public static final String EXTRA_KEY_IS_REBACK_VIEW = "EXTRA_KEY_IS_REBACK_VIEW";
    public static final String EXTRA_KEY_REPLAY_POSITION = "EXTRA_KEY_REPLAY_POSITION";
    public static final String EXTRA_KEY_REPLAY_STATUS = "EXTRA_KEY_REPLAY_STATUS";

    public static final String URL_PARAM_KEY_TOKEN = "token";
    public static final String URL_PARAM_KEY_PLANID = "planid";

    public static final int PLAY_PERMISSION_DEFAULT = 0; // 默认播放权限
    public static final int PLAY_PERMISSION_FREE = 1; // 免费播放章节
    public static final int PLAY_PERMISSION_TRY_SEE = 2; // 试看播放权限

    private final int MSG_KEY_HIDE_CONTROLLER = 1; // 隐藏控制器
    private final int MSG_KEY_START_CONTROLLER_HIDE_ANIMATION = 2; // 开启控制器隐藏的动画
    private final int MSG_KEY_UPDATE_PROGRESS = 3; // 更新播放进度的消息
    private final int MSG_KEY_UPLOAD_PLAY_LOG = 4; // 上传播放日志信息
    private final int MSG_KEY_HIDE_LOCK = 5; // 隐藏锁
    private final int MSG_KEY_HIDE_TIP_MSG_1 = 6; // 隐藏灰色提示信息框（tip message 1）
    private final int MSG_KEY_HIDE_TIP_MSG_2 = 7; // 隐藏蓝色提示信息框（tip message 2）

    private static final int MAX_PROGRESS_VALUE = 1000; // 进度最大值
    private static final int CONTROLLER_HIDE_DELAY_MILLIS = 5000; // 默认的控制器隐藏的延迟时间
    private static final int CONTROLLER_HIDE_ANIMATION_DELAY_MILLIS = 4800; // 默认的控制器隐藏动画开始的延迟时间

    private static final int PLAYER_STATE_ERROR = -1; // 播放页，异常状态
    private static final int PLAYER_STATE_IDLE = 0; // 播放页，初始状态
    private static final int PLAYER_STATE_PREPARING = 1; // 播放页，预加载状态
    private static final int PLAYER_STATE_PREPARED = 2; // 播放页，加载完毕状态
    private static final int PLAYER_STATE_PLAYING = 3; // 播放页，播放状态
    private static final int PLAYER_STATE_PAUSED = 4; // 播放页，暂停状态
    private static final int PLAYER_STATE_COMPLETED = 5; // 播放页，播放完毕状态
    private static final int PLAYER_STATE_DRAGGING = 6; // 播放页，拖拽进度状态

    private static final int MIC_STATE_HIDE = -1; // 隐藏
    private static final int MIC_STATE_IDLE = 0; // 闲置
    private static final int MIC_STATE_RAISE_HAND = 1; // 申请发言
    private static final int MIC_STATE_SPEAKING = 2; // 正在发言
    private static final int MIC_STATE_CANCEL_RAISE_HEAD = 3; // 取消发言

    private static final String FRAGMENT_TAG_NULL = "NULL"; // 空
    private static final String FRAGMENT_TAG_GROUP_CHAT_PRESS = "GROUP_CHAT_PRESS"; // 群聊发送
    private static final String FRAGMENT_TAG_PLAY_VIDEO_MORE = "PLAY_VIDEO_MORE"; // 更多设置

    public static final int DEFAULT_SOCKET_TIMEOUT = 5 * 1000;// 默认超时时间

    // 播放界面状态
    // 错误界面View
    private ViewGroup view_play_video_error;
    private TextView tv_play_video_error_status;
    private ImageView iv_play_video_error_status;
    // 完成界面View
    private ViewGroup viewFinishClass;
    private TextView tvFinishText;
    private ViewGroup llFinishLiveBroadcastTip;
    private TextView tvRank;
    private TextView tvGood;
    private ImageButton tvFinishReplay;
    // 首次加载界面View
    private ViewGroup viewFirstLoading;
    private ImageView ivFirstLoading;
    private TextView tvFirstLoaingContent;
    // 加载界面View
    private ViewGroup viewLoading;
    // 加载失败界面View
    private ViewGroup viewLoadingFailed;
    private ImageButton btnLoadingFailedReload;

    // Right Tool View
    private LinearLayout llRightTool;
    private ImageView ivMessage;

    // Left Tool View
    private LinearLayout llLeftTool;
    private ImageView ivAnswerCard;
    private RelativeLayout rlSpeak;
    private ImageView ivSpeakNormal;
    private ImageView ivSpeaking;
    private TextView tvSpeaking;
    private RelativeLayout rlSpeakOther;
    private ImageView ivSpeakOther;
    private TextView tvSpeakOther;

    // 提示信息界面
    private TextView tvTipMessage2;
    private RelativeLayout rlTipMessage1;
    private TextView tvTipMessage1;
    private ImageView ivTipDeleteMessage1;
    private View lineMessage1;

    // Top Tool View
    private RelativeLayout rlTopbar;
    private ImageButton goBack;

    // Bottom Tool View
    private RelativeLayout rlBottombar;
    private ImageButton ibMore;
    private ViewGroup viewPlayVideoSeekbar;
    private PlayerSeekBar seekbarVideo;
    private TextView tvVideoCurrentTime;
    private TextView tvVideoDuration;
    private ImageView ivPlay;

    // Fragment 插件
    private FrameLayout mFloatLayerPlayVideoMoreContainer;
    private FrameLayout mFloatLayerCenterContainer;
    private FrameLayout mFloatLayerChatContainer;
    private FrameLayout mFloatLayerChatPressContainer;
    private LinearLayout ll_chat_and_notice_container;
    private ImageView notice;

    // 其他View
    private RelativeLayout rlVideo;
    private IjkVideoView mVideoView;
    private LinearLayout noConnWs;
    private ImageView ivLock;
    private ImageView iv_good;

    // 进度预览View
    private LinearLayout thumbsLL;
    private TextView tvThumbsTime;
    private ImageView ivThumbs;
    private ProgressBar pbThumbs;

    public static String noSendMessage = "";

    //------------------信号处理相关--------------------
    private final int MSG_KEY_MANAGE_SIGNAL = 12;//处理信号
    private WebSocketEnty.ResultEntity signalResultEntity;
    private WebSocketEnty.ResultEntity goodResultEntity;
    private final int MSG_KEY_CLOSE_GOOD_POP = 9; // 关闭点赞
    // -------------------------- 公用参数begin --------------------------
    public static int uid = 0;
    public static String token = "";
    public String planId = "";
    public String classId;
    public String courseId;
    public String teacher_id; // 老师ID
    public String sectionName; // 传过来的章节名，用于初次加载时显示
    public StudentCommentResult mStudentCommentResult;
    public boolean isLiveBroadcast = false; // 是否是直播课程
    public List<PlayPlanInfoResult.StreamEntity> mStreamList;
    public boolean isSrcDefinition = true; // 是否为原画
    public List<PlayPlanInfoResult.RtmpEntity> cdnRtmpList = new ArrayList<>();
    public List<PlayPlanInfoResult.HlsEntity> cdnHlsList = new ArrayList<>();
    public int mCurrentCdnLineIdx = 0; // 选择线路的序号
    public int mCurrentDefinitionIdx = 0; // 选择清晰度的序号
    // -------------------------- 公用参数end --------------------------

    // -------------------------- WebSocket维护参数begin --------------------------
    private MyWebSocketManager myWebSocketManager;
    private final int RASE_HAND_OR_CANCEL = 1002;//举手发言和取消发言 学生发起
    private final String SIGNAL = "signal";
    private List<WebSocketEnty.ResultEntity> goodsDataList = new ArrayList<>();

    private AnswerCardDialogManger answerCardDialogManger;

    // -------------------------- WebSocket维护参数end --------------------------

    private boolean mBackPressed = false;
    private int mPlayerState = PLAYER_STATE_IDLE; // 播放器状态
    private int mUserOperatePlayerState = PLAYER_STATE_PLAYING; // 播放器状态
    private int mMicState = PLAYER_STATE_IDLE; // 发言状态
    private String mCurrentShowFragmentTag = FRAGMENT_TAG_NULL;
    private boolean mControllerIsShow = true; // 播放器控制器是否显示
    private boolean mFloatLayerChatPressIsShow = false; // 发送浮动布局是否显示
    private boolean isChangeVideoDefinition = false; // 是否切换视频的清晰度
    private boolean isRebackView = false; // 是否重新返回該應用
    private boolean isChangeVideoLine = false; // 是否切换视频的线路
    private boolean mFloatLayerPlayVideoMoreContainerIsShow = false; // 是否显示了更多
    public PlayPlanInfoResult mPlayInfoResult; // 播放视频的详细信息
    private int mDuration; // 视频总时长
    private int mPosition; // 视频当前播放时长
    private int mTagPosition; // 视频标记播放时长
    private StringBuffer mPlayUrl = new StringBuffer();
    private StringBuffer mSayHiPubUrl = new StringBuffer();
    private StringBuffer mSayHiPlayUrl = new StringBuffer();
    private SayHi mSayHi;
    private AlertDialog mSpeakDialog;
    private FragmentManager mFragmentManager;
    private GroupChatFragment mGroupChatFragment = null;
    private GroupChatPressFragment mGroupChatPressFragment = null;
    private PlayVideoMoreFragmen mPlayVideoMoreFragment = null;
    private PlayVideoLogParams mPlayVideoLogParams = null;
    private PlayVideoLogParams.BfEntity mVideoBf = null;
    private long mVideoPrepareBegin;
    private long mVideoPrepareEnd;
    private long mPlayVideoLogParamsStartTime = System.currentTimeMillis();
    private MyReceiver mReceiver;
    private boolean isCommented = false; // 是否已评价
    private AnimationDrawable animationDrawable;
    private boolean isLock = false; // 屏幕是否已经上锁
    private boolean isThumbsDownload = false; // 是否已经开始下载缩略图
    private int mThumbsCurrentImgIdx = -1; // 记录当前显示的缩略图序号
    private ThreadWork.Tracker mTracker = new ThreadWork.Tracker();
    private long mLeftToolSpeakRequestCountDownInterval = 1000; // 一秒钟执行一次
    private long mLeftToolSpeakRequestMillisInFuture = 60 * 1000; // 请求发言倒计时总时长
    private boolean mPermissionRecordAudio = false; // 是否有录音权限

    private final CountDownTimer mLeftToolSpeakRequestTimer = new CountDownTimer(mLeftToolSpeakRequestMillisInFuture, mLeftToolSpeakRequestCountDownInterval) {
        @Override
        public void onTick(long millisUntilFinished) {
            tvSpeaking.setText(String.valueOf(millisUntilFinished / 1000));
        }

        @Override
        public void onFinish() {
            sendRequestCancelRaiseHand();
            setSpeakStatus(MIC_STATE_IDLE);
        }
    };
    private long mWaitCourseCountDownInterval = 1000 * 60; // 一分钟执行一次
    private long mWaitCourseMillisInFuture; // 请求发言倒计时总时长
    private CountDownTimer mWaitCourseTimer;

    public class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ConnectivityManager.CONNECTIVITY_ACTION)) {
                handleNoWifiPlay();
            } else if (intent.getAction().equalsIgnoreCase(Constants.PLAY_VIDEO_GUID_CLOSE)) {
                setFloatCenterLayerStatus(false);
            } else if (intent.getAction().equalsIgnoreCase(Constants.PLAY_VIDEO_GROUP_CHAT_PRESS_CLOSE)) {
                setFloatLayerChatPressStatus(false);

                // 群聊列表滚动到底部
                if (null != mGroupChatFragment) {
                    mGroupChatFragment.goToLastMessage();
                }
            } else if (intent.getAction().equalsIgnoreCase(Constants.COMMENT_SUCCESS)) {
                setCommentStatus(true);
            }
        }
    }

    private Handler mHandlerMsg = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_KEY_HIDE_TIP_MSG_2:
                    hideTipMessage2();
                    break;
                case MSG_KEY_HIDE_TIP_MSG_1:
                    hideTipMessage1();
                    break;
                case MSG_KEY_HIDE_CONTROLLER:
                    hideController();
                    break;
                case MSG_KEY_START_CONTROLLER_HIDE_ANIMATION:
                    startControllerHideAnimation();
                    break;
                case MSG_KEY_UPDATE_PROGRESS:
                    if (isLiveBroadcast || mPlayerState != PLAYER_STATE_PLAYING) {
                        return;
                    }
                    if (!isApplyCourse() && getPlayPermission() == PLAY_PERMISSION_TRY_SEE) { // 未报名 && 可试看
                        handleTrySee();
                    }
                    updateProgress();
                    break;
                case MSG_KEY_UPLOAD_PLAY_LOG:
                    sendPostPlayLog();
                    break;
                case MSG_KEY_HIDE_LOCK:
//                    showLockView(false);
                    break;
                case MSG_KEY_MANAGE_SIGNAL:
                    //答到 发言 点赞 下课
                    receiveSignalAndGood();
                    break;
                case MSG_KEY_CLOSE_GOOD_POP:
                    iv_good.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    };

    private final TextHttpResponseHandler mHandlerPlayInfoApi = new TextHttpResponseHandler(ApiHttpClient.HEADER_ACCEPT_ENCODING_UTF8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String response) {
            TLog.analytics(PLAYER_TAG, "playInfo " + response);
            try {
                // 处理THUMBS问题
                response = procResponseThumb(response);

                mPlayInfoResult = StringUtil.jsonToObject(response, PlayPlanInfoResult.class);
                if (mPlayInfoResult != null && mPlayInfoResult.error == null) {
                    handleRequestPlayInfoDataSuccess(response);

                    // 设置标题
                    if (mPlayInfoResult.section != null) {
                        tvFinishText.setText(mPlayInfoResult.section.name + " " + mPlayInfoResult.section.desc);
                    }

                    // 设置老师ID
                    teacher_id = mPlayInfoResult.plan.teacherId;

                    // 设置清晰度序号
                    for (int idx = 0; idx < mStreamList.size(); idx++) {
                        PlayPlanInfoResult.StreamEntity entity = mStreamList.get(idx);
                        if (!android.text.TextUtils.isEmpty(mPlayInfoResult.default_clear) && mPlayInfoResult.default_clear.equalsIgnoreCase(entity.name)) {
                            mCurrentDefinitionIdx = idx;
                        }
                    }

                    // 开始下载Thumbs
                    downloadThumbs();
                } else { // 异常状态
                    mPlayerState = PLAYER_STATE_ERROR;
                    if (mPlayInfoResult == null || PlayPlanInfoResult.ErrorEntity.ERROR_CODE_REQUEST_FAILED.equals(mPlayInfoResult.error.code)) { // 接口异常
                        showLoadingFailedView();
                        return;
                    }
                    classId = mPlayInfoResult.plan.classId;
                    courseId = mPlayInfoResult.plan.courseId;
                    setPlayVideoErrorStatus(mPlayInfoResult.error.code);

                    // 初始化WebSocket
                    if (PlayPlanInfoResult.ErrorEntity.ERROR_CODE_NO_START.equalsIgnoreCase(mPlayInfoResult.error.code)) {
                        // 设置老师ID
                        teacher_id = mPlayInfoResult.plan.teacherId;
                    }
                }
            } catch (Exception e) {
                TLog.error(PLAYER_TAG, e.getLocalizedMessage());
                mPlayerState = PLAYER_STATE_ERROR;
                showLoadingFailedView();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
            TLog.analytics(PLAYER_TAG, "playInfo load failed");
            mPlayerState = PLAYER_STATE_ERROR;
            showLoadingFailedView();
        }
    };

    private void initWebSocket() {
        myWebSocketManager = MyWebSocketManager.getInstance(PlayVideoActivity.this);
        myWebSocketManager.isRunInPlayVideoActivity = true;
        myWebSocketManager.openWebSocket(planId);
        OnMyWebSocketStatusChanged();
    }

    private String procResponseThumb(String response) {
        String str = response;

        try {
            JSONObject jsonObject = new JSONObject(response);
            String thumb = jsonObject.getString("thumbs");

            if ((null == thumb) || (thumb.length() <= 10)) {
                thumb = "{\"cols\": 0,\"rows\": 0,\"width\": 0,\"height\": 0,\"last_num\": 0,\"interval\": 0,\"data\": []}";
                jsonObject.put("thumbs", new JSONObject(thumb));
                str = jsonObject.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return str;
    }

    /**
     * WebSocket接收消息
     */
    private void OnMyWebSocketStatusChanged() {
        if (myWebSocketManager != null) {
            myWebSocketManager.setOnWebSocketStatusChanged(new MyWebSocketManager.OnWebSocketStatusChanged() {
                @Override
                public void onOpen() {
                    goodsDataList.clear();
                    myGoodNum = 0;
                    if (noConnWs.getVisibility() == View.VISIBLE) {
                        noConnWs.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onClose() {
                    noConnWs.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAddText(WebSocketEnty.ResultEntity mResultEntity, boolean b) {
                }

                @Override
                public void onAddSignal(WebSocketEnty.ResultEntity mResultEntity) {
                    signalResultEntity = mResultEntity;
                }

                @Override
                public void onExecuteSignal(WebSocketEnty.ResultEntity mResultEntity) {
                    getGoodsData(mResultEntity);
                    mHandlerMsg.sendEmptyMessage(MSG_KEY_MANAGE_SIGNAL);
                }

                @Override
                public void onWebSocketBackSignal() {
                    if (MIC_STATE_CANCEL_RAISE_HEAD == mMicState) { // 发起取消发言
                        TLog.analytics(SAYHI_TAG, "发起取消发言 callback");
                        ThreadUtils.runUIThread(new Runnable() {
                            @Override
                            public void run() {
                                setSpeakStatus(MIC_STATE_IDLE);
                            }
                        });
                    }
                }
            });
        }

    }

    private final JsonHttpResponseHandler mHandlerPlayLog = new JsonHttpResponseHandler(ApiHttpClient.HEADER_ACCEPT_ENCODING_UTF8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            TLog.analytics(PLAYER_TAG, "play log : " + response.toString());
        }
    };

    private final SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(final SeekBar seekBar, final int progress, boolean fromUser) {
            if (!fromUser) {
                return;
            }
            mPosition = (int) (Float.valueOf(progress) / MAX_PROGRESS_VALUE * mDuration);
            tvVideoCurrentTime.setText(StringUtil.stringForTime(mPosition));
            if (fromUser) {
                // 预览图显示
                thumbsLL.setVisibility(View.VISIBLE);
                tvThumbsTime.setText(StringUtil.stringForTime(mPosition));

                // 根据SeekBar位置来显示预览图
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //execute the task
                        try {
                            moveThumbsLL(seekBar, progress);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, 1);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            TLog.analytics(PLAYER_TAG, "onStartTrackingTouch()");
            mPlayerState = PLAYER_STATE_DRAGGING;
            removeUpdateProgressMsg();
            removeDelayedHideControllerMsg();
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            TLog.analytics(PLAYER_TAG, "onStopTrackingTouch()");
            // 预览图隐藏
            thumbsLL.setVisibility(View.GONE);

            mPlayerState = PLAYER_STATE_PLAYING;
            TLog.analytics(PLAYER_TAG, "seekTo mPosition = " + mPosition);
            mVideoView.seekTo(mPosition);
            sendDelayedHideControllerMsg(CONTROLLER_HIDE_DELAY_MILLIS, CONTROLLER_HIDE_ANIMATION_DELAY_MILLIS);
            removeDelayedHideControllerMsg();
            sendUpdateProgressMsg(0);

            signalResultEntity = null;
            goodResultEntity = null;
        }
    };
    private final AdapterView.OnItemClickListener listChoiceDefinitionOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            showLoadingView();
            switchVideoDefinition(i);
            sendDelayedHideControllerMsg(CONTROLLER_HIDE_DELAY_MILLIS, CONTROLLER_HIDE_ANIMATION_DELAY_MILLIS);
            removeDelayedHideControllerMsg();
            sendUpdateProgressMsg(0);
        }
    };

    @Override
    protected int getLayoutId() {
        return UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_LAYOUT, "yunke_activity_play_video");
    }

    @Override
    public void initView() {
        super.initView();
        TLog.analytics(PLAYER_TAG, "initView()");

        view_play_video_error = (ViewGroup)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "view_play_video_error"));
        tv_play_video_error_status = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_play_video_error_status"));
        iv_play_video_error_status = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_play_video_error_status"));
        // 完成界面View
        viewFinishClass = (ViewGroup)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "view_play_video_finish"));
        tvFinishText = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_finish_text"));
        llFinishLiveBroadcastTip = (ViewGroup)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "ll_finish_live_broadcast_tip"));
        tvRank = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_rank"));
        tvGood = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_good"));
        tvFinishReplay = (ImageButton)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "ib_finish_replay"));
        // 首次加载界面View
        viewFirstLoading = (ViewGroup)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "view_play_video_first_loading"));
        ivFirstLoading = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_first_loading"));
        tvFirstLoaingContent = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_first_loading_content"));
        // 加载界面View
        viewLoading = (ViewGroup)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "view_play_video_loading"));
        // 加载失败界面View
        viewLoadingFailed = (ViewGroup)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "view_play_video_loading_failed"));
        btnLoadingFailedReload = (ImageButton)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "btn_loading_failed_reload"));

        // Right Tool View
        llRightTool = (LinearLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "ll_right_tool"));
        ivMessage = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_message"));

        // Left Tool View
        llLeftTool = (LinearLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "ll_left_tool"));
        ivAnswerCard = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_answer_card"));
        rlSpeak = (RelativeLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "rl_speak"));
        ivSpeakNormal = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_speak_normal"));
        ivSpeaking = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_speak_ing"));
        tvSpeaking = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_speak_ing"));
        rlSpeakOther = (RelativeLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "rl_speak_other"));
        ivSpeakOther = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_speak_other"));
        tvSpeakOther = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_speak_other"));

        // 提示信息界面
        tvTipMessage2 = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_tip_message_2"));
        rlTipMessage1 = (RelativeLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "rl_tip_message_1"));
        tvTipMessage1 = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_tip_message_1"));
        ivTipDeleteMessage1 = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_tip_delete_message_1"));
        lineMessage1 = (View)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "line_message_1"));

        // Top Tool View
        rlTopbar = (RelativeLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "rl_topbar"));
        goBack = (ImageButton)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "go_back"));

        // Bottom Tool View
        rlBottombar = (RelativeLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "rl_bottombar"));
        ibMore = (ImageButton)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "ib_more"));
        viewPlayVideoSeekbar = (ViewGroup)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "view_play_video_seekbar"));
        seekbarVideo = (PlayerSeekBar)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "seekbar_video"));
        tvVideoCurrentTime = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_video_current_time"));
        tvVideoDuration = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "tv_video_duration"));
        ivPlay = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_play"));

        // Fragment 插件
        mFloatLayerPlayVideoMoreContainer = (FrameLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "float_layer_play_video_more_container"));
        mFloatLayerCenterContainer = (FrameLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "float_layer_center_container"));
        mFloatLayerChatContainer = (FrameLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "float_layer_chat_container"));
        mFloatLayerChatPressContainer = (FrameLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "float_layer_chat_press_container"));
        ll_chat_and_notice_container = (LinearLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "ll_chat_and_notice_container"));
        notice = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "notice"));

        // 其他View
        rlVideo = (RelativeLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "rl_play_video"));
        mVideoView = (IjkVideoView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "video_view"));
        noConnWs = (LinearLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "no_conn_ws"));
        ivLock = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_lock"));
        iv_good = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_good"));

        // 进度预览View
        thumbsLL = (LinearLayout)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "view_video_thumbs_ll"));
        tvThumbsTime = (TextView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "view_video_thumbs_time_text"));
        ivThumbs = (ImageView)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "view_video_thumbs_image"));
        pbThumbs = (ProgressBar)findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "view_video_thumbs_progress"));

        seekbarVideo.setMax(MAX_PROGRESS_VALUE);
        seekbarVideo.setOnSeekBarChangeListener(mOnSeekBarChangeListener);

        mVideoView.setOnPreparedListener(this);
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnCompletionListener(this);
        mVideoView.setOnErrorListener(this);

        setTouchListener();
        setClickListener();
    }

    private void setClickListener() {
        rlBottombar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickVideoListener();
            }
        });

        // 播放器屏幕
        rlVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickVideoListener();
            }
        });

        // 重新加載
        btnLoadingFailedReload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRequestPlayInfoData();
            }
        });

        // 退出
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backOpration();
            }
        });

        // 播放or暂停
        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handlePauseOrStart();
            }
        });

        // 请求发言
        rlSpeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isApplyCourse()) { // 未报名
                    ToastUtil.showToast(PlayVideoActivity.this, getString(UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_STRING, "yunke_tip_cannot_speak")));
                    return;
                }
                if (mPlayerState == PLAYER_STATE_ERROR) {
                    if (PlayPlanInfoResult.ErrorEntity.ERROR_CODE_ERROR_CLASS.equals(mPlayInfoResult.error.code)) {
                        ToastUtil.showToast(PlayVideoActivity.this, "不能在当前班级发言");
                        return;
                    }
                    if (PlayPlanInfoResult.ErrorEntity.ERROR_CODE_NO_START.equals(mPlayInfoResult.error.code)) {
                        ToastUtil.showToast(PlayVideoActivity.this, "老师还未上课");
                        return;
                    }
                }
                if (mPlayerState == PLAYER_STATE_COMPLETED) {
                    ToastUtil.showToast(PlayVideoActivity.this, getString(UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_STRING, "yunke_tip_play_completed")));
                    return;
                }
                if (mPlayInfoResult.publishChat == null) {
                    ToastUtil.showToast(PlayVideoActivity.this, getString(UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_STRING, "yunke_tip_no_speak")));
                    return;
                }
                if (MIC_STATE_IDLE == mMicState) { // 发起申请发言
                    setSpeakStatus(MIC_STATE_RAISE_HAND);
                } else if (MIC_STATE_RAISE_HAND == mMicState) { // 发起取消发言
                    sendRequestCancelRaiseHand();
                }
            }
        });

        // 打开或者关闭 更多设置（录播）
        ibMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayVideoMoreSetting();
            }
        });

        // 打开或者关闭 更多设置
        tvTipMessage1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlayVideoMoreSetting();
            }
        });

        // 播放完毕后：重播
        tvFinishReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleVideoRePlay();
            }
        });

        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (View.VISIBLE == ll_chat_and_notice_container.getVisibility()) { // 关闭聊天界面
                    setFloatLayerChatStatus(false);
                } else { // 显示聊天界面
                    showGroupChat();
                }
            }
        });

        // 答题卡
        ivAnswerCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                answerCardDialogManger.answerCardDialog(PlayVideoActivity.this);
            }
        });

        // 公告
        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(noticeBody)) {
                    answerCardDialogManger.showNoticeDialog(noticeBody, PlayVideoActivity.this);
                }
            }
        });

        ivLock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLock) {
                    isLock = false;
                    ivLock.setImageResource(UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_video_play_unlock"));

                    // 显示控制
                    showController();
                } else {
                    isLock = true;
                    ivLock.setImageResource(UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_video_play_lock"));

                    // 隐藏控制
                    hideController();
                    mHandlerMsg.removeMessages(MSG_KEY_HIDE_LOCK);
                    mHandlerMsg.sendEmptyMessageDelayed(MSG_KEY_HIDE_LOCK, 3000);
                }
            }
        });
    }

    private void clickVideoListener() {
        if (mFloatLayerChatPressIsShow) {
            setFloatLayerChatPressStatus(false);
            TDevice.hideSoftKeyboard(this, rlVideo);
        }
        if (mFloatLayerPlayVideoMoreContainerIsShow) {
            setFloatLayerPlayVidoeMore(false);
        }

        if (mControllerIsShow) {
            sendHideControllerMsg();
        } else {
            showController();
        }
    }

    public void setTouchListener() {
        rlVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sendDelayedHideControllerMsg(CONTROLLER_HIDE_DELAY_MILLIS, CONTROLLER_HIDE_ANIMATION_DELAY_MILLIS);
                return false;
            }
        });

        llRightTool.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sendDelayedHideControllerMsg(CONTROLLER_HIDE_DELAY_MILLIS, CONTROLLER_HIDE_ANIMATION_DELAY_MILLIS);
                return false;
            }
        });
        llLeftTool.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sendDelayedHideControllerMsg(CONTROLLER_HIDE_DELAY_MILLIS, CONTROLLER_HIDE_ANIMATION_DELAY_MILLIS);
                return false;
            }
        });
        rlTopbar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sendDelayedHideControllerMsg(CONTROLLER_HIDE_DELAY_MILLIS, CONTROLLER_HIDE_ANIMATION_DELAY_MILLIS);
                return false;
            }
        });
        rlBottombar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                sendDelayedHideControllerMsg(CONTROLLER_HIDE_DELAY_MILLIS, CONTROLLER_HIDE_ANIMATION_DELAY_MILLIS);
                return false;
            }
        });
    }

    @Override
    public void initData() {
        super.initData();
        TLog.analytics(PLAYER_TAG, "initData()");
        mFragmentManager = getSupportFragmentManager();

        uid = getIntent().getExtras().getInt(EXTRA_KEY_USER_ID);
        token = getIntent().getExtras().getString(EXTRA_KEY_USER_TOKEN);
        planId = getIntent().getExtras().getString(EXTRA_KEY_PLAN_ID);
        sectionName = getIntent().getExtras().getString(EXTRA_KEY_SECTION_NAME);
        if (mGroupChatPressFragment == null) {
            mGroupChatPressFragment = new GroupChatPressFragment();
        }
        if (answerCardDialogManger == null) {
            answerCardDialogManger = new AnswerCardDialogManger(ivAnswerCard);
        }

        initReceiver();
        initPlayer();
        initSayHi();
        initPermission();
        handlePlayVideo();
        initWebSocket();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initAsyncHttp();
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            TLog.analytics(PLAYER_TAG, "onCreate savedInstanceState = " + savedInstanceState);
            isRebackView = savedInstanceState.getBoolean(EXTRA_KEY_IS_REBACK_VIEW);
            mPosition = savedInstanceState.getInt(EXTRA_KEY_REPLAY_POSITION);
            mPlayerState = savedInstanceState.getInt(EXTRA_KEY_REPLAY_STATUS);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (outState != null) {
            outState.putBoolean(EXTRA_KEY_IS_REBACK_VIEW, true);
            outState.putInt(EXTRA_KEY_REPLAY_POSITION, mPosition);
            outState.putInt(EXTRA_KEY_REPLAY_STATUS, mPlayerState);
            TLog.analytics(PLAYER_TAG, "onSaveInstanceState outState = " + outState);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        TLog.analytics(PLAYER_TAG, "onRestart()");
        isRebackView = true;
        handleVideoRePlay();
    }

    @Override
    protected void onResume() {
        super.onResume();
        TLog.analytics(PLAYER_TAG, "onResume()");
        handleVideoPlay();
    }

    @Override
    protected void onPause() {
        super.onPause();
        TLog.analytics(PLAYER_TAG, "onPause()");
        // 如果不是直播，则暂停处理
        if (!isLiveBroadcast) {
            handleVideoPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        TLog.analytics(PLAYER_TAG, "onStop()");
        noSendMessage = "";
        handleVideoPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        TLog.analytics(PLAYER_TAG, "onDestroy()");
        mBackPressed = true;
        closeSayHi();
        // 关闭WebSocket
        if (myWebSocketManager != null) {
            myWebSocketManager.isRunInPlayVideoActivity = true;
            myWebSocketManager.closeWebSocket();
            myWebSocketManager = null;
        }
        answerCardDialogManger.dismissDialog();

        if (mSayHi != null) {
            mSayHi.ClosePlayer();
        }


        // 释放广播接收者
        unregisterReceiver();
        // 线程池释放
        mTracker.cancellAll();
        // 释放资源
        System.gc();
        mLeftToolSpeakRequestTimer.cancel();
        if (mWaitCourseTimer != null)
            mWaitCourseTimer.cancel();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        TLog.analytics(PLAYER_TAG, "onActivityResult requestCode = " + requestCode + " , resultCode = " + resultCode + " data = " + data);
        switch (requestCode) {
            case Constants.REQUEST_CODE_KEY_PLAYER_NOTE: // seekTo/update
                break;
            case Constants.REQUEST_CODE_KEY_PLAYER_NOTE_SAVE: // save 打点
                break;
        }
    }

    @Override
    public void onBackPressed() {
        TLog.analytics(PLAYER_TAG, "onBackPressed()");
        backOpration();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        TLog.analytics(PLAYER_TAG, " keyCode = " + keyCode);
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            TLog.analytics(PLAYER_TAG, "press home key");
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handlePlayVideo() {
        sendRequestPlayInfoData();
        handleNoWifiPlay();
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        TLog.analytics(PLAYER_TAG, "onPrepared()");
        mPlayerState = PLAYER_STATE_PREPARED;
        mPlayVideoLogParams.vv = PlayVideoLogParams.VV_DEFAULT;

        // 开始播放即显示浮层
        showController();
    }

    @Override
    public void onCompletion(IMediaPlayer mp) {
        TLog.analytics(PLAYER_TAG, "onCompletion()");
        mPlayerState = PLAYER_STATE_COMPLETED;
//        mVideoView.stopPlayback();
        mVideoView.pause();
        removeUpdateProgressMsg();
        updatePauseOrPlayUI();
        sendPostPlayLog();
        showPlayFinishedView();

        // 关闭浮层
        if (mFloatLayerChatPressIsShow) {
            setFloatLayerChatPressStatus(false);
            TDevice.hideSoftKeyboard(this, rlVideo);
        }
        if (mControllerIsShow) {
            sendHideControllerMsg();
        }
    }

    @Override
    public boolean onError(IMediaPlayer mp, int what, int extra) {
        TLog.analytics(PLAYER_TAG, "onError()");
        if (mPlayerState == PLAYER_STATE_COMPLETED) return false;

        mPlayerState = PLAYER_STATE_ERROR;
        showLoadingFailedView();

        // 关闭浮层
        if (mFloatLayerChatPressIsShow) {
            setFloatLayerChatPressStatus(false);
            TDevice.hideSoftKeyboard(this, rlVideo);
        }
        if (mControllerIsShow) {
            sendHideControllerMsg();
        }

        return false;
    }

    @Override
    public boolean onInfo(IMediaPlayer mp, int what, int extra) {
        TLog.analytics(PLAYER_TAG, "onInfo()");
        switch (what) {
            case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                TLog.analytics(PLAYER_TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                if (mVideoPrepareEnd == 0) {
                    mVideoPrepareEnd = System.currentTimeMillis();
                    mPlayVideoLogParams.vst = (int) (mVideoPrepareEnd - mVideoPrepareBegin);
                }
                mPlayVideoLogParams.vv = PlayVideoLogParams.VV_PLAYING;
                mPlayVideoLogParams.tt = mVideoView.getDuration() == -1 ? 0 : mVideoView.getDuration();
                handlePlayOnPrepared();
                if (isRebackView) { // 重返應用
                    TLog.analytics(PLAYER_TAG, " seekTo position = " + mPosition);
                    if (!isLiveBroadcast) {
                        mVideoView.seekTo(mPosition);
                    }
                    isRebackView = false;
                }
                if (isChangeVideoDefinition) { // 切换视频清晰度的操作
                    TLog.analytics(PLAYER_TAG, " seekTo position = " + mTagPosition);
                    if (!isLiveBroadcast) {
                        mVideoView.seekTo(mTagPosition);
                    }
                    isChangeVideoDefinition = false;
                }
                if (isChangeVideoLine) { // 切换视频线路的操作
                    TLog.analytics(PLAYER_TAG, " seekTo position = " + mTagPosition);
                    if (!isLiveBroadcast) {
                        mVideoView.seekTo(mTagPosition);
                    }
                    isChangeVideoLine = false;
                }
                hideLoadingView();
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                TLog.analytics(PLAYER_TAG, "MEDIA_INFO_BUFFERING_START");
                mVideoBf.b = true;
                switch (mVideoBf.t.size()) {
                    case 0:
                        mVideoBf.t.add(0, (long) mVideoView.getCurrentPosition());
                        break;
                    case 2:
                        mVideoBf.t.add(2, System.currentTimeMillis() / 1000);
                        break;
                }
                showLoadingView();
                break;
            case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                TLog.analytics(PLAYER_TAG, "MEDIA_INFO_BUFFERING_END");
                mVideoBf.b = false;
                switch (mVideoBf.t.size()) {
                    case 1:
                        mVideoBf.t.add(1, (long) mVideoView.getCurrentPosition());
                        break;
                    case 3:
                        mVideoBf.t.add(3, System.currentTimeMillis() / 1000);
                        mVideoBf = new PlayVideoLogParams.BfEntity();
                        mPlayVideoLogParams.bf.add(mVideoBf);
                        break;
                }
                hideLoadingView();
                break;
        }
        return true;
    }

    /**
     * 播放器初始化
     */
    private void initPlayer() {
        mPlayerState = PLAYER_STATE_PREPARING;
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    /**
     * 关闭播放器
     */
    private void closePlayer() {
        mBackPressed = true;
        finish();
        mVideoView.stopPlayback();
        mVideoView.release(true);
        mVideoView.stopBackgroundPlay();
        mPlayerState = PLAYER_STATE_IDLE;
        IjkMediaPlayer.native_profileEnd();
    }

    /**
     * SayHi的初始化
     */
    private void initSayHi() {
        TLog.analytics(SAYHI_TAG, "initSayHi");
        mSayHi = SayHi.getInstance();
        TLog.analytics(SAYHI_TAG, "initSayHi End");
    }

    /**
     * 关闭SayHi
     */
    private void closeSayHi() {
        TLog.analytics(SAYHI_TAG, "closeSayHi");
        if (mSayHi != null) {
            closeSayHiPub();
            closeSayHiPlayer();
        }
        TLog.analytics(SAYHI_TAG, "closeSayHi End");
    }

    /**
     * 开启SayHi，发言
     */
    private void openSayHiPub() {
        TLog.analytics(SAYHI_TAG, "openSayHiPub");
        handleSayHiPubUrl();
        closeSayHiPlayer();
        if (mSayHi != null && !mSayHiPubUrl.toString().isEmpty()) {
            mSayHi.OpenPublisher(mSayHiPubUrl.toString());
        }
        TLog.analytics(SAYHI_TAG, "openSayHiPub End");
    }

    /**
     * 关闭SayHi，发言
     */
    private void closeSayHiPub() {
        TLog.analytics(SAYHI_TAG, "closeSayHiPub");
        if (mSayHi != null) {
            mSayHi.ClosePublisher();
        }
    }

    /**
     * 开启SayHi，播放发言
     */
    private void openSayHiPlayer() {
        TLog.analytics(SAYHI_TAG, "openSayHiPlayer");
        if (!isLiveBroadcast) return; // 录播课，没有发言
        if (mPlayInfoResult.chat == null) return;
        handleSayHiPlayUrl();
        closeSayHiPlayer();
        mSayHi.OpenPlayer(mSayHiPlayUrl.toString());
        mSayHi.ResumePlayer();
    }

    /**
     * 关闭SayHi，播放发言
     */
    private void closeSayHiPlayer() {
        TLog.analytics(SAYHI_TAG, "closeSayHiPlayer");
        if (mSayHi != null) {
            mSayHi.PausePlayer();
        }
        hideOtherSpeaker();
        TLog.analytics(SAYHI_TAG, "closeSayHiPlayer End");
    }

    private void initReceiver() {
        mReceiver = new MyReceiver();
        IntentFilter intent = new IntentFilter();
        intent.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intent.addAction(Constants.PLAY_VIDEO_GUID_CLOSE);
        intent.addAction(Constants.PLAY_VIDEO_GROUP_CHAT_PRESS_CLOSE);
        intent.addAction(Constants.COMMENT_SUCCESS);
        registerReceiver(mReceiver, intent);
    }

    private void unregisterReceiver() {
        if (mReceiver != null) {
            unregisterReceiver(mReceiver);
            mReceiver = null;
        }
    }

    /**
     * 处理非WIFI播放
     */
    private void handleNoWifiPlay() {
        if (!TDevice.isWifiNetwork(this)) { // 非WIFI状态
            handleVideoPause(); // 暂停播放
            ToastUtil.showToast(this, getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_toast_move_internet_status")));
            DialogUtil.showConfirmDialog(false, this, "",
                    getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_tip_confirm_open_switch_move_internet_play")),
                    getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_jixu_shangke")),
                    getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_cancel")),
                    new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    handleVideoPlay(); // 继续播放
                }
            }, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    closePlayer();
                }
            });
        }
    }

    /**
     * 初始化所需权限
     */
    private void initPermission() {
        int permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO);
        mPermissionRecordAudio = permission != PackageManager.PERMISSION_GRANTED ? false : true;
    }

    /**
     * 设置播放界面聊天ICON的状态
     *
     * @param isShow
     */
    private void setChatStatus(boolean isShow) {
        if (isShow) {
            hideMessageView();
        } else {
            showMessageView();
        }
        ivMessage.setSelected(!isShow);
    }

    /**
     * 设置浮层显示或隐藏的状态
     *
     * @param isShow
     */
    public void setFloatLayerChatStatus(boolean isShow) {
        if (isShow) {
            ll_chat_and_notice_container.setVisibility(View.VISIBLE);
            CommonUtil.translateAnimationRight(true, ll_chat_and_notice_container);
            setChatStatus(true);
        } else {
            CommonUtil.translateAnimationRight(false, ll_chat_and_notice_container);
            ll_chat_and_notice_container.setVisibility(View.GONE);
            setChatStatus(false);
        }
    }

    /**
     * 设置群聊发送浮层显示或隐藏的状态
     *
     * @param isShow
     */
    private void setFloatLayerChatPressStatus(boolean isShow) {
        mFloatLayerChatPressIsShow = isShow;
        if (isShow) {
            mFloatLayerChatPressContainer.setVisibility(View.VISIBLE);
            CommonUtil.translateAnimationRight(true, mFloatLayerChatPressContainer);
            TDevice.cancelFullScreen(PlayVideoActivity.this);
        } else {
            CommonUtil.translateAnimationRight(false, mFloatLayerChatPressContainer);
            mFloatLayerChatPressContainer.setVisibility(View.GONE);
            TDevice.setFullScreen(PlayVideoActivity.this);
        }
    }

    /**
     * 设置居中浮层显示或隐藏的状态
     *
     * @param isShow
     */
    private void setFloatCenterLayerStatus(boolean isShow) {
        if (isShow) {
            mFloatLayerCenterContainer.setVisibility(View.VISIBLE);
            CommonUtil.translateAnimationRight(true, mFloatLayerCenterContainer);
        } else {
            CommonUtil.translateAnimationRight(false, mFloatLayerCenterContainer);
            mFloatLayerCenterContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 设置 更多设置Fragment的状态
     *
     * @param isShow
     */
    public void setFloatLayerPlayVidoeMore(boolean isShow) {
        mFloatLayerPlayVideoMoreContainerIsShow = isShow;
        if (isShow) {
            mFloatLayerPlayVideoMoreContainer.setVisibility(View.VISIBLE);
            CommonUtil.translateAnimationRight(true, mFloatLayerPlayVideoMoreContainer);
        } else {
            CommonUtil.translateAnimationRight(false, mFloatLayerPlayVideoMoreContainer);
            mFloatLayerPlayVideoMoreContainer.setVisibility(View.GONE);
        }
    }

    /**
     * 关闭浮层
     */
    public void finishFloatLayerFragment() {
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        Fragment tagFragment = mFragmentManager.findFragmentByTag(mCurrentShowFragmentTag);
        if (tagFragment != null) {
            fragmentTransaction.remove(tagFragment).commitAllowingStateLoss();
        }

        mCurrentShowFragmentTag = FRAGMENT_TAG_NULL;
        setFloatCenterLayerStatus(false);
        setFloatLayerChatStatus(false);
        setFloatLayerChatPressStatus(false);
        setFloatLayerPlayVidoeMore(false);
    }

    /**
     * 显示控制器
     */
    private void showController() {
        handleControllerViewIsShow(true);
        handleControllerAnimationIsShow(true);

        mControllerIsShow = true;
        sendDelayedHideControllerMsg(CONTROLLER_HIDE_DELAY_MILLIS, CONTROLLER_HIDE_ANIMATION_DELAY_MILLIS);
        sendUpdateProgressMsg(0);

        // 左侧工具栏位置设置
        RelativeLayout.LayoutParams leftToolsParams = (RelativeLayout.LayoutParams) llLeftTool.getLayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // 移除 ALIGN_PARENT_BOTTOM 操作
            leftToolsParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            leftToolsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        }
        leftToolsParams.setMargins(leftToolsParams.leftMargin, leftToolsParams.topMargin, leftToolsParams.rightMargin, (int) TDevice.dpToPixel(this, 16));
        leftToolsParams.addRule(RelativeLayout.ABOVE, rlBottombar.getId());
        llLeftTool.setLayoutParams(leftToolsParams);

        // 灰色提示框位置设置
        RelativeLayout.LayoutParams tip1LayoutParams = (RelativeLayout.LayoutParams) rlTipMessage1.getLayoutParams();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) { // 移除 ALIGN_PARENT_BOTTOM 操作
            tip1LayoutParams.removeRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        } else {
            tip1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
        }
        tip1LayoutParams.setMargins(tip1LayoutParams.leftMargin, tip1LayoutParams.topMargin, tip1LayoutParams.rightMargin, (int) TDevice.dpToPixel(this, 16));
        tip1LayoutParams.addRule(RelativeLayout.ABOVE, rlBottombar.getId());
        rlTipMessage1.setLayoutParams(tip1LayoutParams);

        showLeftToolView();
        showRightToolView();
    }

    /**
     * 隐藏控制器
     */
    private void sendHideControllerMsg() {
        mControllerIsShow = false;
        sendDelayedHideControllerMsg(300, 0);
    }

    /**
     * 发送延迟隐藏控制器的消息
     *
     * @param viewDelayMillis      控件隐藏的延迟时间
     * @param animationDelayMillis 控件隐藏动画开始的延迟时间
     */
    private void sendDelayedHideControllerMsg(int viewDelayMillis, int animationDelayMillis) {
        removeDelayedHideControllerMsg();
        if (mControllerIsShow) { // 显示状态，状态栏透明效果，失去占位效果
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS); // 状态栏透明，失去占位效果
        }

        mHandlerMsg.sendEmptyMessageDelayed(MSG_KEY_HIDE_CONTROLLER, viewDelayMillis);
        mHandlerMsg.sendEmptyMessageDelayed(MSG_KEY_START_CONTROLLER_HIDE_ANIMATION, animationDelayMillis);
        sendUpdateProgressMsg(0);
    }

    /**
     * 移除延迟隐藏控制器的消息
     */
    private void removeDelayedHideControllerMsg() {
        mHandlerMsg.removeMessages(MSG_KEY_HIDE_CONTROLLER);
        mHandlerMsg.removeMessages(MSG_KEY_START_CONTROLLER_HIDE_ANIMATION);
    }

    /**
     * 处理隐藏控制器的消息
     */
    private void hideController() {
        if (!mFloatLayerChatPressIsShow) {
            TDevice.setFullScreen(PlayVideoActivity.this);
        }
        handleControllerViewIsShow(false);
        mControllerIsShow = false;

        // 左侧工具栏位置设置
        RelativeLayout.LayoutParams leftToolsParams = (RelativeLayout.LayoutParams) llLeftTool.getLayoutParams();
        leftToolsParams.setMargins(leftToolsParams.leftMargin, leftToolsParams.topMargin, leftToolsParams.rightMargin, (int) TDevice.dpToPixel(this, 6));
        leftToolsParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        llLeftTool.setLayoutParams(leftToolsParams);

        // 灰色提示框位置设置
        RelativeLayout.LayoutParams tip1LayoutParams = (RelativeLayout.LayoutParams) rlTipMessage1.getLayoutParams();
        tip1LayoutParams.setMargins(tip1LayoutParams.leftMargin, tip1LayoutParams.topMargin, tip1LayoutParams.rightMargin, (int) TDevice.dpToPixel(this, 16));
        tip1LayoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        rlTipMessage1.setLayoutParams(tip1LayoutParams);

        hideLeftToolView();
        hideRightToolView();
    }

    /**
     * 处理开启控制器的隐藏动画
     */
    private void startControllerHideAnimation() {
        handleControllerAnimationIsShow(false);
    }

    /**
     * 处理控制器的隐藏和显示
     *
     * @param isShow
     */
    private void handleControllerViewIsShow(boolean isShow) {
        if (isShow) {
            rlTopbar.setVisibility(View.VISIBLE);
            rlBottombar.setVisibility(View.VISIBLE);
        } else {
            rlTopbar.setVisibility(View.INVISIBLE);
            rlBottombar.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * 处理控制器的隐藏动画和显示动画
     *
     * @param isShow
     */
    private void handleControllerAnimationIsShow(boolean isShow) {
        TranslateAnimation translateAnimation = null;
        int height = rlTopbar.getHeight();
        if (isShow) {
            translateAnimation = new TranslateAnimation(0, 0, -height, 0);
            translateAnimation.setDuration(200);
            rlTopbar.startAnimation(translateAnimation);
            DisplayMetrics metric = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metric);
            height = rlBottombar.getHeight();
            translateAnimation = new TranslateAnimation(0, 0, height, 0);
            translateAnimation.setDuration(200);
            rlBottombar.startAnimation(translateAnimation);
        } else {
            translateAnimation = new TranslateAnimation(0, 0, 0, -height);
            translateAnimation.setDuration(300);
            rlTopbar.startAnimation(translateAnimation);
            height = rlBottombar.getHeight();
            translateAnimation = new TranslateAnimation(0, 0, 0, height);
            translateAnimation.setDuration(300);
            rlBottombar.startAnimation(translateAnimation);
        }
    }

    /**
     * 显示更多操作
     */
    private void showPlayVideoMoreSetting() {
        mFloatLayerPlayVideoMoreContainerIsShow = true;
        if (null != mPlayVideoMoreFragment) {
            setFloatLayerPlayVidoeMore(true);
            return;
        }

        mCurrentShowFragmentTag = FRAGMENT_TAG_PLAY_VIDEO_MORE;
        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (mPlayVideoMoreFragment == null) {
            mPlayVideoMoreFragment = new PlayVideoMoreFragmen();
        }
        fragmentTransaction.replace(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "float_layer_play_video_more_container"), mPlayVideoMoreFragment, FRAGMENT_TAG_PLAY_VIDEO_MORE).commitAllowingStateLoss();
        setFloatLayerPlayVidoeMore(true);
    }

    /**
     * 显示群聊
     */
    private void showGroupChat() {
        if (null != mGroupChatFragment) {
            setFloatLayerChatStatus(true);
            return;
        }

        FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
        if (mGroupChatFragment == null) {
            mGroupChatFragment = new GroupChatFragment();
        }

        fragmentTransaction.replace(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "float_layer_chat_container"), mGroupChatFragment, null).commitAllowingStateLoss();
        setFloatLayerChatStatus(true);
    }

    /**
     * 显示群聊编辑界面
     */
    public void changeChatPressStats() {
        if (!mFloatLayerChatPressIsShow) {
            if (null != mGroupChatPressFragment) {
                setFloatLayerChatPressStatus(true);
                return;
            }

            mCurrentShowFragmentTag = FRAGMENT_TAG_GROUP_CHAT_PRESS;
            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            if (mGroupChatPressFragment == null) {
                mGroupChatPressFragment = new GroupChatPressFragment();
            }
            fragmentTransaction.replace(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "float_layer_chat_press_container"), mGroupChatPressFragment, FRAGMENT_TAG_GROUP_CHAT_PRESS).commitAllowingStateLoss();
            setFloatLayerChatPressStatus(true);
        } else {
            setFloatLayerChatPressStatus(false);
            mCurrentShowFragmentTag = FRAGMENT_TAG_NULL;
        }
    }

    /**
     * 离开播放界面
     */
    private void backOpration() {
        // 如果群聊编辑显示，则隐藏编辑
        if (mFloatLayerChatPressIsShow) {
            setFloatLayerChatPressStatus(false);
            return;
        }

        // 提示退出
        DialogUtil.showConfirmDialog(false, mContext, "",
                getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_tip_confirm_exit_classroom")),
                getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_video_exit")),
                getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_video_stay_here")),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        closePlayer();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }
        );
    }

    /**
     * 发送获取播放详细信息的请求
     */
    private void sendRequestPlayInfoData() {
        showFirstLoadingView();
        GN100Api.playPlanInfo(this, planId, mHandlerPlayInfoApi);
    }

    /**
     * 成功获取播放信息后的处理
     */
    private void handleRequestPlayInfoDataSuccess(String response) {
        if ((mPlayInfoResult == null) || (null == mPlayInfoResult.plan)
                || ((cdnRtmpList == null) && (cdnHlsList == null))) {
            return;
        }
        classId = mPlayInfoResult.plan.classId;
        courseId = mPlayInfoResult.plan.courseId;

        // 处理播放类型
        parsePlayType();
//        // 开启播放他人发言
//        openSayHiPlayer();
        // 处理CDN线路信息
        handleCDNLine(response);
        // 处理视频清晰度选项列表
        handleShowPlayStreamOption();
        // 播放地址的处理
        handlePlayUrl(0);
        // 处理播放日志信息
        handlePlayLogInfo();
        //录播课获取公告
        getNotice();

        if (!TextUtils.isEmpty(mPlayUrl)) {
            mVideoView.setVideoPath(mPlayUrl.toString());
            mVideoView.start();
            if (mVideoPrepareBegin == 0) {
                mVideoPrepareBegin = System.currentTimeMillis();
            }

            // 第一次正式播放显示引导页
            /*
            if (AppContext.getInstance().isFirstPlayVideo()) {
                showGuid();
            }
            */
        }
    }

    /**
     * 获取公告
     */
    private void getNotice() {
        if (!isLiveBroadcast) {
            GN100Api.getNotice(this, planId, mHandlerNotice);
        }
    }

    private final TextHttpResponseHandler mHandlerNotice = new TextHttpResponseHandler(ApiHttpClient.HEADER_ACCEPT_ENCODING_UTF8) {
        @Override
        public void onSuccess(int statusCode, Header[] headers, String response) {
            TLog.analytics("getNotice", "playInfo " + response);
            try {
                NoticeBean noticeBean = StringUtil.jsonToObject(response, NoticeBean.class);
                if (noticeBean.OK()) {
                    noticeBody = noticeBean.getResult().getContent();
                    if (!TextUtils.isEmpty(noticeBody)) {
                        showNotice(noticeBody);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onFailure(int statusCode, Header[] headers, String errorResponse, Throwable throwable) {
            TLog.analytics("getNotice", "onFailure");
        }
    };


    /**
     * 通过网络接口数据处理播放日志信息
     */
    private void handleCDNLine(String response) {
        try {
            JSONObject jResponse = new JSONObject(response);
            // RTMP
            if (isLiveBroadcast) {
                cdnRtmpList.clear();
                if ((null != mPlayInfoResult.cdn_rtmp) && (mPlayInfoResult.cdn_rtmp.size() > 0)) {
                    for (int i = 0; i < mPlayInfoResult.cdn_rtmp.size(); i++) {
                        PlayPlanInfoResult.RtmpLineInfoEntity info = mPlayInfoResult.cdn_rtmp.get(i);
                        JSONObject jRtmp = jResponse.optJSONObject(info.rtmp);
                        PlayPlanInfoResult.RtmpEntity rtmpInfo = StringUtil.jsonToObject(jRtmp.toString(), PlayPlanInfoResult.RtmpEntity.class);
                        cdnRtmpList.add(rtmpInfo);

                        if (info.defaultType.equalsIgnoreCase("1")) {
                            mCurrentCdnLineIdx = i;
                        }
                    }
                }
            } else {
                // HLS
                cdnHlsList.clear();
                if ((null != mPlayInfoResult.cdn_hls) && (mPlayInfoResult.cdn_hls.size() > 0)) {
                    for (int i = 0; i < mPlayInfoResult.cdn_hls.size(); i++) {
                        PlayPlanInfoResult.HlsLineInfoEntity info = mPlayInfoResult.cdn_hls.get(i);
                        JSONObject jHls = jResponse.optJSONObject(info.hls);
                        PlayPlanInfoResult.HlsEntity hlsInfo = StringUtil.jsonToObject(jHls.toString(), PlayPlanInfoResult.HlsEntity.class);
                        cdnHlsList.add(hlsInfo);

                        if (info.defaultType.equalsIgnoreCase("1")) {
                            mCurrentCdnLineIdx = i;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 通过网络接口数据处理播放日志信息
     */
    private void handlePlayLogInfo() {
        if (mPlayInfoResult != null) {
            mPlayVideoLogParams = new PlayVideoLogParams(uid+"", mPlayInfoResult.user.ip.ip, mPlayInfoResult.user.ip.opName,
                    mPlayInfoResult.user.ip.areaName, mPlayInfoResult.plan.organizationUserId,
                    mPlayInfoResult.plan.courseId, mPlayInfoResult.plan.sectionId, mPlayInfoResult.plan.classId, mPlayInfoResult.plan.planId);
        } else {
            mPlayVideoLogParams = new PlayVideoLogParams();
        }

        mPlayVideoLogParams.lgt = (int) (System.currentTimeMillis() - mPlayVideoLogParamsStartTime);
        if (!isLogin()) {
            mPlayVideoLogParams.uid = mPlayInfoResult.user.info.uid;
        }
        mVideoBf = new PlayVideoLogParams.BfEntity();
        mPlayVideoLogParams.bf.add(mVideoBf);

        if (isLiveBroadcast) { // 直播
            mPlayVideoLogParams.pm = PlayVideoLogParams.PM_RTMP;
            mPlayVideoLogParams.cdnid = cdnRtmpList.get(mCurrentCdnLineIdx).cdnId;
            mPlayVideoLogParams.vid = PlayVideoLogParams.VID_DEFAULT;
        } else { // 录播
            mPlayVideoLogParams.pm = PlayVideoLogParams.PM_HLS;
            mPlayVideoLogParams.cdnid = cdnHlsList.get(mCurrentCdnLineIdx).cdnId;
            mPlayVideoLogParams.vid = Integer.parseInt(cdnHlsList.get(mCurrentCdnLineIdx).videoId);
        }
        mHandlerMsg.sendEmptyMessage(MSG_KEY_UPLOAD_PLAY_LOG);
    }

    /**
     * 通过网络接口数据判断播放类型
     */
    private void parsePlayType() {
        if (mPlayInfoResult != null) {
            try {
                if ("rtmp".equals(mPlayInfoResult.playmode)) { // 直播
                    isLiveBroadcast = true;
                } else if ("hls".equals(mPlayInfoResult.playmode)) { // 录播
                    isLiveBroadcast = false;
                } else {
                    isLiveBroadcast = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 处理播放器显示的播放类型
     *
     * @param isLiveBroadcast
     */
    private void handlePlayerType(boolean isLiveBroadcast) {
        if (isLiveBroadcast) {
            showLiveBroadcastView();
        } else {
            showRecordeBroadcastView();
        }
    }

    private void showLiveBroadcastView() {
        hideRecordeBroadcastView();
        showRequestSpeak();
        showMessageView();
        showMoreSettingView();
    }

    private void hideLiveBroadcastView() {
        hideRequestSpeak();
        hideMessageView();
        hideMoreSettingView();
    }

    private void showLeftToolView() {
        llLeftTool.setVisibility(View.VISIBLE);
    }

    private void hideLeftToolView() {
        llLeftTool.setVisibility(View.GONE);
    }

    private void showRightToolView() {
        llRightTool.setVisibility(View.VISIBLE);
    }

    private void hideRightToolView() {
        llRightTool.setVisibility(View.GONE);
    }

    private void showMoreSettingView() {
        ibMore.setVisibility(View.VISIBLE);
    }

    private void hideMoreSettingView() {
        ibMore.setVisibility(View.GONE);
    }

    /**
     * 顯示進度條
     */
    private void showSeekBar() {
        viewPlayVideoSeekbar.setVisibility(View.VISIBLE);
    }

    /**
     * 隱藏進度條
     */
    private void hideSeekBar() {
        viewPlayVideoSeekbar.setVisibility(View.INVISIBLE);
    }

    /**
     * 顯示播放按鈕
     */
    private void showPlayIcon() {
        ivPlay.setVisibility(View.VISIBLE);
    }

    /**
     * 隱藏播放按鈕
     */
    private void hidePlayIcon() {
        ivPlay.setVisibility(View.GONE);
    }

    private void showRecordeBroadcastView() {
        hideLiveBroadcastView();
        showSeekBar();
        showPlayIcon();
        showMessageView();
        showMoreSettingView();
    }

    private void hideRecordeBroadcastView() {
        hideSeekBar();
        hidePlayIcon();
        hideMessageView();
        hideMoreSettingView();
    }

    /**
     * 通过PlayInfo接口信息，处理PlayURL地址
     *
     * @param streamOption 视频清晰度选项
     */
    private void handlePlayUrl(int streamOption) {
        if (mPlayUrl.length() != 0) mPlayUrl.delete(0, mPlayUrl.length());
        if (isLiveBroadcast) {
            mPlayUrl.append(cdnRtmpList.get(mCurrentCdnLineIdx).url);
            if (cdnRtmpList.get(mCurrentCdnLineIdx).streamList != null) {
                mPlayUrl.append("/").append(cdnRtmpList.get(mCurrentCdnLineIdx).streamList.get(streamOption).stream);
            }
            mPlayUrl.append("?")
                    .append(URL_PARAM_KEY_TOKEN).append("=").append(mPlayInfoResult.user.token)
                    .append("&")
                    .append(URL_PARAM_KEY_PLANID).append("=").append(mPlayInfoResult.plan.planId);
        } else {
            mPlayUrl.append(cdnHlsList.get(mCurrentCdnLineIdx).url);
            if (cdnHlsList.get(mCurrentCdnLineIdx).detail != null) {
                mPlayUrl.append("/").append(cdnHlsList.get(mCurrentCdnLineIdx).detail.get(streamOption).stream);
            }
            mPlayUrl.append("?")
//                    .append(URL_PARAM_KEY_TOKEN).append("=").append(mPlayInfoResult.user.token)
//                    .append("&")
//                    .append(URL_PARAM_KEY_PLANID).append("=").append(mPlayInfoResult.plan.planId)
//                    .append("&")
//                    .append(URL_PARAM_KEY_UPDATETIME).append("=").append(cdnHlsList.get(mCurrentCdnLineIdx).updatetime);
                    .append(cdnHlsList.get(mCurrentCdnLineIdx).detail.get(streamOption).keyName).append("=").append(cdnHlsList.get(mCurrentCdnLineIdx).detail.get(streamOption).keyValue);
        }
        TLog.analytics(PLAYER_TAG, "mPlayUrl = " + mPlayUrl);
    }

    /**
     * 通过PlayInfo接口信息，处理PlayURL地址
     *
     * @param lineIdx 视频线路选项
     */
    private void handleLinePlayUrl(int lineIdx) {
        if (mPlayUrl.length() != 0) mPlayUrl.delete(0, mPlayUrl.length());
        if (isLiveBroadcast) {
            mPlayUrl.append(cdnRtmpList.get(lineIdx).url);
            if (cdnRtmpList.get(lineIdx).streamList != null) {
                mPlayUrl.append("/").append(cdnRtmpList.get(lineIdx).streamList.get(mCurrentDefinitionIdx).stream);
            }
            mPlayUrl.append("?")
                    .append(URL_PARAM_KEY_TOKEN).append("=").append(mPlayInfoResult.user.token)
                    .append("&")
                    .append(URL_PARAM_KEY_PLANID).append("=").append(mPlayInfoResult.plan.planId);
        } else {
            mPlayUrl.append(cdnHlsList.get(lineIdx).url);
            if (cdnHlsList.get(lineIdx).detail != null) {
                mPlayUrl.append("/").append(cdnHlsList.get(lineIdx).detail.get(mCurrentDefinitionIdx).stream);
            }
            mPlayUrl.append("?")
                    .append(cdnHlsList.get(lineIdx).detail.get(mCurrentDefinitionIdx).keyName).append("=").append(cdnHlsList.get(lineIdx).detail.get(mCurrentDefinitionIdx).keyValue);
        }
        TLog.analytics(PLAYER_TAG, "mPlayUrl = " + mPlayUrl);
    }

    /**
     * 处理SayHiPubUrl
     */
    private void handleSayHiPubUrl() {
        if (mSayHiPubUrl.length() != 0) mSayHiPubUrl.delete(0, mSayHiPubUrl.length());
        mSayHiPubUrl.append(mPlayInfoResult.publishChat.url).append("/").append(mPlayInfoResult.publishChat.stream);
        mSayHiPubUrl.append("?")
                .append(URL_PARAM_KEY_TOKEN).append("=").append(mPlayInfoResult.user.token)
                .append("&")
                .append(URL_PARAM_KEY_PLANID).append("=").append(mPlayInfoResult.plan.planId);
        TLog.analytics(SAYHI_TAG, "mSayHiPubUrl = " + mSayHiPubUrl);
    }

    /**
     * 处理SayHiPlayUrl
     */
    private void handleSayHiPlayUrl() {
        if (mSayHiPlayUrl.length() != 0) mSayHiPlayUrl.delete(0, mSayHiPlayUrl.length());
        mSayHiPlayUrl.append(mPlayInfoResult.chat.url).append("/").append(mPlayInfoResult.chat.stream);
        mSayHiPlayUrl.append("?")
                .append(URL_PARAM_KEY_TOKEN).append("=").append(mPlayInfoResult.user.token)
                .append("&")
                .append(URL_PARAM_KEY_PLANID).append("=").append(mPlayInfoResult.plan.planId);
        TLog.analytics(SAYHI_TAG, "mSayHiPlayUrl = " + mSayHiPlayUrl);
    }

    /**
     * 处理视频清晰度选项
     */
    private void handleShowPlayStreamOption() {
        try {
            mStreamList = isLiveBroadcast ? cdnRtmpList.get(mCurrentCdnLineIdx).streamList : cdnHlsList.get(mCurrentCdnLineIdx).detail;
            setDefinitionShow(mStreamList.get(0).name);
        } catch (Exception e) {
            TLog.error(PLAYER_TAG, e.getLocalizedMessage());
        }
    }

    /**
     * 设置 播放视频界面的错误状态
     */
    private void setPlayVideoErrorStatus(String playVideoErrorStatus) {
        switch (playVideoErrorStatus) {
            case PlayPlanInfoResult.ErrorEntity.ERROR_CODE_NO_LOGIN: // 未登录
                showPlayVideoErrorView("未登录", UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_play_video_status_no_find_course"));
                break;
            case PlayPlanInfoResult.ErrorEntity.ERROR_CODE_NO_APPLY: // 未报名
                showPlayVideoErrorView("未报名", UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_play_video_status_no_find_course"));
                break;
            case PlayPlanInfoResult.ErrorEntity.ERROR_CODE_NO_START: // 未开课
//                mPlayInfoResult.plan.startTime // 开课时间
//                mPlayInfoResult.user.serverTime // 服务器当前时间
                long startTime = DateTimeUtil.getDateLongByPattern(mPlayInfoResult.plan.startTime, DateTimeUtil.dateTimeFormatter);
                mWaitCourseMillisInFuture = startTime - mPlayInfoResult.user.serverTime * 1000;
//                mWaitCourseMillisInFuture = startTime - DateTimeUtil.getDateTime().getTime();
                long h24Milliseconds = 24 * 60 * 60 * 1000;
                if (mWaitCourseMillisInFuture > 0 && mWaitCourseMillisInFuture < h24Milliseconds) { // 当天课程
                    mWaitCourseTimer = new CountDownTimer(mWaitCourseMillisInFuture, mWaitCourseCountDownInterval) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                            updateWaitCourseTime(millisUntilFinished);
                        }

                        @Override
                        public void onFinish() {
                            sendRequestPlayInfoData();
                        }
                    };
                    mWaitCourseTimer.start();
                } else { // 过期的课程 || 一天后开课的课程
                    showPlayVideoErrorView(String.format(getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_play_video_status_waiting_course_3")), DateTimeUtil.getDateStringByPattern(startTime, DateTimeUtil.dateTimeFormatterNoSecond)), UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_play_video_status_waiting_course"));
                }
                break;
            case PlayPlanInfoResult.ErrorEntity.ERROR_CODE_ERROR_CLASS: // 已经报名本课程其它班级
                showPlayVideoErrorView(getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_play_video_status_error_class")),
                        UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_play_video_status_no_find_course"));
                break;
            case PlayPlanInfoResult.ErrorEntity.ERROR_CODE_CONVERSION_COURSE: // 转码中
                showPlayVideoErrorView(getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_play_video_status_conversion_course")),
                        UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_play_video_status_conversion_course"));
                break;
            case PlayPlanInfoResult.ErrorEntity.ERROR_CODE_HIDE_COURSE: // 隐藏课
                showPlayVideoErrorView(getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_play_video_status_hide_course")),
                        UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_play_video_status_hide_course"));
                break;
            case PlayPlanInfoResult.ErrorEntity.ERROR_CODE_NO_FIND_COURSE: // 未找到的课
                showPlayVideoErrorView(getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_play_video_status_no_find_course")),
                        UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_play_video_status_no_find_course"));
                break;
        }
    }

    /**
     * 显示播放器错误界面
     *
     * @param text
     * @param drwResId
     */
    private void showPlayVideoErrorView(@NonNull String text, @NonNull int drwResId) {
        showController();
        view_play_video_error.setVisibility(View.VISIBLE);
        tv_play_video_error_status.setText(text);
        iv_play_video_error_status.setImageResource(drwResId);
    }

    /**
     * 隐藏播放器错误界面
     */
    private void hidePlayVideoErrorView() {
        view_play_video_error.setVisibility(View.GONE);
    }

    /**
     * 显示第一次加载状态
     */
    private void showFirstLoadingView() {
        finishFloatLayerFragment();
        hideController();
        hideLoadingView();
        hideLoadingFailedView();
        hidePlayFinishedView();
        hidePlayVideoErrorView();
        hideTipMessage1();
        hideTipMessage2();
        viewFirstLoading.setVisibility(View.VISIBLE);
        // 显示章节名
        if ((null == sectionName) || (sectionName.isEmpty())) {
            tvFirstLoaingContent.setText(getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_loading")));
        } else {
            tvFirstLoaingContent.setText(getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_video_first_loading")) + " " + sectionName);
        }

        // 动画开始
        animationDrawable = (AnimationDrawable) ivFirstLoading.getDrawable();
        animationDrawable.start();
    }

    /**
     * 隐藏第一次加载状态
     */
    private void hideFirstLoadingView() {
        viewFirstLoading.setVisibility(View.GONE);

        // 动画结束
        animationDrawable = (AnimationDrawable) ivFirstLoading.getDrawable();
        animationDrawable.stop();
    }

    /**
     * 显示加载状态
     */
    private void showLoadingView() {
        hideLoadingFailedView();
        hidePlayFinishedView();
        hidePlayVideoErrorView();
        viewLoading.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏加载状态
     */
    private void hideLoadingView() {
        viewLoading.setVisibility(View.GONE);
    }

    /**
     * 显示加载失败状态
     */
    private void showLoadingFailedView() {
        showController();
        hideLoadingView();
//        hideRecordeBroadcastView();
//        hideLiveBroadcastView();
        viewLoadingFailed.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏加载失败状态
     */
    private void hideLoadingFailedView() {
        viewLoadingFailed.setVisibility(View.GONE);
    }

    /**
     * 显示下课状态
     */
    private void showPlayFinishedView() {
        if (isLiveBroadcast) { // 直播
            if (isApplyCourse()) { //  已报名
                closeSpeakDialog();
                llFinishLiveBroadcastTip.setVisibility(View.VISIBLE);
                tvRank.setText(getMyRankNum() + "");
                tvGood.setText(getMyGoodNum() + "");
            } else {
                llFinishLiveBroadcastTip.setVisibility(View.GONE);
            }
            tvFinishReplay.setVisibility(View.GONE);
            hideLiveBroadcastView();
        } else {
            llFinishLiveBroadcastTip.setVisibility(View.GONE);
            tvFinishReplay.setVisibility(View.VISIBLE);
            hideRecordeBroadcastView();
            showMessageView();
        }
        viewFinishClass.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏下课状态
     */
    private void hidePlayFinishedView() {
        viewFinishClass.setVisibility(View.GONE);
    }

    /**
     * 播放前的处理
     */
    private void handlePlayOnPrepared() {
        hideFirstLoadingView();
        showGroupChat();
        handlePlayerType(isLiveBroadcast);
        if (!isApplyCourse() && (getPlayPermission() == PLAY_PERMISSION_FREE || getPlayPermission() == PLAY_PERMISSION_TRY_SEE)) { // 未报名 && (可试看 || 免费看)
            showTipMessage2(getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_tip_try_see_course")));
        } else if (isApplyOtherClass()) { // 报名了其他班级
            showTipMessage2("你已报名了该课程的其他班级，请去报名过的班级学习");
        }
        mPlayerState = PLAYER_STATE_PLAYING;
        if (!isLiveBroadcast) {
            mDuration = mVideoView.getDuration();
            tvVideoDuration.setText("/" + StringUtil.stringForTime(mDuration));
            sendUpdateProgressMsg(0);
        }
        updatePauseOrPlayUI();
    }

    /**
     * 处理视频的播放和暂停
     */
    private void handlePauseOrStart() {
        switch (mPlayerState) {
            case PLAYER_STATE_PLAYING: // 暂停操作
                mUserOperatePlayerState = PLAYER_STATE_PAUSED;
                handleVideoPause();
                break;
            case PLAYER_STATE_PAUSED: // 播放操作
                mUserOperatePlayerState = PLAYER_STATE_PLAYING;
                handleVideoPlay();
                break;
        }
    }

    /**
     * 暂停视频
     */
    private void handleVideoPause() {
        mVideoView.pause();
        mPlayerState = PLAYER_STATE_PAUSED;
        removeUpdateProgressMsg();
        updatePauseOrPlayUI();
    }

    /**
     * 播放视频
     */
    private void handleVideoPlay() {
        if (mUserOperatePlayerState == PLAYER_STATE_PLAYING && mPlayerState == PLAYER_STATE_PAUSED) {
            mVideoView.start();
            mPlayerState = PLAYER_STATE_PLAYING;
            sendUpdateProgressMsg(0);
            updatePauseOrPlayUI();
        }
    }

    /**
     * 重播视频
     */
    private void handleVideoRePlay() {
        mVideoView.release(true);
        mVideoView.resume();
        mVideoView.start();
        mPlayerState = PLAYER_STATE_IDLE;
        showFirstLoadingView();
    }

    /**
     * 更新播放按键状态
     */
    private void updatePauseOrPlayUI() {
        switch (mPlayerState) {
            case PLAYER_STATE_PLAYING:
                ivPlay.setImageResource(UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_video_play_suspend"));
                ivPlay.setContentDescription("pause");
                break;
            case PLAYER_STATE_PAUSED:
            case PLAYER_STATE_COMPLETED:
                ivPlay.setImageResource(UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_video_play_play"));
                ivPlay.setContentDescription("play");
                break;
        }
    }

    /**
     * 更新播放进度
     */
    private void updateProgress() {
        if (mDuration == 0) {
            return;
        }
        mPosition = mVideoView.getCurrentPosition();
        tvVideoCurrentTime.setText(StringUtil.stringForTime(mPosition));
        int pos = (int) (Float.valueOf(mPosition) / Float.valueOf(mDuration) * MAX_PROGRESS_VALUE);
//        TLog.analytics(PLAYER_TAG, "update progress progress = " + pos);
//        TLog.analytics(PLAYER_TAG, "update progress mPosition = " + mPosition);
//        TLog.analytics(PLAYER_TAG, "update progress mDuration = " + mDuration);
        seekbarVideo.setProgress(pos);
        if (pos < MAX_PROGRESS_VALUE) {
            sendUpdateProgressMsg(1000);
        }
    }

    /**
     * 更新播放进度的消息
     *
     * @param delayMillis
     */
    private void sendUpdateProgressMsg(int delayMillis) {
        removeUpdateProgressMsg();
        mHandlerMsg.sendEmptyMessageDelayed(MSG_KEY_UPDATE_PROGRESS, delayMillis);
    }

    /**
     * 移除播放进度的消息
     */
    private void removeUpdateProgressMsg() {
        mHandlerMsg.removeMessages(MSG_KEY_UPDATE_PROGRESS);
    }

    /**
     * 控制视频清晰度选项的显示和隐藏
     */
    public void handleDefinitionOptionStatus() {
        if (mPlayerState == PLAYER_STATE_COMPLETED) {
            return;
        }

        showLoadingView();

        // 找到原画和标清对应的序号
        int index = 0;
        int srcIdx = 0;
        int edIdx = 0;
        for (PlayPlanInfoResult.StreamEntity sm : mStreamList) {
            if (sm.name.equalsIgnoreCase("原画") || sm.name.equalsIgnoreCase("高清")) {
                srcIdx = index;
            } else {
                edIdx = index;
            }

            index++;
        }
        if (isSrcDefinition) { // 如果是原画
            switchVideoDefinition(edIdx);
        } else {
            switchVideoDefinition(srcIdx);
        }
        sendDelayedHideControllerMsg(CONTROLLER_HIDE_DELAY_MILLIS, CONTROLLER_HIDE_ANIMATION_DELAY_MILLIS);
        removeDelayedHideControllerMsg();
        sendUpdateProgressMsg(0);
    }

    /**
     * 切换视频清晰度
     *
     * @param streamOption
     */
    private void switchVideoDefinition(int streamOption) {
        showTipMessage1("正在切换清晰度...", false);
        // 更新控制器文字
        setDefinitionShow(mStreamList.get(streamOption).name);
        // 移除更新播放进度的消息
        removeUpdateProgressMsg();
        // 记录当前的播放进度
        mTagPosition = mVideoView.getCurrentPosition();
        TLog.analytics(PLAYER_TAG, " get position = " + mTagPosition);
        // 更新需要播放URL的地址
        handlePlayUrl(streamOption);
        // 重新播放
        mVideoView.setVideoPath(mPlayUrl.toString());
        isChangeVideoDefinition = true;
        mCurrentDefinitionIdx = streamOption;
    }

    /**
     * 切换视频线路
     *
     * @param lineIdx
     */
    public void switchVideoLine(int lineIdx) {
        showTipMessage1("正在切换线路...", false);
        if (mPlayerState == PLAYER_STATE_COMPLETED) {
            return;
        }

        // 显示正在Loading界面
        showLoadingView();

        // 移除更新播放进度的消息
        removeUpdateProgressMsg();
        // 记录当前的播放进度
        mTagPosition = mVideoView.getCurrentPosition();
        TLog.analytics(PLAYER_TAG, " get position = " + mTagPosition);
        // 更新需要播放URL的地址
        handleLinePlayUrl(lineIdx);
        // 重新播放
        mVideoView.setVideoPath(mPlayUrl.toString());
        isChangeVideoLine = true;
        mCurrentCdnLineIdx = lineIdx;

        // 需要通知刷新分辨率显示

        // 发出消息
        removeDelayedHideControllerMsg();
        sendUpdateProgressMsg(0);
    }

    /**
     * 通过planId，切换视频
     *
     * @param planId
     */
    public void switchVideo(String planId) {
        this.planId = planId;
//        isSwitchVideo = true;
        handleVideoPause();
        handlePlayVideo();
    }

    private boolean isLogin() {
        return true;
    }

    /**
     * 获取播放权限
     *
     * @return
     */
    public int getPlayPermission() {
        try {
            if (isLiveBroadcast) {
                return mPlayInfoResult.plan.livePublicType;
            } else {
                return mPlayInfoResult.plan.videoPublicType;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 设置控制器是否可用
     *
     * @param isEnable
     */
    private void setControllerIsEnable(boolean isEnable) {
        // 直播View || 录播View
        ibMore.setClickable(isEnable);
        ivMessage.setClickable(isEnable);
        // 录播View
        seekbarVideo.setEnabled(isEnable);
        ivPlay.setClickable(isEnable);
    }

    /**
     * 设定分辨率显示
     */
    private void setDefinitionShow(String name) {
        if (name.equalsIgnoreCase("原画") || name.equalsIgnoreCase("高清")) {
            isSrcDefinition = true;
        } else {
            isSrcDefinition = false;
        }
    }

    /**
     * 试看操作
     */
    private void handleTrySee() {
        mPosition = mVideoView.getCurrentPosition();
        if (Integer.parseInt(mPlayInfoResult.plan.videoTrialTime) * 1000 - mPosition <= 0) {
            handleVideoPause();

            ToastUtil.showToast(this, "请报名该课程！");
        }
    }

    /**
     * 发送播放日志的请求
     */
    private void sendPostPlayLog() {
        TLog.analytics(PLAYER_LOG_TAG, "sendPostPlayLog");
        mPlayVideoLogParams.lct = (int) (System.currentTimeMillis() - mPlayVideoLogParamsStartTime);
        mPlayVideoLogParams.ct = mVideoView.getCurrentPosition();
        if (mBackPressed || mPlayerState == PLAYER_STATE_COMPLETED) return;
        TLog.analytics(PLAYER_LOG_TAG, mPlayVideoLogParams.toJson());
        GN100Api.playLog(this, mPlayInfoResult.playLog, mPlayVideoLogParams, mHandlerPlayLog);
        if (mVideoView.getCurrentPosition() < mVideoView.getDuration()) {
            mHandlerMsg.sendEmptyMessageDelayed(MSG_KEY_UPLOAD_PLAY_LOG, 60 * 1000);
        } else {
            mHandlerMsg.removeMessages(MSG_KEY_UPLOAD_PLAY_LOG);
        }
        if (mPlayVideoLogParams.lct != 0) {
            mPlayVideoLogParams.lgt = (int) (System.currentTimeMillis() - mPlayVideoLogParamsStartTime);
            mPlayVideoLogParams.lct = (int) (System.currentTimeMillis() + (60 * 1000) - mPlayVideoLogParamsStartTime);
        }
    }

    private void showLockView(boolean isShow) {
        mHandlerMsg.removeMessages(MSG_KEY_HIDE_LOCK);
        if (isShow) {
            if (View.VISIBLE != ivLock.getVisibility()) {
                ivLock.setVisibility(View.VISIBLE);
                CommonUtil.translateAnimationLeft(true, ivLock);
            }
        } else {
            if (View.VISIBLE == ivLock.getVisibility()) {
                CommonUtil.translateAnimationLeft(false, ivLock);
                ivLock.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 发送举手发言的请求
     */
    private void sendRequestRaiseHand() {
        myWebSocketManager.sendMessage(SIGNAL, "ask", RASE_HAND_OR_CANCEL, 0);
    }

    /**
     * 发送取消发言的请求
     */
    private void sendRequestCancelRaiseHand() {
        setSpeakStatus(MIC_STATE_CANCEL_RAISE_HEAD);
        myWebSocketManager.sendMessage(SIGNAL, "cancel", RASE_HAND_OR_CANCEL, 0);
    }

    /**
     * 设置发言状态
     *
     * @param speakStauts
     */
    private void setSpeakStatus(int speakStauts) {
        switch (speakStauts) {
            case MIC_STATE_CANCEL_RAISE_HEAD:
                mMicState = MIC_STATE_CANCEL_RAISE_HEAD;
                break;
            case MIC_STATE_HIDE: // 隐藏状态
                mMicState = MIC_STATE_HIDE;
                rlSpeak.setVisibility(View.GONE);
            case MIC_STATE_IDLE: // 默认状态
                mMicState = MIC_STATE_IDLE;
                rlSpeak.setVisibility(View.VISIBLE);
                ivSpeakNormal.setVisibility(View.VISIBLE);
                ivSpeaking.setVisibility(View.GONE);
                tvSpeaking.setVisibility(View.GONE);

                mLeftToolSpeakRequestTimer.cancel();
                break;
            case MIC_STATE_RAISE_HAND: // 等待状态
                mMicState = MIC_STATE_RAISE_HAND;
                rlSpeak.setVisibility(View.VISIBLE);
                ivSpeakNormal.setVisibility(View.GONE);
                ivSpeaking.setVisibility(View.VISIBLE);
                tvSpeaking.setVisibility(View.VISIBLE);
                tvSpeaking.setText(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_play_video_speak_request"));

                sendRequestRaiseHand();
                mLeftToolSpeakRequestTimer.start();
                break;
            case MIC_STATE_SPEAKING: // 发言中
                mMicState = MIC_STATE_SPEAKING;
                rlSpeak.setVisibility(View.VISIBLE);
                ivSpeakNormal.setVisibility(View.VISIBLE);
                ivSpeaking.setVisibility(View.GONE);
                tvSpeaking.setVisibility(View.GONE);

                mLeftToolSpeakRequestTimer.cancel();
                break;
        }
    }

    /**
     * 发言的Dialog
     */
    private void showToSpeakDialog() {
        if (!mPermissionRecordAudio) {
            ToastUtil.showToast(this, "需要录音权限，才能发言，请去权限管理开启录音权限！");
            return;
        }
        if (mPlayInfoResult.publishChat == null) {
            ToastUtil.showToast(this, getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_tip_no_speak")));
            return;
        }
        setSpeakStatus(MIC_STATE_SPEAKING);

        openSayHiPub();

        if (mSpeakDialog == null) {
            mSpeakDialog = new AlertDialog.Builder(PlayVideoActivity.this,
                    UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_STYLE, "yunke_theme_transparent")).create();
        }
        mSpeakDialog.show();
        View contentView = View.inflate(mContext,
                UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_LAYOUT, "yunke_view_to_speak_dialog"), null);
        mSpeakDialog.setContentView(contentView);
        mSpeakDialog.setCancelable(false);
        mSpeakDialog.setCanceledOnTouchOutside(false);
        mSpeakDialog.getWindow().setLayout((int) TDevice.dpToPixel(this, 314), (int) TDevice.dpToPixel(this, 264.5f));

        final ImageView imgSpeakMic = (ImageView) contentView.findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_speak_mic"));
        final ImageView ivSpeakClose = (ImageView) contentView.findViewById(UIHelper.getResIdByName(this, UIHelper.TYPE_ID, "iv_speak_close"));
        // 开启发言动画
        AnimationDrawable imgSpeakMicLeftAnimation = (AnimationDrawable) imgSpeakMic.getDrawable();
        imgSpeakMicLeftAnimation.start();

        ivSpeakClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogUtil.showConfirmDialog(mContext, null, "确认要关闭发言？", getString(UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_STRING, "yunke_make_sure")), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        closeSpeakDialog();
                        openSayHiPlayer();
                        MyWebSocketManager.getInstance(PlayVideoActivity.this).sendMessage(SIGNAL, "refuse", 1004, 0);
                    }
                });
            }
        });
    }

    /**
     * 关闭发言的Dialog
     */
    private void closeSpeakDialog() {
        if (mSpeakDialog != null) {
            mSpeakDialog.dismiss();
            mSpeakDialog = null;
        }
        closeSayHiPub();
        setSpeakStatus(MIC_STATE_IDLE);
    }

    /**
     * 显示答题卡按钮
     */
    private void showAnswerCard() {
        ivAnswerCard.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏答题卡按钮
     */
    private void hideAnswerCard() {
        ivAnswerCard.setVisibility(View.GONE);
    }

    /**
     * 显示请求发言按钮
     */
    private void showRequestSpeak() {
        rlSpeak.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏请求发言按钮
     */
    private void hideRequestSpeak() {
        rlSpeak.setVisibility(View.GONE);
    }

    /**
     * 显示消息按钮
     */
    private void showMessageView() {
        ivMessage.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏消息
     */
    private void hideMessageView() {
        ivMessage.setVisibility(View.GONE);
    }

    /**
     * 显示其他发言者,正在发言状态
     *
     * @param imageUrl
     * @param speakerName
     */
    private void showOtherSpeaker(String imageUrl, String speakerName) {
        TLog.analytics(SAYHI_TAG, "他人发言  imageUrl = " + imageUrl + " , speakerName = " + speakerName);
        rlSpeakOther.setVisibility(View.VISIBLE);
        if (!android.text.TextUtils.isEmpty(imageUrl))
            GN100Image.updateCycleAvatarImageView(this, ApiHttpClient.WS_IMG_URL + imageUrl, ivSpeakOther);
        if (!android.text.TextUtils.isEmpty(speakerName))
            tvSpeakOther.setText(speakerName);
    }

    /**
     * 隐藏其他发言者，正在发言状态
     */
    private void hideOtherSpeaker() {
        rlSpeakOther.setVisibility(View.GONE);
    }

    /**
     * 显示 灰色提示框
     *
     * @param msg
     * @param isShowDelete
     */
    public void showTipMessage1(String msg, boolean isShowDelete) {
        rlTipMessage1.setVisibility(View.VISIBLE);
        if (isShowDelete) {
            ivTipDeleteMessage1.setVisibility(View.VISIBLE);
            lineMessage1.setVisibility(View.VISIBLE);
        } else {
            ivTipDeleteMessage1.setVisibility(View.GONE);
            lineMessage1.setVisibility(View.GONE);
            mHandlerMsg.removeMessages(MSG_KEY_HIDE_TIP_MSG_1);
            mHandlerMsg.sendEmptyMessageDelayed(MSG_KEY_HIDE_TIP_MSG_1, 10 * 1000);
        }
        tvTipMessage1.setText(Html.fromHtml(msg));
    }

    /**
     * 隐藏 灰色提示框
     */
    public void hideTipMessage1() {
        rlTipMessage1.setVisibility(View.GONE);
        tvTipMessage1.setText("");
    }

    /**
     * 显示 蓝色提示框
     *
     * @param msg
     */
    public void showTipMessage2(String msg) {
        tvTipMessage2.setVisibility(View.VISIBLE);
        tvTipMessage2.setText(Html.fromHtml(msg));
//        mHandlerMsg.removeMessages(MSG_KEY_HIDE_TIP_MSG_2);
//        mHandlerMsg.sendEmptyMessageDelayed(MSG_KEY_HIDE_TIP_MSG_2, 10 * 1000);
    }

    /**
     * 隐藏 蓝色提示框
     */
    public void hideTipMessage2() {
        tvTipMessage2.setVisibility(View.GONE);
        tvTipMessage2.setText("");
    }

    /**
     * 更新等待上课时间
     */
    private void updateWaitCourseTime(long waitCourseTime) {
        long hour = waitCourseTime / 1000 / 60 / 60;
        long minute = waitCourseTime / 1000 / 60 % 60;
//        long second = waitCourseTime / 1000 % 60;
        if (hour == 0) {
            showPlayVideoErrorView(String.format(
                    getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_play_video_status_waiting_course_2")), minute),
                    UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_play_video_status_waiting_course"));
        } else {
            showPlayVideoErrorView(String.format(getString(UIHelper.getResIdByName(this, UIHelper.TYPE_STRING, "yunke_play_video_status_waiting_course_1")), hour, minute),
                    UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_play_video_status_waiting_course"));
        }
    }

    /**
     * 信号处理
     *
     * @param signalResultEntity
     * @param signalOrGood       signgal / good
     * @param type               1006
     * @param haveC              根据C判断信号
     * @param C
     * @param isOthersSignal     对他人的信号
     * @return
     */
    private boolean isTrueSignal(WebSocketEnty.ResultEntity signalResultEntity, String signalOrGood, int type, boolean haveC, String C, boolean isOthersSignal) {

        if (signalResultEntity == null) {
            return false;
        }

        if (signalResultEntity.getMt().equals(signalOrGood) && signalResultEntity.getCt() == type) {

            if (isOthersSignal) {//对他人的信号
                return signalResultEntity.getUt() != uid && (haveC ? signalResultEntity.getC().equals(C) : true);
            } else if (signalResultEntity.getUt() == 0 && (haveC ? signalResultEntity.getC().equals(C) : true)) {//对所有人
                return true;
            } else if (signalResultEntity.getUt() == uid && (haveC ? signalResultEntity.getC().equals(C) : true)) {//对自己
                if (signalResultEntity.getUft().equals("")) {
                    return true;
                } else if (signalResultEntity.getUft().equals(token.substring(0, 5))) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是已报名
     *
     * @return
     */
    public boolean isApplyCourse() {
        if (null == mPlayInfoResult) { // 异常状态
            return false;
        }
        if (null != mPlayInfoResult.plan && PlayPlanInfoResult.PlanEntity.APPLY_COUTSE.equals(mPlayInfoResult.plan.apply)) {
            return true;
        }
        return false;
    }

    /**
     * 判断是否报名其他班级
     *
     * @return
     */
    public boolean isApplyOtherClass() {
        if (null == mPlayInfoResult) { // 异常状态
            return false;
        }
        if (null != mPlayInfoResult.plan && PlayPlanInfoResult.PlanEntity.UNAPPLY_COUTSE.equals(mPlayInfoResult.plan.apply) && PlayPlanInfoResult.PlanEntity.APPLY_COUTSE.equals(mPlayInfoResult.plan.applyCourse)) {
            return true;
        }
        return false;
    }

    /**
     * 设定是否已经评价
     *
     * @return
     */
    public void setCommentStatus(boolean status) {
        isCommented = status;
    }

    /**
     * 根据SeekBar位置来显示预览图
     *
     * @return
     */
    private void moveThumbsLL(SeekBar seekBar, int progress) {
        Rect seekBarRect = new Rect();
        seekBar.getGlobalVisibleRect(seekBarRect);

        int ax = (seekBarRect.left + 30) + ((seekBarRect.width() - 60) * progress / MAX_PROGRESS_VALUE);
        int ay = seekBarRect.top;

        Rect thumbImgRect = new Rect();
        thumbsLL.getGlobalVisibleRect(thumbImgRect);
        int tx = ax - (thumbImgRect.width() / 2);
        int ty = ay - thumbImgRect.height() - 2;

        // 跟随移动
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) thumbsLL.getLayoutParams();
        lp.setMargins(tx, ty, 0, 0);
        thumbsLL.requestLayout();

        // 计算图片序号
        int total_second = (mVideoView.getDuration() / 1000);
        int img_idx = (total_second * progress / MAX_PROGRESS_VALUE) / mPlayInfoResult.thumbs.interval;

        // 如果显示没有改变，则直接返回
        if (img_idx == mThumbsCurrentImgIdx) {
            return;
        }

        // 记录当前显示的序号
        mThumbsCurrentImgIdx = img_idx;

        // 每张大图最多包含的小图数
        int simg_count = mPlayInfoResult.thumbs.cols * mPlayInfoResult.thumbs.rows;

        // 获取大图的名字
        String img_name = getThumbsImagPathByUrl(mPlayInfoResult.thumbs.data.get(img_idx / simg_count));

        // 如果文件不存在，则显示等待画面
        File file = new File(img_name);
        if (!file.exists()) {
            ivThumbs.setVisibility(View.GONE);
            pbThumbs.setVisibility(View.VISIBLE);
        } else {
            ivThumbs.setVisibility(View.VISIBLE);
            pbThumbs.setVisibility(View.GONE);

            // 计算图片位置
            int mod = img_idx % simg_count;
            int row = mod / mPlayInfoResult.thumbs.rows;
            int col = mod % mPlayInfoResult.thumbs.rows;

            // 设置显示图片
            Rect rect = new Rect();
            rect.left = mPlayInfoResult.thumbs.width * col;
            rect.top = mPlayInfoResult.thumbs.height * row;
            rect.right = rect.left + mPlayInfoResult.thumbs.width;
            rect.bottom = rect.top + mPlayInfoResult.thumbs.height;

            try {
                FileInputStream in = new FileInputStream(file);
                Bitmap bm = ImageUtils.getBitmapFromInputstreamAndReect(in, rect);
                ivThumbs.setImageBitmap(bm);
                in.close();
                in = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return;
    }

    /**
     * 下载预览图
     *
     * @return
     */
    private void downloadThumbs() {
        if (null == mPlayInfoResult.thumbs || (null == mPlayInfoResult.thumbs.data) || (0 == mPlayInfoResult.thumbs.data.size())) {
            return;
        }

        // 没有写SD卡的权限，则不执行
        if (Build.VERSION.SDK_INT >= 23) {
            int checkCallPhonePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (checkCallPhonePermission != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        // 如果已经开始下载了，就返回
        if (isThumbsDownload) {
            return;
        }
        isThumbsDownload = true;

        for (final String url : mPlayInfoResult.thumbs.data) {
            // 判断文件是否存在
            String file_path = getThumbsImagPathByUrl(url);
            File file = new File(file_path);
            if (file.exists()) {
                continue;
            }

            // 启动线程下载
            downloadThumbImage(url, file_path);
        }

        return;
    }

    protected void downloadThumbImage(String url, String file_path) {
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        if (!android.text.TextUtils.isEmpty(url) && (url.startsWith("http") || url.startsWith("https"))) {
            Uri uri = Uri.parse(url);
            DownloadManager.Request request = new DownloadManager.Request(uri);
            request.setShowRunningNotification(false);
            File file = new File(file_path);
            Uri dstUri = Uri.fromFile(file);
            request.setDestinationUri(dstUri);
            //不显示下载界面
            request.setVisibleInDownloadsUi(false);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);

            try {
                downloadManager.enqueue(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
//            ToastUtil.showToast("不规范的URL");
        }

    }

    /**
     * 根据URL地址生成下载图片路径
     *
     * @return
     */
    private String getThumbsImagPathByUrl(String url) {
        // 默认存放文件下载的路径
        String file_path = Environment
                .getExternalStorageDirectory()
                + File.separator
                + "Download"
                + File.separator
                + "GN100"
                + File.separator + "download";
        String path = file_path + File.separator + "thumbs";
//        String path = getCacheDir().getPath() + File.separator + "thumbs";
        if ((null == url) || (0 == url.length())) {
            return null;
        }

        File file = new File(path);
        if (!file.exists() && !file.isDirectory()) {
            //　如果没有就创建
            file.mkdirs();
        }

        path = path + File.separator + url.substring(url.lastIndexOf(",") + 1) + ".jpg";
        return path;
    }

    private String noticeBody;

    /**
     * websocket发送的信号
     */
    private void receiveSignalAndGood() {

        //点赞信息
        if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_GOOD_DATA, false, "", false)) {
            //经验值
            GetGoodDataBean mResult = new Gson().fromJson(signalResultEntity.getC().toString(), GetGoodDataBean.class);
            if ((mResult.getData().getFk_user() == uid)) {
                haveScore = true;
                if (mResult.getData().getUp_type() != 0) {
                    //升级
                    answerCardDialogManger.showUpgradeDialog(mResult, this);
                }
            }
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_NOTICE_BOARD, false, "", false)) {
            //公告
            noticeBody = signalResultEntity.getC();

            showNotice(noticeBody);
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_CALL, false, "", false)) {
            //答到
            answerCardDialogManger.daDao(planId, PlayVideoActivity.this);
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_AGREE_REFUSE, true, "agree", false)) {
            // 同意发言
            TLog.analytics(SAYHI_TAG, "同意自己发言");
            showToSpeakDialog();
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_AGREE_REFUSE, true, "refuse", false)) {
            // 拒绝发言
            TLog.analytics(SAYHI_TAG, "拒绝自己发言");
            if (signalResultEntity.getUf() == uid) { // 自己拒绝了老师
                ToastUtil.showToast(this, "拒绝了老师的发言请求");
                setSpeakStatus(MIC_STATE_IDLE);
            } else if (signalResultEntity.getUt() == uid) { // 老师拒绝了自己
                setSpeakStatus(MIC_STATE_IDLE);
                ToastUtil.showToast(this, "老师拒绝了你的发言请求");
            }
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_AGREE_REFUSE, true, "stop", false)) {
            // 结束发言
            TLog.analytics(SAYHI_TAG, "结束自己发言");
            closeSpeakDialog();
            openSayHiPlayer();
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_AGREE_REFUSE, true, "stop", true)) {
            //他人被结束发言
            TLog.analytics(SAYHI_TAG, "结束他人发言");
            closeSayHiPlayer();
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_ASK_CANCEL, true, "cancel", true)) {
            //遍历学生列表获取 某某的取消发言

            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_AGREE_REFUSE, true, "asking", false)) {
            // 自己被邀请发言
            TLog.analytics(SAYHI_TAG, "邀请自己发言");
            showToSpeakDialog();
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_AGREE_REFUSE, true, "asking", true)) {
            // 他人被邀请发言
            TLog.analytics(SAYHI_TAG, "邀请他人发言");
            closeSpeakDialog();
            openSayHiPlayer();
            showOtherSpeaker(signalResultEntity.getUt_t(), signalResultEntity.getUt_n());
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_ASEED_TO_COMMENT, true, "stop", false)) {
            // 下课 直播
            TLog.analytics(PLAYER_TAG, "直播-下课");
            mPlayerState = PLAYER_STATE_COMPLETED;
            showPlayFinishedView();
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_START_CLOSE, true, "start", false)) {
            // 上课 直播
            TLog.analytics(PLAYER_TAG, "直播-上课");
//            if (mPlayerState == PLAYER_STATE_ERROR) { // ERROR，等待上课状态
//                sendRequestPlayInfoData();
//            }
            try {
                sendRequestPlayInfoData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_CAMERA, true, "open", false)) {
            // 打开摄像头

            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_CAMERA, true, "close", false)) {
            //关闭摄像头摄像头

            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_TEACHER_QUESTION, false, "", false)) {
            //出题
            final Gson gson = new Gson();
            TeacherQuestionEnty mTeaQueEnty = gson.fromJson(signalResultEntity.getC(), TeacherQuestionEnty.class);
            if (mTeaQueEnty.getAnswer().size() == 2 && mTeaQueEnty.getPhraseType() == 2) {//互动问答只有A,B选项  //phraseType 1 快速出题 2 询问
                answerCardDialogManger.showHuDongWenDaDialog(mTeaQueEnty, planId, PlayVideoActivity.this);
            } else {
                answerCardDialogManger.onSendAnswerCard(mTeaQueEnty, PlayVideoActivity.this);
            }
            signalResultEntity = null;
        }
        //答案
        else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_TEACHER_ANSWER, false, "", false)) {
            answerCardDialogManger.onSendAnswer(signalResultEntity, PlayVideoActivity.this);
            signalResultEntity = null;
        }
        //收回答题卡
        else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_RETRIEVE_ANSWER_CARD, false, "", false)) {
            answerCardDialogManger.cancelAnswerCardDialog();
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_SINGLE_NOTALK, true, "forbid", false)) {
            if (signalResultEntity.getUt() == uid) {//对自己禁言
                if (null != mGroupChatFragment) {
                    mGroupChatFragment.setICanSpeak(false);
                }

                // 设置输入框状态
                if (null != mGroupChatPressFragment) {
                    mGroupChatPressFragment.setIForbidStatus(true);
                }
            } else {
                //某同学禁言

            }
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_SINGLE_NOTALK, true, "free", false)) {
            if (signalResultEntity.getUt() == uid) {
                //对自己解除禁言
                if (null != mGroupChatFragment) {
                    mGroupChatFragment.setICanSpeak(true);
                }

                // 设置输入框状态
                if (null != mGroupChatPressFragment) {
                    mGroupChatPressFragment.setIForbidStatus(false);
                }
            } else {
                //对某同学解除禁言

            }
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_PATTERN, true, "notalk", false)) {
            //全体禁言

            // 设置发言框状态
            if (null != mGroupChatFragment) {
                mGroupChatFragment.setAllCanSpeak(false);
            }

            // 设置输入框状态
            if (null != mGroupChatPressFragment) {
                mGroupChatPressFragment.setAllForbidStatus(true);
            }

            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_PATTERN, true, "normal", false)) {
            //解除禁言和仅老师课件
            if (null != mGroupChatFragment) {
                mGroupChatFragment.setGroupChatContentVisibal(true);
                mGroupChatFragment.setAllCanSpeak(true);
            }

            // 设置输入框状态
            if (null != mGroupChatPressFragment) {
                mGroupChatPressFragment.setAllForbidStatus(false);
            }

            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_PATTERN, true, "reply", false)) {
            //仅老师可见
            if (null != mGroupChatFragment) {
                mGroupChatFragment.setGroupChatContentVisibal(false);
            }
            signalResultEntity = null;
        } else if (isTrueSignal(signalResultEntity, SIGNAL, MyWebSocketManager.CT_DELETE_TEXT, false, "", false)) {
            //删除某条聊天记录
            if (null != mGroupChatFragment) {
                mGroupChatFragment.delOneChatRecordById(signalResultEntity.getC());
            }

            signalResultEntity = null;
        }
        //点赞
        else if (goodResultEntity != null) {
            if ((goodResultEntity.getUt() == uid || goodResultEntity.getUt() == 0) && goodResultEntity.getCt() == MyWebSocketManager.CT_GOOD) {
                if ((null != myWebSocketManager) && (myWebSocketManager.isFristGetConnData != 1)) {
                    if (haveScore) {
                        iv_good.setBackgroundResource(UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_good_bg_1"));
                    } else {
                        iv_good.setBackgroundResource(UIHelper.getResIdByName(PlayVideoActivity.this, UIHelper.TYPE_DRAWABLE, "yunke_good_bg"));
                    }
                    iv_good.setVisibility(View.VISIBLE);
                    haveScore = false;
                    mHandlerMsg.removeMessages(MSG_KEY_CLOSE_GOOD_POP);
                    if (translateAnimation == null) {
                        translateAnimation =
                                new TranslateAnimation(
                                        Animation.RELATIVE_TO_SELF, -1.5f,
                                        Animation.RELATIVE_TO_SELF, 0f,
                                        Animation.RELATIVE_TO_SELF, 0f,
                                        Animation.RELATIVE_TO_SELF, 0f);
                    }
                    translateAnimation.setDuration(500);
                    iv_good.startAnimation(translateAnimation);
                    mHandlerMsg.sendEmptyMessageDelayed(MSG_KEY_CLOSE_GOOD_POP, 1500);
                }
            }
            goodResultEntity = null;
        }
    }

    private void showNotice(String noticeBody) {
        if (!TextUtils.isEmpty(noticeBody)) {
            if (notice.getVisibility() != View.VISIBLE) {
                notice.setVisibility(View.VISIBLE);
            }
            answerCardDialogManger.showNoticeDialog(noticeBody, PlayVideoActivity.this);
        } else {
            if (notice.getVisibility() != View.GONE) {
                notice.setVisibility(View.GONE);
            }
        }
    }

    private boolean haveScore = false;
    private TranslateAnimation translateAnimation;
    private int myGoodNum = 0;

    /**
     * 点赞信息
     *
     * @param mResultEntity
     */
    private void getGoodsData(WebSocketEnty.ResultEntity mResultEntity) {
        if (mResultEntity.getMt().equals("good")) {

            goodResultEntity = mResultEntity;

            if (goodResultEntity.getUt() == uid) {
                myGoodNum = Integer.parseInt(mResultEntity.getC()) + myGoodNum;
            }

            if (goodsDataList.size() == 0) {
                goodsDataList.add(mResultEntity);
            } else {
                boolean flag = false;
                for (int j = 0; j < goodsDataList.size(); j++) {
                    if (goodsDataList.get(j).getUt() == mResultEntity.getUt()) {//存在 点赞加
                        goodsDataList.get(j).setC(Integer.parseInt(goodsDataList.get(j).getC()) + Integer.parseInt(mResultEntity.getC()) + "");
                        flag = true;
                    }
                }
                if ((flag == false)) {
                    goodsDataList.add(mResultEntity);
                }
            }

        }
    }

    /**
     * 获取自己的排名
     *
     * @return
     */
    private int getMyRankNum() {
        for (int i = 0; i < goodsDataList.size(); i++) {
            if (goodsDataList.get(i).getUt() == uid) {
                return i + 1;
            }
        }
        return 1;
    }

    /**
     * 获取点赞数
     *
     * @return
     */
    private int getMyGoodNum() {
        return myGoodNum;
    }

    public PlayPlanInfoResult getPlayPlanInfo() {
        return mPlayInfoResult;
    }

    private void initAsyncHttp() {
        // 初始化网络请求
        AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
        asyncHttpClient.setTimeout(DEFAULT_SOCKET_TIMEOUT);
        PersistentCookieStore myCookieStore = new PersistentCookieStore(this);
        asyncHttpClient.setCookieStore(myCookieStore);
        ApiHttpClient.setAsyncHttpClient(asyncHttpClient);
        SyncHttpClient syncHttpClient = new SyncHttpClient();
        syncHttpClient.setTimeout(DEFAULT_SOCKET_TIMEOUT);
        syncHttpClient.setCookieStore(myCookieStore);
        ApiHttpClient.setSyncHttpClient(syncHttpClient);
    }

}
