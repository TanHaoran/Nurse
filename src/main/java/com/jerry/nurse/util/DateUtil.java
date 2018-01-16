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

        Date date;
        if (mysqlDate == null) {
            date = new Date();
        } else {
            date = new Date(parseMysqlDateToLong(mysqlDate));
        }

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
     * 转换成聊天界面需要显示的时间
     *
     * @param date
     * @return
     */
    public static String parseDateToChatDate(Date date) {

        Calendar current = Calendar.getInstance();

        Calendar today = Calendar.getInstance();    //今天

        today.set(Calendar.YEAR, current.get(Calendar.YEAR));
        today.set(Calendar.MONTH, current.get(Calendar.MONTH));
        today.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH));
        //  Calendar.HOUR——12小时制的小时数 Calendar.HOUR_OF_DAY——24小时制的小时数
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);

        Calendar yesterday = Calendar.getInstance();    //昨天

        yesterday.set(Calendar.YEAR, current.get(Calendar.YEAR));
        yesterday.set(Calendar.MONTH, current.get(Calendar.MONTH));
        yesterday.set(Calendar.DAY_OF_MONTH, current.get(Calendar.DAY_OF_MONTH) - 1);
        yesterday.set(Calendar.HOUR_OF_DAY, 0);
        yesterday.set(Calendar.MINUTE, 0);
        yesterday.set(Calendar.SECOND, 0);

        current.setTime(date);

        if (current.after(today)) {
            SimpleDateFormat format = new SimpleDateFormat("HH:mm");
            return format.format(date);
        } else if (current.before(today) && current.after(yesterday)) {
            SimpleDateFormat format = new SimpleDateFormat("昨天 HH:mm");
            return format.format(date);
        } else {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.format(date);
        }


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

    /**
     * 把Date数据转换成String(详细)
     *
     * @param date
     * @return
     */
    public static String parseDateToStringDetail(Date date) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd " +
                "HH:mm:ss");
        return format.format(date);
    }

}
