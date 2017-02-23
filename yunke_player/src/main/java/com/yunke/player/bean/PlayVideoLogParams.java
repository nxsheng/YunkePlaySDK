package com.yunke.player.bean;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * author: wangyanan on 2015/12/22 10:28
 * email: 13001111269@163.com
 */
public class PlayVideoLogParams {

    public static final int VID_DEFAULT = 0;
    public static final String VV_DEFAULT = "1";
    public static final String VV_PLAYING = "0";
    public static final String PM_RTMP = "rtmp";
    public static final String PM_HLS = "hls";

    public PlayVideoLogParams() {

    }

    public PlayVideoLogParams(String uid, String ip, String op, String an, String oid, String cid, String sid, String clid, String pid) {
        this.uid = uid;
        this.ip = ip;
        this.op = op;
        this.an = an;
        this.oid = oid;
        this.cid = cid;
        this.sid = sid;
        this.clid = clid;
        this.pid = pid;
    }

    public String toJson() {
        Gson gson = new Gson();
//        String paramsJson = gson.toJson(this);
//        this.key = CommonUtil.md5(CommonUtil.md5(paramsJson + this.time + Constants.SALT));
        return gson.toJson(this);
    }

    /**
     * uid : 23
     * vid : 0
     * vv : 1
     * pm : rtmp
     * ip : 127.0.0.1
     * op : 电信
     * an : 北京
     * oid : 123
     * cid : 123
     * sid : 123
     * clid : 123
     * pid : 123
     * cdnid : 3
     * vst : 350ms
     * lgt : 444443
     * lct : 123545
     * bf : [{"b":"false","t":[234,400,33999,334555]},{"b":"false","t":[234,400,33999,334555]},{"b":"false","t":[234,400,33999,334555]},{"b":"false","t":[234,400,33999,334555]}]
     */

    @SerializedName("uid")
    public String uid;
    @SerializedName("vid")
    public int vid = VID_DEFAULT;
    @SerializedName("vv")
    public String vv = VV_DEFAULT;
    @SerializedName("pm")
    public String pm;
    @SerializedName("ip")
    public String ip;
    @SerializedName("op")
    public String op;
    @SerializedName("an")
    public String an;
    @SerializedName("oid")
    public String oid;
    @SerializedName("cid")
    public String cid;
    @SerializedName("sid")
    public String sid;
    @SerializedName("clid")
    public String clid;
    @SerializedName("pid")
    public String pid;
    @SerializedName("cdnid")
    public String cdnid;
    @SerializedName("vst")
    public int vst; // 视频从开始连接到播放所花的时间（ms)
    @SerializedName("lgt")
    public int lgt; // 收集数据开始的时间(ms)
    @SerializedName("lct")
    public int lct; // 数据的上传时间(ms)
    @SerializedName("tp")
    public int tp = 4;
    @SerializedName("ct")
    public int ct; // 视频当前播放时间
    @SerializedName("tt")
    public int tt; // 视频总时长
    /**
     * b : false
     * t : [234,400,33999,334555]
     */

    @SerializedName("bf")
    public List<BfEntity> bf = new ArrayList<BfEntity>(); // 缓冲时间，时间段内的所有缓冲，如果缓冲未结束则计算到下一个时间段内

    public static class BfEntity {
        @SerializedName("b")
        public boolean b = false; // true缓冲并未结束又进入了另一段缓冲;false缓冲正常结束,值有效
        @SerializedName("t")
        public List<Long> t = new ArrayList<Long>(); // 0索引数值缓冲开始时视频的播放时间(s),1索引的数值缓冲结束时视频的播放时间(s),2索引的数值缓冲开始时间(ms),3索引的数值 缓冲结束时间(ms)
    }
}
