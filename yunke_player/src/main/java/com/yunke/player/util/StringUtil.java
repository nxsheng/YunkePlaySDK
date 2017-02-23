package com.yunke.player.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.internal.Primitives;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具包
 */
public class StringUtil {
    private final static Pattern EMAIL = Pattern
            .compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");

    private final static Pattern IMG_URL = Pattern
            .compile(".*?(gif|jpeg|png|jpg|bmp)");

    public final static Pattern URL = Pattern
            .compile("^(https|http)://.*?$(net|com|.com.cn|org|me|)");

    private final static Pattern MOBILE = Pattern.compile("^1\\d{10}$");
    public final static Pattern A_URL = Pattern.compile("<a[^>]*href='.*'>(.*)</a>");//带a标签的超链接

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=del&id=123"，解析出Action:del,id:123存入map中
     *
     * @param URL url地址
     * @return url请求参数部分
     */
    public static Map<String, String> decodeUrlParams(String URL) {
        Map<String, String> mapParams = new HashMap<String, String>();

        String[] arrSplit = null;

        String strUrlParam = truncateUrlPage(URL);
        if (strUrlParam == null) {
            return mapParams;
        }
        // 每个键值为一组
        arrSplit = strUrlParam.split("[&]");
        for (String strSplit : arrSplit) {
            String[] arrSplitEqual = null;
            arrSplitEqual = strSplit.split("[=]");

            // 解析出键值
            if (arrSplitEqual.length > 1) {
                // 正确解析
                mapParams.put(arrSplitEqual[0], arrSplitEqual[1]);
            } else {
                if (arrSplitEqual[0] != "") {
                    // 只有参数没有值，不加入
                    mapParams.put(arrSplitEqual[0], "");
                }
            }
        }
        return mapParams;
    }

    /**
     * 去掉url中的路径，留下请求参数部分
     *
     * @param strURL url地址
     * @return url请求参数部分
     */
    private static String truncateUrlPage(String strURL) {
        String strAllParam = null;
        String[] arrSplit = null;

        strURL = strURL.trim();

        arrSplit = strURL.split("[?]");
        if (strURL.length() > 1) {
            if (arrSplit.length > 1) {
                if (arrSplit[1] != null) {
                    strAllParam = arrSplit[1];
                }
            }
        }

        return strAllParam;
    }

    /**
     * json字符串转Object
     *
     * @param json
     * @param classOfT
     * @return
     */
    public static <T> T jsonToObject(String json, Class<T> classOfT) {
        Object object = null;
        try {
            object = new Gson().fromJson(json, classOfT);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Primitives.wrap(classOfT).cast(object);
    }


    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        int versioncode = 0;
        try {
            // ---get the package info---
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            versioncode = pi.versionCode;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            Log.e("VersionInfo", "Exception", e);
        }
        return versionName;
    }


    /**
     * 视频时间转换
     *
     * @param timeMs 视频时长
     * @return
     */
    public static String stringForTime(int timeMs) {
        StringBuilder stringBuilder = new StringBuilder();
        Formatter formatter = new Formatter(stringBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        stringBuilder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * 将时分秒的字符格式，转换为，long类型的毫秒数
     *
     * @param timeStr "hh:mm:dd"
     * @return
     */
    public static long stringToTime(String timeStr) {
        long time = 0;
        if (null != timeStr && !"".equals(timeStr)) {
            if (timeStr.length() == 8) { // hh:mm:dd
                String[] splitTimeStr = timeStr.split(":");
                int hour = Integer.parseInt(splitTimeStr[0]);
                int minute = Integer.parseInt(splitTimeStr[1]);
                int second = Integer.parseInt(splitTimeStr[2]);
                time = (hour * 3600 + minute * 60 + second) * 1000;
            }
        }
        return time;
    }

    /**
     * 判断字符串是否为中文
     *
     * @param str
     * @return
     */
    public static boolean gbk(String str) {
        String regx = "(([\u4E00-\u9FA5]{0,10})|([a-zA-Z]{0,50}))";
        return Pattern.matches(regx, str);
    }


    /**
     * 判断字符串是否为中文和英文
     *
     * @param str
     * @return
     */
    public static boolean isEnglish(String str) {

        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(i, i++);

            if (!gbk(temp)) {
                return false;
            }
        }

        return true;
    }


    /**
     * 判断字符串是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainChinese(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * 判断字符串是否包含字母
     *
     * @param str
     * @return
     */
    public static boolean isContainLetters(String str) {
        for (char i = 'A'; i <= 'Z'; i++) {
            if (str.contains(String.valueOf(i))) {
                return true;
            }
        }
        for (char i = 'a'; i <= 'z'; i++) {
            if (str.contains(String.valueOf(i))) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断给定字符串是否空白串。 空白串是指由空格、制表符、回车符、换行符组成的字符串 若输入字符串为null或空字符串，返回true
     *
     * @param input
     * @return boolean
     */
    public static boolean isEmpty(String input) {
        if (input == null || "".equals(input))
            return true;

        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c != ' ' && c != '\t' && c != '\r' && c != '\n') {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是不是一个合法的电子邮件地址
     *
     * @param str
     * @return
     */
    public static boolean isEmail(String str) {
        if (str == null || str.trim().length() == 0)
            return false;
        return EMAIL.matcher(str).matches();
    }

    /**
     * 判断一个mobile是否为手机号
     *
     * @param str
     * @return
     */
    public static boolean isMobile(String str) {
        if (str == null || str.trim().length() == 0) {
            return false;
        }
        return MOBILE.matcher(str).matches();
    }

    /**
     * 判断一个国际号码
     *
     * @param str
     * @return
     */
    public static boolean isCountryMobile(String str) {
        if (str == null || str.trim().length() < 6) {
            return false;
        }
        return true;
    }

    /**
     * 判断一个url是否为图片url
     *
     * @param str
     * @return
     */
    public static boolean isImgUrl(String str) {
        if (str == null || str.trim().length() == 0)
            return false;
        return IMG_URL.matcher(str).matches();
    }

    /**
     * 判断是否为一个合法的url地址
     *
     * @param str
     * @return
     */
    public static boolean isUrl(String str) {
        if (str == null || str.trim().length() == 0)
            return false;
        return URL.matcher(str).matches();
    }

    /**
     * 字符串转整数
     *
     * @param str
     * @param defValue
     * @return
     */
    public static int toInt(String str, int defValue) {
        try {
            return Integer.parseInt(str);
        } catch (Exception e) {
        }
        return defValue;
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static int toInt(Object obj) {
        if (obj == null)
            return 0;
        return toInt(obj.toString(), 0);
    }

    /**
     * 对象转整数
     *
     * @param obj
     * @return 转换异常返回 0
     */
    public static long toLong(String obj) {
        try {
            return Long.parseLong(obj);
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 字符串转布尔值
     *
     * @param b
     * @return 转换异常返回 false
     */
    public static boolean toBool(String b) {
        try {
            return Boolean.parseBoolean(b);
        } catch (Exception e) {
        }
        return false;
    }

    public static String getString(String s) {
        return s == null ? "" : s;
    }

    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader read = new BufferedReader(isr);
        try {
            String line;
            line = read.readLine();
            while (line != null) {
                res.append(line + "\n");
                line = read.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    /***
     * 截取字符串
     *
     * @param start 从那里开始，0算起
     * @param num   截取多少个
     * @param str   截取的字符串
     * @return
     */
    public static String getSubString(int start, int num, String str) {
        if (str == null) {
            return "";
        }
        int leng = str.length();
        if (start < 0) {
            start = 0;
        }
        if (start > leng) {
            start = leng;
        }
        if (num < 0) {
            num = 1;
        }
        int end = start + num;
        if (end > leng) {
            end = leng;
        }
        return str.substring(start, end);
    }

    // 数字转汉字
    static String[] units = { "", "十", "百", "千", "万", "十万", "百万", "千万", "亿",
            "十亿", "百亿", "千亿", "万亿" };
    static char[] numArray = { '零', '一', '二', '三', '四', '五', '六', '七', '八', '九' };
    public static String foematInteger(int num) {
        char[] val = String.valueOf(num).toCharArray();
        int len = val.length;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            String m = val[i] + "";
            int n = Integer.valueOf(m);
            boolean isZero = n == 0;
            String unit = units[(len - 1) - i];
            if (isZero) {
                if ('0' == val[i - 1]) {
                    // not need process if the last digital bits is 0
                    continue;
                } else {
                    // no unit for 0
                    sb.append(numArray[n]);
                }
            } else {
                sb.append(numArray[n]);
                sb.append(unit);
            }
        }
        return sb.toString();
    }

}
