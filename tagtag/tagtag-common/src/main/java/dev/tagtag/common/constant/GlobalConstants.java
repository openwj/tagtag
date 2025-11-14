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
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATE);
    public static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern(FORMAT_DATETIME);

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

    /**
     * 归一化页码（null 或 < 1 时返回默认值）
     * @param pageNo 页码
     * @return 归一化后的页码
     */
    public static int normalizePageNo(Integer pageNo) {
        if (pageNo == null || pageNo < 1) {
            return DEFAULT_PAGE_NO;
        }
        return pageNo;
    }

    /**
     * 限制分页大小（null 返回默认值，超出上限则截断）
     * @param pageSize 每页大小
     * @return 归一化后的分页大小
     */
    public static int clampPageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(pageSize, MAX_PAGE_SIZE);
    }

    /**
     * 为令牌添加 Bearer 前缀（已包含则原样返回）
     * @param token 令牌字符串
     * @return 带前缀的令牌
     */
    public static String ensureBearerPrefix(String token) {
        Objects.requireNonNull(token, "token");
        String t = token.trim();
        if (t.isEmpty()) {
            return t;
        }
        return t.startsWith(TOKEN_PREFIX) ? t : TOKEN_PREFIX + t;
    }

    /**
     * 生成请求唯一标识（UUID）
     * @return 请求ID
     */
    public static String generateRequestId() {
        return UUID.randomUUID().toString();
    }

    /**
     * 按项目默认格式格式化日期（yyyy-MM-dd）
     * @param date 日期
     * @return 格式化字符串
     */
    public static String formatDate(LocalDate date) {
        Objects.requireNonNull(date, "date");
        return DATE_FORMATTER.format(date);
    }

    /**
     * 按项目默认格式格式化日期时间（yyyy-MM-dd HH:mm:ss）
     * @param dateTime 日期时间
     * @return 格式化字符串
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");
        return DATETIME_FORMATTER.format(dateTime);
    }
}
