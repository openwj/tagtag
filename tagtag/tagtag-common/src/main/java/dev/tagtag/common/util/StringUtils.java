package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;

/**
 * 字符串工具
 */
@UtilityClass
public class StringUtils {

    /**
     * 至少包含一个非空白字符
     * @param str 字符串
     * @return 是否包含文本
     */
    public static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }
}
