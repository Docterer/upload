package com.zero.upload.utils;


import java.util.Calendar;
import java.util.Random;

/**
 * @Author: danyiran
 * @Date: 2020/6/18 15:49
 */
public class DateUtils {

    private static Random random= new Random();

    private DateUtils(){}


    /**
     * 获得当前年份
     * @return String
     */
    public static String getYear(){
        Calendar calendar=Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.YEAR));
    }

    /**
     * 获得当前月份
     * @return String
     */
    public static String getMonth(){
        Calendar calendar=Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.MONTH)+1);
    }

    /**
     * 获得当前日期
     * @return String
     */
    public static String getDay(){
        Calendar calendar=Calendar.getInstance();
        return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * 获得当前时间
     * @return String
     */
    public static String getTime(){
        StringBuilder sb=new StringBuilder();
        Calendar calendar=Calendar.getInstance();
        sb.append(calendar.get(Calendar.HOUR_OF_DAY)).
                append(calendar.get(Calendar.MINUTE)).
                append(calendar.get(Calendar.SECOND)).
                append(calendar.get(Calendar.MILLISECOND)).
                append("_").
                append(random.nextInt(1000));

        return sb.toString();
    }

}
