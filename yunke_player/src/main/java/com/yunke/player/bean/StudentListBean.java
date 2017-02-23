package com.yunke.player.bean;

import android.graphics.Bitmap;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2016/12/20.
 */

public class StudentListBean extends Result {

    public static final int STUDENT_ONLINE_STATUS = 1; // 上线状态
    public static final int STUDENT_OFFLINE_STATUS = 0; // 下线状态

    private List<ResultEntity> result;

    public List<ResultEntity> getResult() {
        return result;
    }

    public void setResult(List<ResultEntity> result) {
        this.result = result;
    }

    public static class ResultEntity implements Serializable{
        public int uid;
        public String name;
        public String thumb;
        public String userToToken;
        public Bitmap bitmap;
        /**
         * 1 答到
         * 2 点名
         */
        public int callReply;
        /**
         * 单个禁言
         * 1 正常
         * 2 禁言
         */
        public int freeForbid;
        /**
         * 集体禁言
         * 1正常
         * 2禁言
         */
        public int normalNotalk;
        public int goodNum;
        /**
         * 0 普通状态  邀请学生发言
         * 1 学生举手
         * 2 发言中
         */
        public int askIng = 0;

        @Override
        public String toString() {
            return "ResultEntity{" +
                    "uid=" + uid +
                    ", name='" + name + '\'' +
                    ", thumb='" + thumb + '\'' +
                    ", userToToken='" + userToToken + '\'' +
                    ", bitmap=" + bitmap +
                    ", callReply=" + callReply +
                    ", freeForbid=" + freeForbid +
                    ", normalNotalk=" + normalNotalk +
                    ", goodNum=" + goodNum +
                    ", askIng=" + askIng +
                    '}';
        }
    }
}
