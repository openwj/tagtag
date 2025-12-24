package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * 日期时间工具类
 */
@UtilityClass
public class DateTimeUtil {

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATE);
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATETIME);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_TIME);
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    /**
     * 格式化LocalDateTime为字符串
     * @param dateTime LocalDateTime对象
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime.format(DATETIME_FORMATTER);
    }

    /**
     * 格式化LocalDate为字符串
     * @param date LocalDate对象
     * @return 格式化后的字符串
     */
    public static String format(LocalDate date) {
        return date.format(DATE_FORMATTER);
    }

    /**
     * 格式化LocalTime为字符串
     * @param time LocalTime对象
     * @return 格式化后的字符串
     */
    public static String format(LocalTime time) {
        return time.format(TIME_FORMATTER);
    }

    /**
     * 解析字符串为LocalDateTime
     * @param dateTimeStr 日期时间字符串
     * @return LocalDateTime对象
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return LocalDateTime.parse(dateTimeStr, DATETIME_FORMATTER);
    }

    /**
     * 解析字符串为LocalDate
     * @param dateStr 日期字符串
     * @return LocalDate对象
     */
    public static LocalDate parseDate(String dateStr) {
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 解析字符串为LocalTime
     * @param timeStr 时间字符串
     * @return LocalTime对象
     */
    public static LocalTime parseTime(String timeStr) {
        return LocalTime.parse(timeStr, TIME_FORMATTER);
    }

    /**
     * 将Date转换为LocalDateTime
     * @param date Date对象
     * @return LocalDateTime对象
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return date.toInstant().atZone(DEFAULT_ZONE_ID).toLocalDateTime();
    }

    /**
     * 将LocalDateTime转换为Date
     * @param dateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date toDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(DEFAULT_ZONE_ID).toInstant());
    }

    /**
     * 获取当前日期时间
     * @return 当前LocalDateTime
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }

    /**
     * 获取当前日期
     * @return 当前LocalDate
     */
    public static LocalDate today() {
        return LocalDate.now(DEFAULT_ZONE_ID);
    }

    /**
     * 获取当前时间
     * @return 当前LocalTime
     */
    public static LocalTime currentTime() {
        return LocalTime.now(DEFAULT_ZONE_ID);
    }

    /**
     * 计算两个日期时间之间的差值
     * @param start 开始时间
     * @param end 结束时间
     * @param unit 时间单位
     * @return 差值
     */
    public static long between(LocalDateTime start, LocalDateTime end, ChronoUnit unit) {
        return unit.between(start, end);
    }

    /**
     * 计算两个日期之间的差值
     * @param start 开始日期
     * @param end 结束日期
     * @param unit 时间单位
     * @return 差值
     */
    public static long between(LocalDate start, LocalDate end, ChronoUnit unit) {
        return unit.between(start, end);
    }

    /**
     * 获取本月第一天
     * @return 本月第一天
     */
    public static LocalDate firstDayOfMonth() {
        return today().with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取本月最后一天
     * @return 本月最后一天
     */
    public static LocalDate lastDayOfMonth() {
        return today().with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取本周第一天（周一）
     * @return 本周第一天
     */
    public static LocalDate firstDayOfWeek() {
        return today().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    /**
     * 获取本周最后一天（周日）
     * @return 本周最后一天
     */
    public static LocalDate lastDayOfWeek() {
        return today().with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
    }
}