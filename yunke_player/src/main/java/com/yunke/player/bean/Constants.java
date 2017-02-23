package com.yunke.player.bean;

import android.os.Environment;

/**
 * 常量类
 */
public interface Constants {
    String SALT = "gn1002015";

    String INTENT_ACTION_USER_CHANGE = "com.gn100.action.USER_CHANGE";
    String INTENT_ACTION_LOGOUT = "com.gn100.action.LOGOUT"; // 退出登录的广播

    String APP_CONFIG_CURRENT_CITY = "user.currentCity";
    String APP_CONFIG_CURRENT_CITY_ID = "user.currentCityId";
    String INTENT_ACTION_CHOICE_CITY_CHANGE = "com.gn100.action.CHOICE_CITY_CHANGE"; // 选择城市改变的广播

    String INTENT_ACTION_HOMEWORK_FAIL_DOWNLOAD = "com.gn100.action.HOMEWORK_FAIL_DOWNLOAD"; // 作业下载失败的广播
    String INTENT_ACTION_HOMEWORK_SUCCESS_DOWNLOAD = "com.gn100.action.HOMEWORK_SUCCESS_DOWNLOAD"; // 下载成功的广播
    String HOMEWORK_PATH = "homeworkPath"; // 下载成功后存储路径的标识

    String INTENT_ACTION_FINISH_DOWNLOAD = "com.gn100.action.FINISH_DOWNLOAD";
    String INTENT_ACTION_REGISTER_SUCCESS = "com.gn100.action.REGISTER_SUCCESS";
    String INTENT_ACTION_REGISTERED_PHONE_NUMBER = "com.gn100.action.REGISTERED_PHONE_NUMBER";
    String INTENT_ACTION_RETRIEVE_PWD_SUCCESS = "com.gn100.action.RETRIEVE_PWD_SUCCESS"; // 找回密码成功的广播
    String INTENT_ACTION_CLASS_STUDENT_STATUS_CHANGE = "com.gn100.action.CLASS_STUDENT_STATUS_CHANGE"; // 上课学生上下线的广播
    String INTENT_ACTION_DOWNLOAD_VIDEO_CHANGE = "com.gn100.action.download_video_change"; // 下载视频数量变化的广播
    String APP_CONFIG_MOVE_INTERNET_DOWNLOAD_UPLOAD = "moveInternetDownloadUpload"; // 移动网络视频可下载或上传
    String APP_CONFIG_VIDEO_DEFINITION_PRIMARY = "videoDefinitionPrimary"; // 视频下载清晰度，是否是原画
    String INTENT_ACTION_EDIT_HEAD_PORTRAIT = "com.gn100.action.EDIT_HEAD_PORTRAIT"; // 用户头像改变的广播
    String PLAY_VIDEO_GUID_CLOSE = "PlayVideoGuidClose"; // 播放页关闭引导
    String PLAY_VIDEO_GROUP_CHAT_PRESS_CLOSE = "PlayVideoGroupChatPressClose"; // 播放页关闭群聊输入框

    String APP_CONFIG_UNIVERSAL_IMAGE_CACHE_PATH = "imageloader/Cache";
    int APP_CONFIG_UNIVERSAL_IMAGE_MEMORY_CACHE_SIZE = 2 * 1024 * 1024;
    int APP_CONFIG_UNIVERSAL_IMAGE_DISK_CACHE_SIZE = 50 * 1024 * 1024;

    String INTEREST_CLASSIFICATION_CACHE = "interest_classification_cache";//兴趣分类缓存key
    String INTEREST_SELECTED_SECOND_CACHE = "interest_selected_second_cache";//选择的兴趣缓存key
    String INTEREST_SELECTED_THRID_CACHE = "interest_selected_thrid_cache";//选择的兴趣缓存key
    String HOME_NAVIGATION_CACHE = "home_navigation_cache";//主页导航缓存

    String INTENT_ACTION_EDIT_NICK_KEY = "intent_action_edit_nick_key";
    String REAL_NAME_KEY = "real_name_key";
    String NICK_NAME_KEY = "nick_name_key";
    String ORDER_TYPE_KEY = "order_type_key"; //订单类型
    String ORDER_COURSE_ID_KEY = "order_course_id_key"; //课程Id
    String ORDER_NAME_KEY = "order_name_key";
    String ORDER_DESC_KEY = "order_desc_key";
    String ORDER_PRICE_KEY = "order_price_key";
    String ORDER_NUM_KEY = "order_num_key";
    String ORDER_TIME_KEY = "order_time_key";
    String ORDER_OUT_TRADE_ID_KEY = "order_out_trade_id_key";
    String ORDER_ID_KEY = "order_id_key"; // 订单ID
    String COURSE_ID_KEY = "course_id_key"; // 课程ID
    String CLASS_ID_KEY = "class_id_key"; // 班级ID

    String HOMEWORK_ID = "homework_id"; // 作业ID
    String CLASS_ID = "class_id"; // 班级id
    String MAP = "map";
    String LIST = "list";
    String REFRESH = "refresh";
    String STUDENT_RESPONSE_ACTION = "student_response_action";
    String LIST_STATUS_TRUE_ACTION = "list_true_action";
    String LIST_STATUS_FALSE_ACTION = "list_false_action";
    String LIST_STATUS = "list_true";
    String RESPONSE_STATUS_ACTION = "response_status_action";
    String COURSE_ID = "course_id"; // 课程id
    String DOWNLOAD_SUCCESS_FLAG = "download_success_flag"; // 学生列表信息下载成功
    ///////////////////  去筛选搜索  ///////////////////////
    String KEY_CLASSIFY_ID = "classify_id";
    String KEY_CLASSIFY_NAME = "classify_name";
    String KEY_CLASSIFY_GRADE = "classify_grade";

    ///////////////////  去筛选老师  ///////////////////////
    String KEY_FILETER_TEACHER_ID = "teacher_classify_id";
    String KEY_FILETER_TEACHER_NAME = "teacher_classify_name";

    //////////////////// 去提交订单///////////////////////
    String KEY_CLASS_ID = "key_class_id";
    String KEY_COURSE_ID = "key_course_id";

    //接收到websocket发送的广播
    String HAVE_NEW_WEBSOCKET_DATA = "have_new_websocket_data";
    //点赞时发送广播 刷新排名
    String HAVE_GOOD = "have_good";
    //评价成功发送广播
    String COMMENT_SUCCESS = "comment_success";
    String WEBSOCKET_ISCONNECTED = "websocket_isconnected";//WEBSOCKET是否连接
    String WEBSOCKET_IS_NOT_CONNECTED = "websocket_isconnected";//WEBSOCKET广播
    String IS_IN_GROUP_CHAT = "is_in_group_chat";//在聊天页面
    String CLOSE_EMOTION_CONTAINER = "close_emotion_container";//关闭表情键盘
    String GOOD_STUID = "good_stuid";//发送点赞广播，携带stuid
    String SELECTPAGE = "select_page";
    String APPLY_CANCEL = "apply_cancel";//报名于取消报名的广播
    String APP_CONFIG_HAS_NEW_VERSION = "app_config_has_new_version";
    String APP_CONFIG_MOVE_INTERNET_PLAY = "moveInternetPlay"; // 移动网络视频可播放

    String APP_VERSION_UPDATE_BYSELF = "app_config_version_update_internet_download"; // 下载安装包
    String USER_INFO = "user_info";
    String VERIFICATION_CODE_TYPE_REGISTER = "1";
    String VERIFICATION_CODE_LOGIN_AND_REGISTER = "4";
    String VERIFICATION_CODE_TYPE_RETRIEVE_PWD = "2";

    String EXTRA_KEY_PHONE_NUMBER = "EXTRA_KEY_PHONE_NUMBER";
    String PAY_SUCCESS = "pay_success";

    int VERIFICATION_CODE_TYPE_CHECK_BIND = 3; // 第三方绑定
    int PHONE_NUMBER_LENGTH = 11; // 手机号长度
    int NEW_USER_ID = 1;
    int OLD_USER_ID = 0;
    int CHOICE_PHONE_NUMBER_PREFIX_REQUEST_CODE = 1;
    String EXTRA_KEY_PHONE_PREFIX = "EXTRA_KEY_PHONE_PREFIX";
    String SETTING_PASS_WORD_TYPE = "SETTING_PASS_WORD_TYPE";
    String SMS_CODE = "SMS_CODE";
    String REGISTER_PHONE = "REGISTER_PHONE";
    String APP_CONFIG_VERSION_UPDATE_TIME = "versionUpdateTime";
    String SETTING_PASS_WORD_TITLE = "SETTING_PASS_WORD_TITLE";

    // 打包类型
    String YUN_KE = "yunke";
    String XIAO_VO = "player.sdk";

    // 课程状态
    String COURSE_TYPE_NO_START = "1"; // 未开始
    String COURSE_TYPE_ING = "2"; // 进行中
    String COURSE_TYPE_COMPLETE = "3"; // 完成

    // 用户针对课程的状态
    String COURSE_JOIN_TYPE_NO = "0"; // 未报名
    String COURSE_JOIN_TYPE_YES = "1"; // 已报名

    // 试看状态
    String TRY_SEE_TYPE_NO = "0"; // 不可试看
    String TRY_SEE_TYPE_YES = "1"; // 可试看

    // 笔记列表状态
    String PLAYER_NOTE_LIST_TYPE_LIVE = "2"; // 直播
    String PLAYER_NOTE_LIST_TYPE_RECORD = "3"; // 录播

    // Activity To Activity 通信
    int REQUEST_CODE_KEY_PLAYER_NOTE = 11;
    int REQUEST_CODE_KEY_PLAYER_NOTE_SAVE = 12;
    int REQUEST_CODE_KEY_PLAYER_NOTE_UPDATE = 13;

    String HOME_LIST_GUIDE_IS_SHOW = "HOME_LIST_GUIDE_IS_SHOW"; // 首页导航标记

    // 首頁學生List
    String HOME_COURSE_LIST_PLAY_NUMBER = "1";
    String HOME_COURSE_LIST_APPLY_NUMBER = "2";
    String HOME_COURSE_LIST_YU_NUMBER = "3";


    /////////////////// 网络环境 /////////////////////
    String WS_IMG_URL = "ws_img_url";//DEV
    String VIDEO_UPLOAD_URL = "video_upload_url";//DEV
    String VIDEO_DOWNLOAD_URL = "video_download_url";//DEV
    String API_NEED_PROTOCOL = "api_need_protocol";//DEV
    String NEED_HOST = "need_host";//DEV
    String WEBSOCKET_URL = "websocket_url";//DEV
    String OID = "oid";//"842";//DEV


    //存储用户id的SharedPreferences 名称
    String USER_TO_ID = "user_id";
    //对方id的key
    String TO_ID_KEY = "to_id_key";
    //页面跳转时的ToID/对方的id
    String TO_ID = "to_id";
    //对方头像
    String FROM_AVATAR = "from_avatar";
    //对方的name
    String TO_NAME = "to_name";

    String RUN_IN_APP = "app_run";
    //存储maxId的sp
    String MAXID = "maxId";
    String INTENT_ACTION_REFRESH_NEWS_REMAIN = "com.gn100.action.refresh_news_remain"; // MainActivity更新消息提醒的广播
    //保存消息通知的key
    String NOTIFY_NEWS_KEY = "notify_news_key";
    //保存声音通知的key
    String SOUND_KEY = "sound_key";
    //保存震动提醒的key
    String VIBRATE_KEY = "vibrate_key";

    // 课件存储路径
    String DOWNLOAD_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/player.sdk/file/";

    String KEY_HOMEWORK_INDEX_KEY = "key_homework_index_key";
    String APP_CONFIG_MOVE_INTERNET_LIVE = "app_config_move_internet_live";
    String INTEREST_SELECTED_THRID_OID_CACHE = "interest_selected_thrid_oid_cache";
}
