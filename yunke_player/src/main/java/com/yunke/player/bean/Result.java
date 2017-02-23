package com.yunke.player.bean;

import com.yunke.player.api.ApiHttpClient;
import com.google.gson.annotations.SerializedName;

public class Result extends Base {

    /**
     * message : success
     * code : 0 成功返回0
     */
    @SerializedName("message")
    public String message;
    @SerializedName("errMsg")
    public String errMsg;
    @SerializedName("code")
    public int code;

    public boolean OK() {
        return code == ApiHttpClient.RESPONSE_CODE_OK;
    }

    @Deprecated
    public boolean NO_DATA() {
        return code == ApiHttpClient.RESPONSE_CODE_NO_DATA;
    }
}
