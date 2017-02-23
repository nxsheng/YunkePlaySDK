package com.yunke.player.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期时间工具类
 *
 * @author yanan.wang
 * @date 2013.4.17
 */
public class DateTimeUtil {
    public static final String dateTimeFormatter = "yyyy-MM-dd HH:mm:ss";
    public static final String dateTimeFormatterNoSecond = "yyyy-MM-dd HH:mm";

    private DateTimeUtil() {

    }

    /**
     * 按指定格式,将指定时间获取字符串
     *
     * @param date
     * @param pattern
     * @return
     */
    public static final String getDateStringByPattern(long date, String pattern) {
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String dateString = formatter.format(date);
        return dateString;
    }

    /**
     * 将字符串时间 转换 成long
     *
     * @param strDate
     * @param pattern
     * @return
     * @throws ParseException
     */
    public static long getDateLongByPattern(String strDate, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(strDate).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 格式化日期
     *
     * @param date
     * @param pattern
     * @return
     */
    public static final String getDateStringByPattern(Date date, String pattern) {
        SimpleDateFormat sf = new SimpleDateFormat(pattern);
        String str = sf.format(date);
        return str;
    }

    public static String getCurTimeStr() {
        Calendar cal = Calendar.getInstance();
        String curDate = getDateStringByPattern(cal.getTime(), dateTimeFormatter);
        return curDate;
    }

}