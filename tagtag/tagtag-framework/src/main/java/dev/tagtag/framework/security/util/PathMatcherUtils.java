package dev.tagtag.framework.security.util;

public final class PathMatcherUtils {

    private PathMatcherUtils() {}

    public static boolean matchPath(String pattern, String path) {
        // 精确匹配
        if (pattern.equals(path)) {
            return true;
        }
        // 匹配 /** 模式（多级路径）
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            return path.startsWith(prefix);
        }
        // 匹配 /* 模式（单级路径）
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            if (!path.startsWith(prefix)) {
                return false;
            }
            // 检查是否只有一级
            String remaining = path.substring(prefix.length());
            return !remaining.contains("/");
        }
        return false;
    }

    public static boolean isPermitPath(String path, String[] permitPaths) {
        if (permitPaths == null || permitPaths.length == 0) {
            return false;
        }
        for (String pattern : permitPaths) {
            if (matchPath(pattern, path)) {
                return true;
            }
        }
        return false;
    }
}