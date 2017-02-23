package com.yunke.player.api;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.yunke.player.util.TLog;

import org.apache.http.HttpEntity;
import org.apache.http.client.params.ClientPNames;

import java.io.ByteArrayOutputStream;
import java.util.Locale;

/**
 * 网络请求工具包
 */
public class ApiHttpClient {

    public static final String HTTPS = "https://";
    public static final String HTTP = "http://";
    public static final String WS = "ws://";
    public static final String HEADER_ACCEPT_ENCODING_UTF8 = "UTF-8";
    public static final String HEADER_CONTENT_TYPE_JSON = "application/json";
    public static final String HEADER_CONTENT_TYPE_URLENCODED = "application/x-www-form-urlencoded";

    public static final String POST = "POST";
    public static final String GET = "GET";

    /**
     * 服务器响应状态码
     */
    public static final int RESPONSE_CODE_OK = 0;
    public static final int RESPONSE_CODE_TOKEN_EXPIRED = 1003;
    public static final int RESPONSE_CODE_NO_DATA = 3002;
    /**
     * 状态码：-1{服务器端请求前预处理操作的异常}
     */
    public static final int STATUS_CODE_EXCEPTION = -1;
    public static final int STATUS_CODE_CONNECT_TIME_EXCEPTION = 0;

    private static AsyncHttpClient asyncHttpClient;
    private static SyncHttpClient syncHttpClient;

    public static final String RELEASE_PROTOCOL = HTTPS;
    public static final String RELEASE_HOST = "www.yunke.com";
    public static final String RELEASE_WS_IMG_URL = "https://f.gn100.com/";
    public static final String RELEASE_VIDEO_UPLOAD_URL = "https://upload.yunke.com/upload";
    public static final String RELEASE_VIDEO_DOWNLOAD_URL = "https://hls.yunke.com/";
    public static final String RELEASE_WEBSOCKET_URL = "ws://message.yunke.com/msg/ws";

    public static String WS_IMG_URL = RELEASE_WS_IMG_URL;
    public static String VIDEO_UPLOAD_URL = RELEASE_VIDEO_UPLOAD_URL;
    public static String VIDEO_DOWNLOAD_URL = RELEASE_VIDEO_DOWNLOAD_URL;
    public static String API_NEED_PROTOCOL = RELEASE_PROTOCOL;
    public static String NEED_HOST = RELEASE_HOST;
    public static String WEBSOCKET_URL = RELEASE_WEBSOCKET_URL;
    public static String OID = "0";
    public static String API_URL = API_NEED_PROTOCOL + NEED_HOST + "/%s";

//    public static AsyncHttpClient getAsyncHttpClient() {
//        return asyncHttpClient;
//    }
//
//    public static SyncHttpClient getSyncHttpClient() {
//        return syncHttpClient;
//    }

    public static void setAsyncHttpClient(AsyncHttpClient c) {
        asyncHttpClient = c;
        asyncHttpClient.addHeader("Accept-Language", Locale.getDefault().toString());
//        asyncHttpClient.addHeader("Host", HOST);
        asyncHttpClient.addHeader("Connection", "Keep-Alive");
        asyncHttpClient.addHeader("Content-Type", "application/x-www-form-urlencoded");
        asyncHttpClient.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
    }

    public static void setSyncHttpClient(SyncHttpClient c) {
        syncHttpClient = c;
        syncHttpClient.addHeader("Accept-Language", Locale.getDefault().toString());
//        syncHttpClient.addHeader("Host", HOST);
        syncHttpClient.addHeader("Connection", "Keep-Alive");
        syncHttpClient.addHeader("Content-Type", "application/x-www-form-urlencoded");
        syncHttpClient.getHttpClient().getParams()
                .setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
    }

    public static void cancelAll(Context context) {
        asyncHttpClient.cancelRequests(context, true);
    }

    public static void cancelAllSync(Context context) {
        syncHttpClient.cancelRequests(context, true);
    }

    public static void get(String partUrl, AsyncHttpResponseHandler handler) {
        asyncHttpClient.get(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("GET ").append(" action = ").append(partUrl).toString());
    }

    public static void getSync(String partUrl, AsyncHttpResponseHandler handler) {
        syncHttpClient.get(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("GET ").append(" action = ").append(partUrl).toString());
    }

    public static void get(String partUrl, RequestParams params,
                           AsyncHttpResponseHandler handler) {
        asyncHttpClient.get(getAbsoluteApiUrl(partUrl), params, handler);
        log(new StringBuilder("GET ").append(" action = ").append(partUrl).append(" | params = ")
                .append(params).toString());
    }

    public static void post(Context context, String partUrl, AsyncHttpResponseHandler handler) {
        asyncHttpClient.post(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("POST ").append(" action = ").append(partUrl).toString());
    }

    public static void postSync(Context context, String partUrl, AsyncHttpResponseHandler handler) {
        syncHttpClient.post(getAbsoluteApiUrl(partUrl), handler);
        log(new StringBuilder("POST ").append(" action = ").append(partUrl).toString());
    }

    public static void post(Context context, String partUrl, HttpEntity entity, String contentType, AsyncHttpResponseHandler handler) {
        asyncHttpClient.post(context, getAbsoluteApiUrl(partUrl), entity, contentType, handler);
        log(new StringBuilder("POST ").append(" action = ").append(partUrl).append(" | params = ").append(printEntityInfo(entity)).toString());
    }

    public static void postSync(Context context, String partUrl, HttpEntity entity, String contentType, AsyncHttpResponseHandler handler) {
        syncHttpClient.post(context, getAbsoluteApiUrl(partUrl), entity, contentType, handler);
        log(new StringBuilder("POST ").append(" action = ").append(partUrl).append(" | params = ").append(printEntityInfo(entity)).toString());
    }

    public static void postFullUrl(Context context, String fullUrl, HttpEntity entity, String contentType, AsyncHttpResponseHandler handler) {
        asyncHttpClient.post(context, fullUrl, entity, contentType, handler);
        log(new StringBuilder("POST ").append(" action = ").append(fullUrl).append(" | params = ").append(printEntityInfo(entity)).toString());
    }

    public static String getAbsoluteApiUrl(String partUrl) {
        String url = String.format(API_URL, partUrl);
        TLog.analytics("BASE_CLIENT", "request:" + url);
        return url;
    }

    public static void log(String log) {
        TLog.analytics("BaseApi", log);
    }

    public static String printEntityInfo(HttpEntity entity) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            entity.writeTo(baos);
            return baos.toString();
        } catch (Exception e) {
        }
        return "";
    }
}
