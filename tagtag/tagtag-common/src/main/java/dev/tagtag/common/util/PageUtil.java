package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class PageUtil {

    public static final int DEFAULT_PAGE_NO = 1;
    public static final int DEFAULT_PAGE_SIZE = 20;
    public static final int MAX_PAGE_SIZE = 200;
    public static final String PAGE_PARAM_PAGE_NO = "pageNo";
    public static final String PAGE_PARAM_PAGE_SIZE = "pageSize";
    public static final String PAGE_PARAM_SORT = "sort";

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