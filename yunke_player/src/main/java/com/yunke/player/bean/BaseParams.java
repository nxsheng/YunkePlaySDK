package com.yunke.player.bean;

import android.os.Build;

import com.google.gson.annotations.SerializedName;
import com.yunke.player.api.ApiHttpClient;
import com.yunke.player.util.TDevice;

public class BaseParams extends Base {

    /**
     * i=ios,a=android,p=pc_client
     */
    public String u = "a";
    /**
     * 当前版本号=2
     */
    public String v = "2";
    public String oid = ApiHttpClient.OID;
    public long time = System.currentTimeMillis();
    public String token = "";
    public dinfo dinfo = new dinfo();

    public class dinfo {
        @SerializedName("uid")
        public int uid = 0;
        @SerializedName("os")
        public String os = "a";
        @SerializedName("osv")
        public String osv = Build.VERSION.RELEASE;
        @SerializedName("osvc")
        public int osvc = Build.VERSION.SDK_INT;
        @SerializedName("udid")
        public String udid = "";
        @SerializedName("mac")
        public String mac = "";
        @SerializedName("ip")
        public String ip = "";
        @SerializedName("imsi")
        public String imsi = "";
        @SerializedName("imei")
        public String imei = "";
        @SerializedName("phone")
        public String phone = "";
        @SerializedName("p")
        public String p = TDevice.getPhoneType();
        @SerializedName("m")
        public String m = Build.MANUFACTURER;
        @SerializedName("d")
        public String d = Build.DEVICE;
        @SerializedName("n")
        public int n = 0;
        @SerializedName("o")
        public int o = 1;
        @SerializedName("rw")
        public int rw = 0;
        @SerializedName("rh")
        public int rh = 0;
        @SerializedName("jb")
        public int jb = 0;
        @SerializedName("cv")
        public String cv = "";
        @SerializedName("cvc")
        public int cvc = 0;
        @SerializedName("ch")
        public String ch = "";
    }
}
