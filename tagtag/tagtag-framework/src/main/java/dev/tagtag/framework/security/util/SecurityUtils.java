package dev.tagtag.framework.security.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

public class SecurityUtils {

    public static boolean hasAuthority(String authority) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            return false;
        }

        Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
        if (authorities == null) {
            return false;
        }

        for (GrantedAuthority ga : authorities) {
            if (ga != null && authority.equals(ga.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
