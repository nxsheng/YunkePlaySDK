package com.yunke.player.bean;

/**
 * Created by love on 2015/12/22.
 */
public class StudentCommentResult extends Result {

    /**
     * content : 评论内容
     * satisfaction : 5.0
     * conform : 5.0
     * expression : 5.0
     */

    public ResultEntity result;

    public static class ResultEntity {
        public String content;
        public float satisfaction;
        public float conform;
        public float expression;
        public String planName;
        public String commentTime;
    }
}
