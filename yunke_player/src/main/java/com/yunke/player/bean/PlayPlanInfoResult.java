package com.yunke.player.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlayPlanInfoResult extends Result {

    @SerializedName("error")
    public ErrorEntity error;
    @SerializedName("plan")
    public PlanEntity plan;
    @SerializedName("section")
    public SectionEntity section;
    @SerializedName("course")
    public CourseEntity course;
    @SerializedName("streamType")
    public String streamType;
    @SerializedName("playmode")
    public String playmode;
    @SerializedName("rtmp")
    public RtmpEntity rtmp;
    @SerializedName("chat")
    public ChatEntity chat;
    @SerializedName("publish_chat")
    public ChatEntity publishChat;
    @SerializedName("hls_v2")
    public HlsEntity hls;
    @SerializedName("live_starttime")
    public int live_starttime;
    @SerializedName("user")
    public UserEntity user;
    @SerializedName("plancansearch")
    public int plancansearch;
    @SerializedName("playLog")
    public String playLog;
    @SerializedName("clearConfig")
    public List<ClearConfigEntity> clearConfig; // 视频清晰度列表
    @SerializedName("member")
    public VipNumber vipNumber; // 所属的会员列表
    @SerializedName("thumbs")
    public ThumbsEntity thumbs;
    @SerializedName("cdn_rtmp")
    public List<RtmpLineInfoEntity> cdn_rtmp; // RTMP线路列表
    @SerializedName("cdn_hls")
    public List<HlsLineInfoEntity> cdn_hls; // HLS线路列表
    @SerializedName("default_clear")
    public String default_clear;

    public static class ErrorEntity {

        public static final String ERROR_CODE_NO_LOGIN = "-401"; // 视频需要登录后才能观看
        public static final String ERROR_CODE_NO_APPLY = "-402"; // 视频需要报名后才能观看
        public static final String ERROR_CODE_REQUEST_FAILED = "-410"; // 获取课程信息失败，请稍后再试
        public static final String ERROR_CODE_NO_START = "-412"; // 课程还没有开始，请稍后再试
        public static final String ERROR_CODE_ERROR_CLASS = "-413"; // 你已经报名了本课程其它班级，请选择正确班级后观看
        public static final String ERROR_CODE_CONVERSION_COURSE = "-420"; // 视频正在处理中，请稍后再试
        public static final String ERROR_CODE_HIDE_COURSE = "-421"; // 视频没有公开，暂时不能观看
        public static final String ERROR_CODE_NO_FIND_COURSE = "-422"; // 视频已经删除，不能观看

        @SerializedName("code")
        public String code;
        @SerializedName("msg")
        public String msg;
        @SerializedName("linktip")
        public String linktip;
        @SerializedName("link")
        public String link;
    }

    public static class PlanEntity {

        public static final String UNAPPLY_COUTSE = "0";
        public static final String APPLY_COUTSE = "1";

        @SerializedName("plan_id")
        public String planId;
        @SerializedName("class_id")
        public String classId;
        @SerializedName("section_id")
        public String sectionId;
        @SerializedName("course_id")
        public String courseId;
        @SerializedName("teacher_id")
        public String teacherId;
        @SerializedName("start_time")
        public String startTime;
        @SerializedName("teacher_info")
        public TeacherInfoEntity teacherInfo;
        @SerializedName("play_url")
        public String playUrl; // 播放地址
        @SerializedName("login_url")
        public String loginUrl; // 登录地址
        @SerializedName("apply_url")
        public String applyUrl; // 报名地址
        @SerializedName("organization_user_id")
        public String organizationUserId;
        @SerializedName("live_public_type")
        public int livePublicType; // 直播权限设置 0 没有设置，默认值0，1免费公开
        @SerializedName("video_public_type")
        public int videoPublicType; // 录播权限设置 0 没有设置，默认值0，1免费公开 2试看
        @SerializedName("video_trial_time")
        public String videoTrialTime; // 试看时间，大于0有效，单位秒
        @SerializedName("apply")
        public String apply; // 是否报名，0没有，1报名
        @SerializedName("apply_course")
        public String applyCourse; // 是否报名，0没有，1报名 （如果有多班的课程，报名了其中的任何一个班，这个字段都为1，这个用来检测多班报名情况）

        public static class TeacherInfoEntity {
            @SerializedName("user_id")
            public String userId;
            @SerializedName("name")
            public String name;
            @SerializedName("thumb_big")
            public String thumbBig;
            @SerializedName("thumb_med")
            public String thumbMed;
            @SerializedName("thumb_small")
            public String thumbSmall;
        }
    }

    public static class SectionEntity {
        @SerializedName("section_id")
        public String sectionId;
        @SerializedName("name")
        public String name;
        @SerializedName("desc")
        public String desc;
        @SerializedName("status")
        public String status;
    }

    public static class CourseEntity {
        @SerializedName("course_id")
        public String courseId;
        @SerializedName("title")
        public String title;
        @SerializedName("user_total")
        public String userTotal;
        @SerializedName("thumb_big")
        public String thumbBig;
        @SerializedName("thumb_med")
        public String thumbMed;
        @SerializedName("thumb_small")
        public String thumbSmall;
        @SerializedName("fee")
        public FeeEntity fee;

        public static class FeeEntity {
        }
    }

    public static class RtmpEntity {
        @SerializedName("url")
        public String url;
        @SerializedName("cdn_id")
        public String cdnId; // 网络CDN
        @SerializedName("streamList")
        public List<StreamEntity> streamList; // 清晰度对应频道
    }

    public static class HlsEntity {
        @SerializedName("url")
        public String url;
        @SerializedName("cdn_id")
        public String cdnId;
        @SerializedName("detail")
        public List<StreamEntity> detail;
        @SerializedName("stream")
        public String stream;
        @SerializedName("video_id")
        public String videoId;
        /*
        @SerializedName("updatetime")
        public long updatetime;
        */
    }

    public static class UserEntity {
        @SerializedName("now")
        public long serverTime;
        @SerializedName("info")
        public InfoEntity info;
        @SerializedName("token")
        public String token;
        @SerializedName("ip")
        public IpEntity ip;

        public static class InfoEntity {
            @SerializedName("uid")
            public String uid;
            @SerializedName("name")
            public String name;
        }

        public static class IpEntity {
            @SerializedName("ip")
            public String ip;
            @SerializedName("area_name")
            public String areaName;
            @SerializedName("op_name")
            public String opName;
        }
    }

    public static class ClearConfigEntity {
        @SerializedName("name")
        public String name;
        @SerializedName("bitrate")
        public int bitrate;

        @Override
        public String toString() {
            return name;
        }
    }

    public static class StreamEntity {
        @SerializedName("stream")
        public String stream;
        @SerializedName("bitrate")
        public String bitrate;
        @SerializedName("name")
        public String name;
        @SerializedName("key_name")
        public String keyName;
        @SerializedName("key_value")
        public String keyValue;

        @Override
        public String toString() {
            return name;
        }
    }

    public static class ChatEntity {
        @SerializedName("url")
        public String url;
        @SerializedName("stream")
        public String stream;
    }

    public static class VipNumber {
        @SerializedName("memberId")
        public List<String> memberId;  // 所属的会员列表
    }

    public static class ThumbsEntity {
        @SerializedName("cols")
        public int cols;
        @SerializedName("rows")
        public int rows;
        @SerializedName("width")
        public int width;
        @SerializedName("height")
        public int height;
        @SerializedName("interval")
        public int interval;
        @SerializedName("data")
        public List<String> data;
    }

    public static class RtmpLineInfoEntity {
        @SerializedName("name")
        public String name;
        @SerializedName("rtmp")
        public String rtmp;
        @SerializedName("default")
        public String defaultType;
    }

    public static class HlsLineInfoEntity {
        @SerializedName("name")
        public String name;
        @SerializedName("hls")
        public String hls;
        @SerializedName("default")
        public String defaultType;
    }
}
