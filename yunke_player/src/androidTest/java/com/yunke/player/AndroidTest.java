package com.yunke.player;

import android.test.InstrumentationTestCase;
import android.util.Log;

import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.SyncHttpClient;
import com.loopj.android.http.TextHttpResponseHandler;
import com.yunke.player.api.ApiHttpClient;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONObject;

public class AndroidTest extends InstrumentationTestCase {

    public void testMd5() {
        Log.d("wangyanan", CommonUtil.md5("123456"));
    }

    public void testEncoding() {
        Log.d("wangyanan", CommonUtil.gbToUnicode("中国"));
    }

    public void testDecodeUnicode() {
        Log.d("wangyanan", CommonUtil.decodeUnicode(CommonUtil.gbToUnicode("中国")));
    }

    public void testFriendly_time3() {
        Log.d("wangyanan", DateTimeUtil.friendly_time3("2015-08-16 09:10:00"));
    }

    public void testJsonToBean() {
        String json = "{\"code\":0,\"msg\":\"success\",\"data\":{\"name\": \"wenyu\",\"uid\": \"197\",\"token\": \"2927c1c588d59c6fbd3a31d8d052152f\",\"large\": \"//devf.gn100.com/2,038a3194c18e\",\"medium\": \"//devf.gn100.com/6,038b88af5b0f\",\"small\": \"//devf.gn100.com/3,038c60610802\",\"types\":{\"student\": true,\"teacher\": true,\"organization\": true},\"streamInfo\": {\"server\":\"rtmp://121.42.56.177/publish\",\"stream\": \"274ad4786c?token=a23016774cae169ccc4e49c09adf5e43\"}}}";
        Gson gson = new Gson();
        LoginResult user = gson.fromJson(json, LoginResult.class);
        Log.d("wangyanan", user.result.name + ", " + user.result.uid + ", " + user.result.token);
    }

    public void testAsync() {
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(1000);
        ApiHttpClient.setAsyncHttpClient(client);
        ApiHttpClient.get("http://www.baidu.com", new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d("wangyanan", "" + statusCode);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d("wangyanan", "" + statusCode);
            }
        });
    }

    public void testSync() {
        SyncHttpClient syncHttpClient = new SyncHttpClient();
        syncHttpClient.setTimeout(1000);
        ApiHttpClient.setSyncHttpClient(syncHttpClient);
        ApiHttpClient.getSync("http://www.baidu.com", new TextHttpResponseHandler() {
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d("wangyanan", responseString);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString) {
                Log.d("wangyanan", responseString);
            }
        });
    }

    public void testLoginApi() {
        String username = "13001111269";
        String password = "123456";
        String key = "";

        SyncHttpClient syncHttpClient = new SyncHttpClient();
        syncHttpClient.setTimeout(10000);
        ApiHttpClient.setSyncHttpClient(syncHttpClient);

        LoginParams loginParams = new LoginParams(new LoginParams.Params(username, password, "no"));

        try {
            StringEntity stringEntity = new StringEntity(loginParams.toJson());
            ApiHttpClient.postSync("interface/login", stringEntity, "application/json", new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    Log.d("wangyanan", response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    Log.d("wangyanan", errorResponse.toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
