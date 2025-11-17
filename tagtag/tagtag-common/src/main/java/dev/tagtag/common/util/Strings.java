package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;

/**
 * 字符串工具：提供统一的规范化处理
 */
@UtilityClass
public class Strings {

    /**
     * 规范化字符串：去除首尾空格，空值安全处理
     * @param s 输入字符串
     * @return 规范化结果
     */
    public static String normalize(String s) {
        return s == null ? null : s.trim();
    }

    /**
     * 规范化字符串：去除首尾空格，空白则返回 null
     * @param s 输入字符串
     * @return 非空规范化结果或 null
     */
    public static String normalizeToNull(String s) {
        String v = normalize(s);
        return v == null || v.isEmpty() ? null : v;
    }
}