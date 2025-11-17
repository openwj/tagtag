package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;

/**
 * 数值转换工具：提供统一的对象到 Long 转换（不依赖 Spring）
 */
@UtilityClass
public class Numbers {

    /**
     * 将任意对象转换为 Long（不可转换时返回 null）
     * @param src 源对象
     * @return Long 或 null
     */
    public static Long toLong(Object src) {
        if (src == null) return null;
        if (src instanceof Number n) return n.longValue();
        String s = String.valueOf(src);
        if (s == null) return null;
        s = s.trim();
        if (s.isEmpty()) return null;
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}