package dev.tagtag.framework.security.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class PathMatcherUtils {

    private PathMatcherUtils() {}

    public static boolean matchPath(String pattern, String path) {
        // 精确匹配
        if (pattern.equals(path)) {
            log.debug("Path matched exactly: pattern='{}', path='{}'", pattern, path);
            return true;
        }
        // 匹配 /** 模式（多级路径）
        if (pattern.endsWith("/**")) {
            String prefix = pattern.substring(0, pattern.length() - 3);
            boolean matched = path.startsWith(prefix);
            if (matched) {
                log.debug("Path matched with /**: pattern='{}', path='{}'", pattern, path);
            }
            return matched;
        }
        // 匹配 /* 模式（单级路径）
        if (pattern.endsWith("/*")) {
            String prefix = pattern.substring(0, pattern.length() - 2);
            if (!path.startsWith(prefix)) {
                return false;
            }
            // 检查是否只有一级
            String remaining = path.substring(prefix.length());
            boolean matched = !remaining.contains("/");
            if (matched) {
                log.debug("Path matched with /*: pattern='{}', path='{}'", pattern, path);
            }
            return matched;
        }
        return false;
    }

    public static boolean isPermitPath(String path, String[] permitPaths) {
        if (permitPaths == null || permitPaths.length == 0) {
            return false;
        }
        for (String pattern : permitPaths) {
            if (matchPath(pattern, path)) {
                log.debug("Path is permit path: path='{}', matched pattern='{}'", path, pattern);
                return true;
            }
        }
        log.debug("Path is NOT permit path: path='{}'", path);
        return false;
    }
}