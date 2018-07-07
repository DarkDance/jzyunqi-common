package cn.jzyunqi.common.utils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.Temporal;
import java.util.Date;

/**
 * @author wiiyaya
 * @date 2018/5/25.
 */
public class DateTimeUtilPlus {

    public static final String SYSTEM_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String getCurrentDateStr(String pattern) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getCurrentDateTimeStr(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String getCurrentTimeStr(String pattern) {
        return LocalTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDate parseLocalDate(String dateStr, String pattern) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalDateTime parseLocalDateTime(String dateTimeStr, String pattern) {
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    public static LocalTime parseLocalTime(String timeStr, String pattern) {
        return LocalTime.parse(timeStr, DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatLocalDate(LocalDate date, String pattern) {
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatLocalDateTime(LocalDateTime datetime, String pattern) {
        return datetime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static String formatLocalTime(LocalTime time, String pattern) {
        return time.format(DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 日期相隔天数
     *
     * @param startDateInclusive 开始时间
     * @param endDateExclusive   结束时间
     * @return 天
     */
    public static int periodDays(LocalDate startDateInclusive, LocalDate endDateExclusive) {
        return Period.between(startDateInclusive, endDateExclusive).getDays();
    }

    /**
     * 日期相隔小时
     *
     * @param startInclusive 开始时间
     * @param endExclusive   结束时间
     * @return 小时
     */
    public static long durationHours(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive).toHours();
    }

    /**
     * 日期相隔分钟
     *
     * @param startInclusive 开始时间
     * @param endExclusive   结束时间
     * @return 分
     */
    public static long durationMinutes(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive).toMinutes();
    }

    /**
     * 日期相隔秒数
     *
     * @param startInclusive 开始时间
     * @param endExclusive   结束时间
     * @return 秒(如果开始时间大于结束时间返回0)
     */
    public static long durationSeconds(Temporal startInclusive, Temporal endExclusive) {
        long seconds = Duration.between(startInclusive, endExclusive).getSeconds();
        return seconds > 0L ? seconds : 0L;
    }

    /**
     * 日期相隔毫秒数
     *
     * @param startInclusive 开始时间
     * @param endExclusive   结束时间
     * @return 毫秒
     */
    public static long durationMillis(Temporal startInclusive, Temporal endExclusive) {
        return Duration.between(startInclusive, endExclusive).toMillis();
    }

    /**
     * 是否当天
     *
     * @param date 时间
     * @return true 当天
     */
    public static boolean isToday(LocalDate date) {
        return LocalDate.now().equals(date);
    }

    public static Long toEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * Date 转 LocalDateTime
     *
     * @param date util日期
     * @return LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * Date 转 LocalDate
     *
     * @param date util日期
     * @return LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        return toLocalDateTime(date).toLocalDate();
    }

    /**
     * Date 转 LocalTime
     *
     * @param date util日期
     * @return LocalTime
     */
    public static LocalTime toLocalTime(Date date) {
        return toLocalDateTime(date).toLocalTime();
    }


    /**
     * LocalDateTime 转 Date
     *
     * @param localDateTime 日期
     * @return Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate 转 Date
     *
     * @param localDate 日期
     * @return Date
     */
    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalTime 转 Date
     *
     * @param localTime 日期
     * @return Date
     */
    public static Date toDate(LocalTime localTime) {
        LocalDateTime localDateTime = LocalDateTime.of(LocalDate.now(), localTime);
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * eg：
     * LocalTime zero = LocalTime.of(0, 0, 0); // 00:00:00
     * LocalTime mid = LocalTime.parse("12:00:00"); // 12:00:00
     * LocalTime now = LocalTime.now(); // 23:11:08.006
     * all method
     * LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute)
     * LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second)
     * LocalDateTime of(int year, Month month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond)
     * LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute)
     * LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second)
     * LocalDateTime of(int year, int month, int dayOfMonth, int hour, int minute, int second, int nanoOfSecond)
     * LocalDateTime of(LocalDate date, LocalTime time)
     */

    /**
     * eg：
     * // 取当前日期：
     * LocalDate today = LocalDate.now(); // -> 2014-12-24
     * // 根据年月日取日期：
     * LocalDate crischristmas = LocalDate.of(2014, 12, 25); // -> 2014-12-25
     * // 根据字符串取：
     * LocalDate endOfFeb = LocalDate.parse("2014-02-28"); // 严格按照ISO yyyy-MM-dd验证，02写成2都不行，当然也有一个重载方法允许自己定义格式
     * LocalDate.parse("2014-02-29"); // 无效日期无法通过：DateTimeParseException: Invalid date
     * // 取本月第1天：
     * LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth()); // 2017-03-01
     * // 取本月第2天：
     * LocalDate secondDayOfThisMonth = today.withDayOfMonth(2); // 2017-03-02
     * // 取本月最后一天，再也不用计算是28，29，30还是31：
     * LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth()); // 2017-12-31
     * // 取下一天：
     * LocalDate firstDayOf2015 = lastDayOfThisMonth.plusDays(1); // 变成了2018-01-01
     * // 取2017年1月第一个周一，用Calendar要死掉很多脑细胞：
     * LocalDate firstMondayOf2015 = LocalDate.parse("2017-01-01").with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)); // 2017-01-02
     */

    /**
     * 1.	adjustInto	调整指定的Temporal和当前LocalDateTime对
     * 2.	atOffset	结合LocalDateTime和ZoneOffset创建一个
     * 3.	atZone	结合LocalDateTime和指定时区创建一个ZonedD
     * 4.	compareTo	比较两个LocalDateTime
     * 5.	format	格式化LocalDateTime生成一个字符串
     * 6.	from	转换TemporalAccessor为LocalDateTi
     * 7.	get	得到LocalDateTime的指定字段的值
     * 8.	getDayOfMonth	得到LocalDateTime是月的第几天
     * 9.	getDayOfWeek	得到LocalDateTime是星期几
     * 10.	getDayOfYear	得到LocalDateTime是年的第几天
     * 11.	getHour	得到LocalDateTime的小时
     * 12.	getLong	得到LocalDateTime指定字段的值
     * 13.	getMinute	得到LocalDateTime的分钟
     * 14.	getMonth	得到LocalDateTime的月份
     * 15.	getMonthValue	得到LocalDateTime的月份，从1到12
     * 16.	getNano	得到LocalDateTime的纳秒数
     * 17.	getSecond	得到LocalDateTime的秒数
     * 18.	getYear	得到LocalDateTime的年份
     * 19.	isAfter	判断LocalDateTime是否在指定LocalDateT
     * 20.	isBefore	判断LocalDateTime是否在指定LocalDateT
     * 21.	isEqual	判断两个LocalDateTime是否相等
     * 22.	isSupported	判断LocalDateTime是否支持指定时间字段或单元
     * 23.	minus	返回LocalDateTime减去指定数量的时间得到的值
     * 24.	minusDays	返回LocalDateTime减去指定天数得到的值
     * 25.	minusHours	返回LocalDateTime减去指定小时数得到的值
     * 26.	minusMinutes	返回LocalDateTime减去指定分钟数得到的值
     * 27.	minusMonths	返回LocalDateTime减去指定月数得到的值
     * 28.	minusNanos	返回LocalDateTime减去指定纳秒数得到的值
     * 29.	minusSeconds	返回LocalDateTime减去指定秒数得到的值
     * 30.	minusWeeks	返回LocalDateTime减去指定星期数得到的值
     * 31.	minusYears	返回LocalDateTime减去指定年数得到的值
     * 32.	now	返回指定时钟的当前LocalDateTime
     * 33.	of	根据年、月、日、时、分、秒、纳秒等创建LocalDateTi
     * 34.	ofEpochSecond	根据秒数(从1970-01-0100:00:00开始)创建L
     * 35.	ofInstant	根据Instant和ZoneId创建LocalDateTim
     * 36.	parse	解析字符串得到LocalDateTime
     * 37.	plus	返回LocalDateTime加上指定数量的时间得到的值
     * 38.	plusDays	返回LocalDateTime加上指定天数得到的值
     * 39.	plusHours	返回LocalDateTime加上指定小时数得到的值
     * 40.	plusMinutes	返回LocalDateTime加上指定分钟数得到的值
     * 41.	plusMonths	返回LocalDateTime加上指定月数得到的值
     * 42.	plusNanos	返回LocalDateTime加上指定纳秒数得到的值
     * 43.	plusSeconds	返回LocalDateTime加上指定秒数得到的值
     * 44.	plusWeeks	返回LocalDateTime加上指定星期数得到的值
     * 45.	plusYears	返回LocalDateTime加上指定年数得到的值
     * 46.	query	查询LocalDateTime
     * 47.	range	返回指定时间字段的范围
     * 48.	toLocalDate	返回LocalDateTime的LocalDate部分
     * 49.	toLocalTime	返回LocalDateTime的LocalTime部分
     * 50.	toString	返回LocalDateTime的字符串表示
     * 51.	truncatedTo	返回LocalDateTime截取到指定时间单位的拷贝
     * 52.	until	计算LocalDateTime和另一个LocalDateTi
     * 53.	with	返回LocalDateTime指定字段更改为新值后的拷贝
     * 54.	withDayOfMonth	返回LocalDateTime月的第几天更改为新值后的拷贝
     * 55.	withDayOfYear	返回LocalDateTime年的第几天更改为新值后的拷贝
     * 56.	withHour	返回LocalDateTime的小时数更改为新值后的拷贝
     * 57.	withMinute	返回LocalDateTime的分钟数更改为新值后的拷贝
     * 58.	withMonth	返回LocalDateTime的月份更改为新值后的拷贝
     * 59.	withNano	返回LocalDateTime的纳秒数更改为新值后的拷贝
     * 60.	withSecond	返回LocalDateTime的秒数更改为新值后的拷贝
     * 61.	withYear	返回LocalDateTime年份更改为新值后的拷贝
     */
}
