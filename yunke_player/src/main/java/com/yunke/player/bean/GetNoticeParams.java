package com.yunke.player.bean;

import com.google.gson.Gson;
import com.yunke.player.util.CommonUtil;

public class GetNoticeParams extends BaseParams {

    public Params params;
    public String key;

    public GetNoticeParams(Params params) {
        this.params = params;
    }

    public String toJson() {
        Gson gson = new Gson();
        String paramsJson = gson.toJson(this.params);
        this.key = CommonUtil.md5(CommonUtil.md5(paramsJson + this.time + Constants.SALT));
        return gson.toJson(this);
    }

    public static class Params {
        public String fkPlan;

        public Params(String fkPlan) {
            this.fkPlan = fkPlan;
        }
    }
}
