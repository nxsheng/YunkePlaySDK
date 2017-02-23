package com.yunke.player.bean;

/**
 * Created by zpf on 2016/11/29.
 */

public class WebSocketBackResult {

    /**
     * result : true
     * code : 0
     * code_msg :
     * message_type : text
     * type : 1
     * msg_id : 77525
     * uf_n :
     * uf_t :
     * uf_l : 0
     * uf_lt :
     * ut_n :
     * ut_t :
     * ut_l : 0
     * ut_lt :
     */

    private boolean result;
    private int code;
    private String code_msg;
    private String message_type;
    private int type;
    private int msg_id;
    private String uf_n;
    private String uf_t;
    private int uf_l;
    private String uf_lt;
    private String ut_n;
    private String ut_t;
    private int ut_l;
    private String ut_lt;

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getCode_msg() {
        return code_msg;
    }

    public void setCode_msg(String code_msg) {
        this.code_msg = code_msg;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getMsg_id() {
        return msg_id;
    }

    public void setMsg_id(int msg_id) {
        this.msg_id = msg_id;
    }

    public String getUf_n() {
        return uf_n;
    }

    public void setUf_n(String uf_n) {
        this.uf_n = uf_n;
    }

    public String getUf_t() {
        return uf_t;
    }

    public void setUf_t(String uf_t) {
        this.uf_t = uf_t;
    }

    public int getUf_l() {
        return uf_l;
    }

    public void setUf_l(int uf_l) {
        this.uf_l = uf_l;
    }

    public String getUf_lt() {
        return uf_lt;
    }

    public void setUf_lt(String uf_lt) {
        this.uf_lt = uf_lt;
    }

    public String getUt_n() {
        return ut_n;
    }

    public void setUt_n(String ut_n) {
        this.ut_n = ut_n;
    }

    public String getUt_t() {
        return ut_t;
    }

    public void setUt_t(String ut_t) {
        this.ut_t = ut_t;
    }

    public int getUt_l() {
        return ut_l;
    }

    public void setUt_l(int ut_l) {
        this.ut_l = ut_l;
    }

    public String getUt_lt() {
        return ut_lt;
    }

    public void setUt_lt(String ut_lt) {
        this.ut_lt = ut_lt;
    }
}
