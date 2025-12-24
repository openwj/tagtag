package dev.tagtag.framework.security.context;

import dev.tagtag.kernel.constant.SecurityClaims;
import dev.tagtag.framework.security.model.UserPrincipal;
import dev.tagtag.framework.security.util.JwtClaimUtils;
import dev.tagtag.common.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Set;

/**
 * 认证上下文工具类，用于获取当前用户信息
 */
public final class AuthContext {

    private AuthContext() {}

    /**
     * 从安全上下文中获取当前JWT令牌
     * @return 当前JWT令牌
     * @throws BusinessException 如果未认证
     */
    public static Jwt getCurrentJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw BusinessException.unauthorized("未登录或会话已过期");
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Jwt j) return j;
        throw BusinessException.unauthorized("未登录或会话已过期");
    }

    /**
     * 从JWT声明中获取当前用户ID
     * @return 当前用户ID
     * @throws BusinessException 如果未认证
     */
    public static Long getCurrentUserId() {
        Jwt jwt = getCurrentJwt();
        Long uid = JwtClaimUtils.claimAsLong(jwt, SecurityClaims.UID);
        if (uid == null) {
            throw BusinessException.unauthorized("未登录或会话已过期");
        }
        return uid;
    }

    /**
     * 从JWT声明中获取当前用户名
     * @return 当前用户名
     * @throws BusinessException 如果未认证
     */
    public static String getCurrentUsername() {
        Jwt jwt = getCurrentJwt();
        String uname = jwt.getClaim(SecurityClaims.UNAME);
        if (uname == null || uname.isBlank()) {
            throw BusinessException.unauthorized("未登录或会话已过期");
        }
        return uname;
    }

    /**
     * 从JWT声明中获取当前用户角色ID集合
     * @return 角色ID集合
     */
    public static Set<Long> getCurrentRoleIds() {
        Jwt jwt = getCurrentJwt();
        return JwtClaimUtils.claimAsLongSet(jwt, SecurityClaims.ROLES);
    }

    /**
     * 从JWT声明中获取当前用户权限集合
     * @return 权限集合
     */
    public static Set<String> getCurrentPermissions() {
        Jwt jwt = getCurrentJwt();
        return JwtClaimUtils.claimAsStringSet(jwt, SecurityClaims.PERMS);
    }

    /**
     * 从JWT令牌中获取当前用户主体
     * @return 当前用户主体
     * @throws BusinessException 如果未认证
     */
    public static UserPrincipal getCurrentPrincipal() {
        Jwt jwt = getCurrentJwt();
        UserPrincipal p = buildPrincipal(jwt);
        if (p.getId() == null) {
            throw BusinessException.unauthorized("未登录或会话已过期");
        }
        return p;
    }

    /**
     * 从JWT令牌构建用户主体
     * @param jwt JWT令牌
     * @return 用户主体
     */
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