package dev.tagtag.framework.security.util;

import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

public final class JwtClaimUtils {

    private JwtClaimUtils() {}

    public static Long claimAsLong(Jwt jwt, String key) {
        Object v = jwt.getClaim(key);
        if (v instanceof Number n) return n.longValue();
        if (v instanceof String s) {
            try { return Long.parseLong(s); } catch (Exception ignore) {}
        }
        return null;
    }

    public static Set<Long> claimAsLongSet(Jwt jwt, String key) {
        Object v = jwt.getClaim(key);
        Set<Long> set = new LinkedHashSet<>();
        if (v instanceof Collection<?> c) {
            for (Object o : c) {
                if (o instanceof Number n) set.add(n.longValue());
                else if (o instanceof String s) {
                    try { set.add(Long.parseLong(s)); } catch (Exception ignore) {}
                }
            }
        }
        return set;
    }

    public static Set<String> claimAsStringSet(Jwt jwt, String key) {
        Object v = jwt.getClaim(key);
        Set<String> set = new LinkedHashSet<>();
        if (v instanceof Collection<?> c) {
            for (Object o : c) {
                if (o != null) set.add(String.valueOf(o));
            }
        }
        return set;
    }
}
