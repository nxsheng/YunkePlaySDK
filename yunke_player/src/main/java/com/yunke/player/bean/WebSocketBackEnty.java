package com.yunke.player.bean;

/**
 * Created by zpf on 2016/11/29.
 */

public class WebSocketBackEnty {

    /**
     * result : {"key":"text","value":77535,"info":""}
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
         * key : text
         * value : 77535
         * info :
         */

        private String key;
        private int value;
        private String info;

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }
}
