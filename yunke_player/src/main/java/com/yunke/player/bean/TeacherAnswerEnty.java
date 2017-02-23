package com.yunke.player.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zpf on 2015/11/30.
 */
public class TeacherAnswerEnty implements Serializable {


    /**
     * answer : A
     * id : 863
     * rank : [{"duration":"-21","id":"255","name":"李胜红"}]
     * unanswer : [{"id":"282","name":"张齐"}]
     */

    private String answer;
    private String id;
    /**
     * duration : -21
     * id : 255
     * name : 李胜红
     */

    private List<RankEntity> rank;
    /**
     * id : 282
     * name : 张齐
     */

    private List<UnanswerEntity> unanswer;

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<RankEntity> getRank() {
        return rank;
    }

    public void setRank(List<RankEntity> rank) {
        this.rank = rank;
    }

    public List<UnanswerEntity> getUnanswer() {
        return unanswer;
    }

    public void setUnanswer(List<UnanswerEntity> unanswer) {
        this.unanswer = unanswer;
    }

    public static class RankEntity {
        private String duration;
        private String id;
        private String name;

        public String getDuration() {
            return duration;
        }

        public void setDuration(String duration) {
            this.duration = duration;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public static class UnanswerEntity {
        private String id;
        private String name;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
