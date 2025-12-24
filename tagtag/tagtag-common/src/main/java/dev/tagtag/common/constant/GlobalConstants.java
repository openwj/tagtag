package dev.tagtag.common.constant;

import lombok.experimental.UtilityClass;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@UtilityClass
public class GlobalConstants {

    public static final Charset CHARSET_UTF8 = StandardCharsets.UTF_8;
    public static final String CHARSET_UTF8_STR = StandardCharsets.UTF_8.name();
    public static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

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
}
