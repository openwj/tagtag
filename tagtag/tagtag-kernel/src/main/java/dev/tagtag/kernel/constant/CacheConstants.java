package dev.tagtag.kernel.constant;

import lombok.experimental.UtilityClass;
import java.time.Duration;
import java.util.Objects;

@UtilityClass
public class CacheConstants {

    public static final String SEPARATOR = ":";
    public static final String PREFIX = "tagtag";
    public static final String AUTH = "auth";
    public static final String USER = "user";
    public static final String ROLE = "role";
    public static final String PERM = "perm";
    public static final String DEPT = "dept";
    public static final String CONFIG = "config";
    public static final String CAPTCHA = "captcha";
    public static final String RATE_LIMIT = "rate-limit";
    public static final String DICT = "dict";
    public static final String MENU = "menu";

    public static final Duration TOKEN_TTL = Duration.ofHours(12);
    public static final Duration CAPTCHA_TTL = Duration.ofMinutes(5);
    public static final Duration USER_INFO_TTL = Duration.ofMinutes(30);
    public static final Duration PERMISSIONS_TTL = Duration.ofMinutes(10);
    public static final Duration CONFIG_TTL = Duration.ofMinutes(10);
    public static final Duration RATE_LIMIT_TTL = Duration.ofMinutes(1);
    public static final Duration MENU_TTL = Duration.ofMinutes(30);
    public static final Duration DICT_TTL = Duration.ofHours(1);
    public static final Duration DEPT_TREE_TTL = Duration.ofMinutes(30);
    public static final Duration DEFAULT_TTL = Duration.ofMinutes(5);
    public static final String RATE_LIMIT_KEY_PREFIX = "rate_limit:";

    public static String compose(String... segments) {
        Objects.requireNonNull(segments, "segments");
        StringBuilder sb = new StringBuilder();
        for (String segment : segments) {
            String s = Objects.requireNonNull(segment, "segment").trim();
            if (s.isEmpty()) {
                continue;
            }
            if (!sb.isEmpty()) {
                sb.append(SEPARATOR);
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static String keyCaptcha(String uuid) {
        Objects.requireNonNull(uuid, "uuid");
        return compose(PREFIX, CAPTCHA, "img", uuid.trim());
    }
}
