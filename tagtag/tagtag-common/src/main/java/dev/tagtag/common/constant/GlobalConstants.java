package dev.tagtag.common.constant;

import lombok.experimental.UtilityClass;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@UtilityClass
public class GlobalConstants {

    public static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;
    public static final String CHARSET_UTF8_STR = StandardCharsets.UTF_8.name();
    public static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;
    public static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("Asia/Shanghai");

    public static final String FORMAT_DATE = "yyyy-MM-dd";
    public static final String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FORMAT_TIME = "HH:mm:ss";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATE);
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATETIME);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_TIME);

    public static final int DEFAULT_PAGE_NO = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 200;
    public static final String PAGE_PARAM_PAGE_NO = "pageNo";
    public static final String PAGE_PARAM_PAGE_SIZE = "pageSize";
    public static final String PAGE_PARAM_SORT = "sort";

    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_REQUEST_ID = "X-Request-Id";
    public static final String HEADER_TENANT_ID = "X-Tenant-Id";
    public static final String HEADER_REAL_IP = "X-Real-IP";
    public static final String HEADER_FORWARDED = "X-Forwarded-For";

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String API_PREFIX = "/api";
    public static final String LOGIN_PATH = "/api/auth/login";
    public static final String LOGOUT_PATH = "/api/auth/logout";

    public static final String TRACE_ID_MDC_KEY = "traceId";

    public static int normalizePageNo(Integer pageNo) {
        if (pageNo == null || pageNo < 1) {
            return DEFAULT_PAGE_NO;
        }
        return pageNo;
    }

    public static int clampPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }
}
