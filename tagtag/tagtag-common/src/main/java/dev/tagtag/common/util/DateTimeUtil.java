package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;

import java.time.format.DateTimeFormatter;
import java.time.ZoneId;

@UtilityClass
public class DateTimeUtil {

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATE);
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATETIME);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_TIME);
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");
}