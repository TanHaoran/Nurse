package com.jerry.nurse.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jerry on 2017/7/26.
 */

public class DateUtil {

    /**
     * 将Mysql日期转换成字符串
     *
     * @param mysqlDate
     * @return
     */
    public static String parseMysqlDateToString(String mysqlDate) {
        mysqlDate = mysqlDate.substring(6, mysqlDate.length() - 7);

        Date date = new Date(Long.parseLong(mysqlDate));
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 将Mysql日期转换成long型
     *
     * @param mysqlDate
     * @return
     */
    public static long parseMysqlDateToLong(String mysqlDate) {
        mysqlDate = mysqlDate.substring(6, mysqlDate.length() - 7);
        return Long.parseLong(mysqlDate);
    }

    public static Date parseStringToDate(String stringDate) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = format.parse(stringDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Date();
    }


    /**
     * 将字符串转换成Mysql日期
     *
     * @param stringDate
     * @return
     */
    public static String parseStringToMysqlDate(String stringDate) {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        try {
            date = format.parse(stringDate);
            return "/Date(" + date.getTime() + "+0800)/";
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "/Date(" + new Date().getTime() + "+0800)/";
    }
}
