package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 字符串工具
 */
@UtilityClass
public class StringUtils {

    /**
     * 判断字符串是否为空（null 或 长度为0）
     * @param str 字符串
     * @return 是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    /**
     * 判断是否为 Blank（null 或 仅空白字符）
     * @param str 字符串
     * @return 是否为空白
     */
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 至少包含一个非空白字符
     * @param str 字符串
     * @return 是否包含文本
     */
    public static boolean hasText(String str) {
        return !isBlank(str);
    }

    /**
     * 默认字符串（当入参为 null 返回默认值）
     * @param str 原字符串
     * @param defaultStr 默认值
     * @return 结果字符串
     */
    public static String defaultString(String str, String defaultStr) {
        return str == null ? defaultStr : str;
    }

    /**
     * 去除首尾空白字符，若结果为空返回 null
     * @param str 字符串
     * @return 结果
     */
    public static String trimToNull(String str) {
        if (str == null) return null;
        String t = str.trim();
        return t.isEmpty() ? null : t;
    }

    /**
     * 连接集合为字符串（跳过空元素）
     * @param parts 元素集合
     * @param delimiter 分隔符
     * @return 拼接结果
     */
    public static String join(Collection<?> parts, String delimiter) {
        if (parts == null || parts.isEmpty()) return "";
        String d = delimiter == null ? "" : delimiter;
        return parts.stream().filter(Objects::nonNull).map(String::valueOf).collect(Collectors.joining(d));
    }

    /**
     * 按分隔符拆分字符串并去除空白项
     * @param str 字符串
     * @param delimiter 分隔符（正则）
     * @return 拆分列表
     */
    public static List<String> splitAndTrim(String str, String delimiter) {
        if (isBlank(str)) return java.util.Collections.emptyList();
        String[] arr = str.split(delimiter);
        return java.util.Arrays.stream(arr)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 首字母大写
     * @param str 字符串
     * @return 结果
     */
    public static String capitalize(String str) {
        if (isEmpty(str)) return str;
        return Character.toUpperCase(str.charAt(0)) + str.substring(1);
    }

    /**
     * 首字母小写
     * @param str 字符串
     * @return 结果
     */
    public static String uncapitalize(String str) {
        if (isEmpty(str)) return str;
        return Character.toLowerCase(str.charAt(0)) + str.substring(1);
    }
}
