package com.jerry.nurse.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

        Date date = new Date(parseMysqlDateToLong(mysqlDate));

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

    /**
     * 把MySQL日期转成标准日期格式
     *
     * @param mysqlDate
     * @return
     */
    public static Date parseMysqlDateToDate(String mysqlDate) {
        Date date = new Date(parseMysqlDateToLong(mysqlDate));
        return date;
    }

    /**
     * 把Date数据转换成String
     *
     * @param date
     * @return
     */
    public static String parseDateToString(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    /**
     * 把Date类型转换成MySqlDate类型
     *
     * @param date
     * @return
     */
    public static String parseDateToMysqlDate(Date date) {
        return "/Date(" + date.getTime() + "+0800)/";
    }

    /**
     * 把String类型转成Date类型
     *
     * @param stringDate
     * @return
     */
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

    /**
     * 计算两个日期相距间隔（月）
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getMonthsBetweenTwoDate(Date startDate, Date endDate) {
        Calendar startCalendar = new GregorianCalendar();
        startCalendar.setTime(startDate);
        Calendar endCalendar = new GregorianCalendar();
        endCalendar.setTime(endDate);

        int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
        int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
        int diffDay = endCalendar.get(Calendar.DAY_OF_MONTH) - startCalendar.get(Calendar.DAY_OF_MONTH);
        if (diffDay < 0) {
            return diffMonth - 1;
        } else {
            return diffMonth;
        }
    }


}
