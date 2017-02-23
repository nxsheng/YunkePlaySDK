package com.yunke.player.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/21.
 */

public class SerializableList implements Serializable {

    private ArrayList<WebSocketEnty.ResultEntity> list;
    private List<StudentListBean.ResultEntity> list_student_bean;

    public List<StudentListBean.ResultEntity> getList_student_bean() {
        return list_student_bean;
    }

    public void setList_student_bean(List<StudentListBean.ResultEntity> list_student_bean) {
        this.list_student_bean = list_student_bean;
    }

    public ArrayList<WebSocketEnty.ResultEntity> getList() {
        return list;
    }

    public void setList(ArrayList<WebSocketEnty.ResultEntity> list) {
        this.list = list;
    }

}
