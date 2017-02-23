package com.yunke.player.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zpf on 2015/11/28.
 */
public class TeacherQuestionEnty implements Serializable {


    /**
     * id : exam_id
     * type : 1(single)or2(multi)
     * text : 文本题
     * img : 图片题
     * answer : ["Aa","Bab","Cbgasdgasg","Dcdd"]
     * phraseType : 2
     */

    private String id;
    private String type;
    private String text = "";
    private String img = "";
    private List<String> answer;
    private int phraseType;

    public int getPhraseType() {
        return phraseType;
    }

    public void setPhraseType(int phraseType) {
        this.phraseType = phraseType;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setAnswer(List<String> answer) {
        this.answer = answer;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public String getImg() {
        return img;
    }

    public List<String> getAnswer() {
        return answer;
    }
}
