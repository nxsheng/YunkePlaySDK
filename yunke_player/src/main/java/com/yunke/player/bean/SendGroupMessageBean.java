package com.yunke.player.bean;

/**
 * Created by zpf on 2016/11/29.
 */

public class SendGroupMessageBean {

    public int plan_id;//必须 int
    public int user_from_id;
    public int user_to_id;
    public String user_from_token;
    public String user_to_token;
    public String message_type;//必须 string( text good signal )

    public String content;
    public int type;

}
