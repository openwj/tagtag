package dev.tagtag.framework.constant;

import java.util.List;

public final class SecurityConstants {

    private SecurityConstants() {}

    public static final List<String> DEFAULT_PUBLIC_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/auth/logout",
            "/api/captcha/**",
            "/v3/api-docs/**",
            "/scalar/**",
            "/index.html",
            "/",
            "/favicon.ico",
            "/static/**",
            "/css/**",
            "/js/**",
            "/images/**",
            "/api/auth/tools/**"
    );

    public static final String PERMIT_PATHS_PROPERTY = "security.permit-paths";
    public static final String URL_PATTERN_ALL = "/**";
    public static final String X_FORWARDED_FOR = "X-Forwarded-For";
    public static final String X_REAL_IP = "X-Real-IP";
}

