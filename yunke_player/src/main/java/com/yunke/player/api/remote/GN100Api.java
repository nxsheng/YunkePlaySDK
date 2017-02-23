package com.yunke.player.api.remote;

import android.content.Context;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yunke.player.api.ApiHttpClient;
import com.yunke.player.bean.GetNoticeParams;
import com.yunke.player.bean.PlayVideoLogParams;
import org.apache.http.entity.ByteArrayEntity;

public class GN100Api {

    private static final java.lang.String TAG = GN100Api.class.getName();

    //获取通知
    public static void getNotice(Context context, String fkPlan, AsyncHttpResponseHandler handler) {
        GetNoticeParams requestParams = new GetNoticeParams(new GetNoticeParams.Params(fkPlan));
        try {
            ByteArrayEntity stringEntity = new ByteArrayEntity(requestParams.toJson().getBytes(ApiHttpClient.HEADER_ACCEPT_ENCODING_UTF8));
            ApiHttpClient.post(context, "interface/announcement/GetAnnouncement", stringEntity, ApiHttpClient.HEADER_CONTENT_TYPE_JSON, handler);
        } catch (Exception e) {
            e.printStackTrace();
            ((TextHttpResponseHandler) handler).onFailure(ApiHttpClient.STATUS_CODE_EXCEPTION, null, e.getMessage(), e);
        }
    }

    /**
     * 课程视频播放
     *
     * @param planId
     * @param handler
     */
    public static void playPlanInfo(Context context, String planId, AsyncHttpResponseHandler handler) {
        try {
            ApiHttpClient.post(context, "player.plan.info/" + planId, handler);
        } catch (Exception e) {
            ((TextHttpResponseHandler) handler).onFailure(ApiHttpClient.STATUS_CODE_EXCEPTION, null, e.getMessage(), e);
        }
    }

    /**
     * 记录播放日志
     *
     * @param url
     * @param playLogData
     * @param handler
     */
    public static void playLog(Context context, String url, PlayVideoLogParams playLogData, AsyncHttpResponseHandler handler) {
        try {
            ByteArrayEntity stringEntity = new ByteArrayEntity(playLogData.toJson().getBytes(ApiHttpClient.HEADER_ACCEPT_ENCODING_UTF8));
            ApiHttpClient.postFullUrl(context, url, stringEntity, ApiHttpClient.HEADER_CONTENT_TYPE_JSON, handler);
        } catch (Exception e) {
            e.printStackTrace();
            ((TextHttpResponseHandler) handler).onFailure(ApiHttpClient.STATUS_CODE_EXCEPTION, null, e.getMessage(), e);
        }
    }

}