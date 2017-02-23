package com.yunke.player.bean;

public class NoticeBean extends Result {

    /**
     * result : {"fkPlan":"3667","createTime":"2016-12-07 16:03:33","content":"哈哈哈\n","status":"1","id":"65"}
     */

    private ResultBean result;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * fkPlan : 3667
         * createTime : 2016-12-07 16:03:33
         * content : 哈哈哈

         * status : 1
         * id : 65
         */

        private String fkPlan;
        private String createTime;
        private String content;
        private String status;
        private String id;

        public String getFkPlan() {
            return fkPlan;
        }

        public void setFkPlan(String fkPlan) {
            this.fkPlan = fkPlan;
        }

        public String getCreateTime() {
            return createTime;
        }

        public void setCreateTime(String createTime) {
            this.createTime = createTime;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
