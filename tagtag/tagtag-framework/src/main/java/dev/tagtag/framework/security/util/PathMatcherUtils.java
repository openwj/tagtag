package dev.tagtag.framework.security.util;

import org.springframework.util.AntPathMatcher;

public final class PathMatcherUtils {

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    private PathMatcherUtils() {}

    public static boolean matchPath(String pattern, String path) {
        return PATH_MATCHER.match(pattern, path);
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