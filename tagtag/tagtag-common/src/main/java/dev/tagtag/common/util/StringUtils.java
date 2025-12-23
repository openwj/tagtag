package dev.tagtag.common.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class StringUtils {

    public static boolean hasText(String str) {
        return str != null && !str.trim().isEmpty();
    }

    public static String normalize(String s) {
        return s == null ? null : s.trim();
    }

    public static String normalizeToNull(String s) {
        String v = normalize(s);
        return v == null || v.isEmpty() ? null : v;
    }
}
