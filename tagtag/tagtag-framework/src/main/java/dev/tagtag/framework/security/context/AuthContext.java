package dev.tagtag.framework.security.context;

import dev.tagtag.framework.constant.SecurityClaims;
import dev.tagtag.framework.security.model.UserPrincipal;
import dev.tagtag.framework.security.util.JwtClaimUtils;
import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

public final class AuthContext {

    private AuthContext() {}

    public static Jwt getCurrentJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或会话已过期");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Jwt j) return j;
        throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或会话已过期");
    }

    public static Long getCurrentUserId() {
        Jwt jwt = getCurrentJwt();
        Long uid = JwtClaimUtils.claimAsLong(jwt, SecurityClaims.UID);
        if (uid == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或会话已过期");
        }
        return uid;
    }

    public static String getCurrentUsername() {
        Jwt jwt = getCurrentJwt();
        String uname = jwt.getClaim(SecurityClaims.UNAME);
        if (uname == null || uname.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或会话已过期");
        }
        return uname;
    }

    public static Set<Long> getCurrentRoleIds() {
        Jwt jwt = getCurrentJwt();
        return JwtClaimUtils.claimAsLongSet(jwt, SecurityClaims.ROLES);
    }

    public static Set<String> getCurrentPermissions() {
        Jwt jwt = getCurrentJwt();
        return JwtClaimUtils.claimAsStringSet(jwt, SecurityClaims.PERMS);
    }

    public static UserPrincipal getCurrentPrincipal() {
        Jwt jwt = getCurrentJwt();
        UserPrincipal p = buildPrincipal(jwt);
        if (p.getId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或会话已过期");
        }
        return p;
    }

    public static UserPrincipal buildPrincipal(Jwt jwt) {
        Long uid = JwtClaimUtils.claimAsLong(jwt, SecurityClaims.UID);
        String uname = jwt.getClaim(SecurityClaims.UNAME);
        Set<Long> roleIds = JwtClaimUtils.claimAsLongSet(jwt, SecurityClaims.ROLES);
        Set<String> perms = JwtClaimUtils.claimAsStringSet(jwt, SecurityClaims.PERMS);
        Long ver = JwtClaimUtils.claimAsLong(jwt, SecurityClaims.VER);
        Long isAdminLong = JwtClaimUtils.claimAsLong(jwt, SecurityClaims.IS_ADMIN);
        boolean isAdmin = isAdminLong != null && isAdminLong == 1L;
        return new UserPrincipal(uid, uname, roleIds, perms, ver, isAdmin);
    }
}
