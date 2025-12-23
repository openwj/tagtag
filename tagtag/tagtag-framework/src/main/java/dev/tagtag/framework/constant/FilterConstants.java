package dev.tagtag.framework.constant;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FilterConstants {

    public static final String TRACE_ID_FILTER_NAME = "traceIdFilter";
    public static final String URL_PATTERN_ALL = "/*";
    public static final int TRACE_ID_FILTER_ORDER = Integer.MIN_VALUE + 10;
}
