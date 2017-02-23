package com.yunke.player.util;

/**
 * Created by zpf on 2016/1/27.
 */
public class StringToHtmlString {
    public static final String stringToHtmlString(String s){
        StringBuffer sb = new StringBuffer();
        int n = s.length();
        for (int i = 0; i < n; i++) {
            char c = s.charAt(i);
            switch (c) {
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '&': sb.append("&amp;"); break;
                case '"': sb.append("&quot;"); break;
                default:  sb.append(c); break;
            }
        }
        return sb.toString();
    }

}
