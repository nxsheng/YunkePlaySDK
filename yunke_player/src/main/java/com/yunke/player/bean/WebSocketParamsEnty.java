package com.yunke.player.bean;

/**
 * Created by zpf on 2015/11/23.
 */
public class WebSocketParamsEnty extends ProtocalObj {

//    MessageType: //  string 必须 消息类型，固定为 get
//    PlanId://        int 必须
//    UserFlagFrom:// string //必须 目标用户标识，取前5位，下行的时候需要
//
//    TextLimit: // int 可选，文本限制数，默认为0，不限制，会读取全部，如果 >0 由 获取 大于 StartTextId 的 TextLimit数，如果 <0 ，获取小于 StartTextId的聊天文本条数。
//    StartGoodId://   int 可选，默认为0，当已经返回过good（点赞）信息后，再次获取时不能为0
//    StartTextId://   int 可选，默认为0，当已经返回过text（消息）信息后，再次获取时不能为0
//    StartSignalId:// int 可选，默认为0，当已经返回过signal（信号）信息后，再次获取时不能为0
//
//    UserIdFrom://   int 可选   //可选 来源用户
//    UserIdTo://   int 可选   //目标用户
//    UserFlagTo:// string 可选 //目标用户标识，取前5位，下行的时候需要
//    Content://     string 可选  //内容
//    ContentType:// int 可选    //内容类型
//    LastUpdate://  string 可选  //内容
//    LiveSecond://  float64 可选`json:"LiveSecond"` //直播时间

    public  String MessageType;
    public  String Content;
    public  int UserIdFrom;
    public  String UserFlagFrom;
    public  int PlanId;
    public  int StartTextId;
    public  int StartSignalId;
    public  int StartGoodId;
    public  int ContentType;
    public  int TextLimit;

}
