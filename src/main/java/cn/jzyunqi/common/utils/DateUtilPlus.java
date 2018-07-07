package cn.jzyunqi.common.utils;

import org.apache.commons.lang3.time.DateUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * @author wiiyaya
 * @date 2018/5/3
 */
public final class DateUtilPlus extends DateUtils {

    private DateUtilPlus() {

    }

    /**
     * 设置指定日期的开始时间，将时分秒置为00:00:00
     *
     * @param date 指定日期
     * @return 格式化后的日期
     */
    public static Date setDayStart(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 设置指定日期的结束时间，将时分秒置为23:59:59
     *
     * @param date 指定日期
     * @return 格式化后的日期
     */
    public static Date setDayEnd(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 999);
        return c.getTime();
    }

    /**
     * 获取当前自然月1号，将时分秒置为00:00:00
     *
     * @param date 指定日期
     * @return 格式化后的日期
     */
    public static Date setMonthStart(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取当前时间的整点
     *
     * @param date 时间
     * @return 整点时间
     */
    public static Date setHourStart(final Date date) {
        final Calendar c = Calendar.getInstance();
        c.setLenient(false);
        c.setTime(date);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 获取指定的整点时间
     *
     * @param hour   小时
     * @param minute 分
     * @param second 秒
     * @return 整点时间
     */
    public static Date getSpecifyDate(final Date date, int hour, int minute, int second) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.setLenient(false);
        todayStart.setTime(date);
        todayStart.set(Calendar.HOUR_OF_DAY, hour);
        todayStart.set(Calendar.MINUTE, minute);
        todayStart.set(Calendar.SECOND, second);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime();
    }
}
