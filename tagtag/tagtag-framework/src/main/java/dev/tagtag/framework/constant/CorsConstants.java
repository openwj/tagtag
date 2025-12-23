package dev.tagtag.framework.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CorsConstants {

    public static final String ALLOWED_ORIGIN_PATTERN = "*";
    public static final String ALLOWED_METHOD = "*";
    public static final String ALLOWED_HEADER = "*";
    public static final String PATH_PATTERN = "/**";
    public static final boolean ALLOW_CREDENTIALS = false;
}
