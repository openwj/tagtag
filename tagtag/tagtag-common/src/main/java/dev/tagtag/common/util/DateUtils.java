package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;
import dev.tagtag.common.constant.GlobalConstants;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间工具
 */
@UtilityClass
public class DateUtils {

    /**
     * 获取当前时间（默认时区）
     * @return 当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(GlobalConstants.DEFAULT_ZONE_ID);
    }

    /**
     * 当前毫秒时间戳
     * @return 毫秒时间戳
     */
    public static long nowMillis() {
        return System.currentTimeMillis();
    }

    /**
     * 使用项目默认格式格式化日期
     * @param date 日期
     * @return 字符串
     */
    public static String formatDate(LocalDate date) {
        return GlobalConstants.DATE_FORMATTER.format(date);
    }

    /**
     * 使用项目默认格式格式化日期时间
     * @param dateTime 日期时间
     * @return 字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return GlobalConstants.DATETIME_FORMATTER.format(dateTime);
    }

    /**
     * 按指定格式解析日期字符串
     * @param text 文本
     * @param formatter 格式
     * @return 日期
     */
    public static LocalDate parseDate(String text, DateTimeFormatter formatter) {
        return LocalDate.parse(text, formatter);
    }

    /**
     * 按指定格式解析日期时间字符串
     * @param text 文本
     * @param formatter 格式
     * @return 日期时间
     */
    public static LocalDateTime parseDateTime(String text, DateTimeFormatter formatter) {
        return LocalDateTime.parse(text, formatter);
    }

    /**
     * 转换为毫秒时间戳（默认时区）
     * @param dateTime 日期时间
     * @return 毫秒时间戳
     */
    public static long toEpochMillis(LocalDateTime dateTime) {
        ZoneId zone = GlobalConstants.DEFAULT_ZONE_ID;
        return dateTime.atZone(zone).toInstant().toEpochMilli();
    }

    /**
     * 由毫秒时间戳转换为日期时间（默认时区）
     * @param epochMillis 毫秒时间戳
     * @return 日期时间
     */
    public static LocalDateTime fromEpochMillis(long epochMillis) {
        ZoneId zone = GlobalConstants.DEFAULT_ZONE_ID;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMillis), zone);
    }

    /**
     * 当天开始（00:00:00）
     * @param dateTime 任意时间
     * @return 当天开始时间
     */
    public static LocalDateTime startOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atStartOfDay();
    }

    /**
     * 当天结束（23:59:59.999999999）
     * @param dateTime 任意时间
     * @return 当天结束时间
     */
    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        return dateTime.toLocalDate().atTime(23, 59, 59, 999_999_999);
    }

    /**
     * 增加天数
     * @param dateTime 日期时间
     * @param days 天数
     * @return 增加后的时间
     */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        return dateTime.plusDays(days);
    }
}
