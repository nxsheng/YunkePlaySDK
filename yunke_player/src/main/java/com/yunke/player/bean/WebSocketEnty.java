package com.yunke.player.bean;

import android.support.annotation.NonNull;

import java.util.List;

/**
 * Created by zpf on 2015/11/23.
 */
public class WebSocketEnty extends ProtocalObj {


    /**
     * result : [{"mt":"text","sg":0,"st":32450,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"开始上课","ct":1,"lu":"2015-11-16 10:39:40","ls":9},{"mt":"text","sg":0,"st":32452,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"马上结束","ct":1,"lu":"2015-11-16 10:39:56","ls":25},{"mt":"text","sg":0,"st":32453,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"结束了","ct":1,"lu":"2015-11-16 10:40:03","ls":0},{"mt":"text","sg":0,"st":32454,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"haha","ct":1,"lu":"2015-11-16 10:51:41","ls":0},{"mt":"text","sg":0,"st":32463,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"haha","ct":1,"lu":"2015-11-17 14:17:03","ls":3},{"mt":"text","sg":0,"st":32464,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"test","ct":1,"lu":"2015-11-17 14:17:05","ls":5},{"mt":"text","sg":0,"st":32465,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"xinde","ct":1,"lu":"2015-11-17 14:23:52","ls":4057},{"mt":"text","sg":0,"st":32466,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"xinde2","ct":1,"lu":"2015-11-17 14:23:55","ls":4060},{"mt":"text","sg":0,"st":32467,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"哈哈","ct":1,"lu":"2015-11-17 14:59:49","ls":4065},{"mt":"text","sg":0,"st":32471,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"qqq","ct":1,"lu":"2015-11-18 11:52:25","ls":0},{"mt":"text","sg":0,"st":32472,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"11","ct":1,"lu":"2015-11-18 15:00:40","ls":0},{"mt":"text","sg":0,"st":32489,"ss":0,"pid":839,"uf":254,"uff":"","ut":0,"uft":"","c":"1","ct":1,"lu":"2015-11-20 17:32:48","ls":0},{"mt":"good","sg":803,"st":0,"ss":0,"pid":839,"uf":0,"uff":"","ut":6374,"uft":"","c":"1","ct":100,"lu":"2015-10-12 15:18:53","ls":0},{"mt":"good","sg":804,"st":0,"ss":0,"pid":839,"uf":0,"uff":"","ut":6374,"uft":"","c":"1","ct":100,"lu":"2015-10-12 15:18:53","ls":0},{"mt":"good","sg":805,"st":0,"ss":0,"pid":839,"uf":0,"uff":"","ut":5404,"uft":"","c":"1","ct":100,"lu":"2015-10-12 15:19:16","ls":0},{"mt":"signal","sg":0,"st":0,"ss":31,"pid":839,"uf":254,"uff":"05ddd","ut":0,"uft":"","c":"online","ct":1006,"lu":"","ls":0}]
     */

    private List<ResultEntity> result;

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public List<ResultEntity> getResult() {
        return result;
    }

    public static class ResultEntity implements Comparable {
        /**
         * mt : text             //消息类型(text文本消息,good点赞消息,signal信号消息)
         * sg : 0                //点赞消息自增ID,用来判断消息是否重复被发送,只有比当前值大的ID才有效
         * st : 32450            //文本消息自增ID,用来判断消息是否重复被发送,只有比当前值大的ID才有效
         * ss : 0                //信号消息自增ID,用来判断消息是否重复被发送,只有比当前值大的ID才有效
         * pid : 839             //上课ID
         * uf : 254              //本条消息的发送者的用户ID
         * uff :                 //本条消息的发送者的TOKEN,只取前5位
         * ut : 0                //本条消息的接收者的用户ID，一般只有点赞和信号才有对应接收者
         * uft :                 //本条消息的接收者的TOKEN
         * c : 开始上课          //消息内容
         * ct : 1                     //消息具本类型
         * lu : 2015-11-16 10:39:40   //最后修改时间
         * ls : 9                     // 直播视频的当前播放时间
         */
        public boolean onlyTeacherCanSee;
        private String mt; // MessageType   string //消息类型
        private int sg; // StartGoodId   int
        private int st; // StartTextId   int
        private int ss; // StartSignalId int
        private int pid; // PlanId       int

        private int uf; // UserIdFrom   int //来源用户
        private String uff; // UserFlagFrom string  //目标用户标识，取前5位，下行的时候需要

        private String uf_n; // UserFromName string
        private String uf_t; // UserFromThumb string
        private int uf_l; // UserFromLevel int
        private String uf_lt; // UserToLevelTitle string

        private int ut; // UserIdTo   int     //目标用户
        private String uft; // UserFlagTo string //目标用户标识，取前5位，下行的时候需要

        private String ut_n; // UserToName string
        private String ut_t; // UserToThumb string
        private int ut_l; // UserToLevel int
        private String ut_lt; // UserToLevelTitle string

        private String c; // Content     string    //内容
        private int ct; // ContentType int     //内容类型
        private String lu; // LastUpdate  string  //内容
        private int ls; // LiveSecond  float64 //直播时间

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

        public void setMt(String mt) {
            this.mt = mt;
        }

        public void setSg(int sg) {
            this.sg = sg;
        }

        public void setSt(int st) {
            this.st = st;
        }

        public void setSs(int ss) {
            this.ss = ss;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public void setUf(int uf) {
            this.uf = uf;
        }

        public void setUff(String uff) {
            this.uff = uff;
        }

        public void setUt(int ut) {
            this.ut = ut;
        }

        public void setUft(String uft) {
            this.uft = uft;
        }

        public void setC(String c) {
            this.c = c;
        }

        public void setCt(int ct) {
            this.ct = ct;
        }

        public void setLu(String lu) {
            this.lu = lu;
        }

        public void setLs(int ls) {
            this.ls = ls;
        }

        public String getMt() {
            return mt;
        }

        public int getSg() {
            return sg;
        }

        public int getSt() {
            return st;
        }

        public int getSs() {
            return ss;
        }

        public int getPid() {
            return pid;
        }

        public int getUf() {
            return uf;
        }

        public String getUff() {
            return uff;
        }

        public int getUt() {
            return ut;
        }

        public String getUft() {
            return uft;
        }

        public String getC() {
            return c;
        }

        public int getCt() {
            return ct;
        }

        public String getLu() {
            return lu;
        }

        public int getLs() {
            return ls;
        }

        @Override
        public String toString() {
            return "ResultEntity{" +
                    "c='" + c + '\'' +
                    ", mt='" + mt + '\'' +
                    ", sg=" + sg +
                    ", st=" + st +
                    ", ss=" + ss +
                    ", pid=" + pid +
                    ", uf=" + uf +
                    ", uff='" + uff + '\'' +
                    ", ut=" + ut +
                    ", uft='" + uft + '\'' +
                    ", ct=" + ct +
                    ", lu='" + lu + '\'' +
                    ", ls=" + ls +
                    ", uf_n='" + uf_n + '\'' +
                    ", uf_t='" + uf_t + '\'' +
                    ", uf_l=" + uf_l +
                    ", uf_lt='" + uf_lt + '\'' +
                    ", ut_n='" + ut_n + '\'' +
                    ", ut_t='" + ut_t + '\'' +
                    ", ut_l=" + ut_l +
                    ", ut_lt='" + ut_lt + '\'' +
                    '}';
        }


        @Override
        public int compareTo(@NonNull Object o) {
            return Integer.parseInt(((ResultEntity) o).getC()) - Integer.parseInt(this.getC());
        }
    }


}
