package com.yunke.player.bean;

import java.io.Serializable;

/**
 * Created by zpf on 2016/3/3.
 */
public class GetGoodDataBean implements Serializable {


    /**
     * code : 0
     * msg : success
     * data : {"up_type":0,"fk_user":313,"fk_level":"3","title":"书生3","score":230,"add_score":"2"}
     */

    private int code;
    private String msg;
    /**
     * up_type : 0 //不升级
     * fk_user : 313 id
     * fk_level : 3 当前level
     * title : 书生3
     * score : 230   当前值
     * add_score : 2 增加经验值
     */

    private DataEntity data;

    public void setCode(int code) {
        this.code = code;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public DataEntity getData() {
        return data;
    }

    public static class DataEntity {
        private int up_type;
        private int fk_user;
        private String fk_level;
        private String title;
        private int score;
        private String add_score;

        public void setUp_type(int up_type) {
            this.up_type = up_type;
        }

        public void setFk_user(int fk_user) {
            this.fk_user = fk_user;
        }

        public void setFk_level(String fk_level) {
            this.fk_level = fk_level;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setScore(int score) {
            this.score = score;
        }

        public void setAdd_score(String add_score) {
            this.add_score = add_score;
        }

        public int getUp_type() {
            return up_type;
        }

        public int getFk_user() {
            return fk_user;
        }

        public String getFk_level() {
            return fk_level;
        }

        public String getTitle() {
            return title;
        }

        public int getScore() {
            return score;
        }

        public String getAdd_score() {
            return add_score;
        }

        @Override
        public String toString() {
            return "DataEntity{" +
                    "add_score='" + add_score + '\'' +
                    ", up_type=" + up_type +
                    ", fk_user=" + fk_user +
                    ", fk_level='" + fk_level + '\'' +
                    ", title='" + title + '\'' +
                    ", score=" + score +
                    '}';
        }

    }
}
