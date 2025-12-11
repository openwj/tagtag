package dev.tagtag.framework.security.context;

import dev.tagtag.framework.security.model.UserPrincipal;
import dev.tagtag.kernel.constant.SecurityClaims;
import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 提供服务层获取当前用户的通用入口，基于 SecurityContext 与 JWT claims。
 */
public final class AuthContext {

    private AuthContext() {}

    /**
     * 获取当前请求上下文中的 JWT
     * @return 当前 JWT
     * @throws AuthenticationCredentialsNotFoundException 当未认证或主体类型不支持
     */
    public static Jwt getCurrentJwt() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null)  throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或会话已过期");
        Object principal = auth.getPrincipal();
        if (principal instanceof Jwt j) return j;
        throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或会话已过期");
    }

    /**
     * 获取当前用户ID（来自 JWT claims，必需）
     * 若未认证或缺失 UID 声明，则抛出 UNAUTHORIZED（HTTP 401）。
     * @return 用户ID
     */
    public static Long getCurrentUserId() {
        Jwt jwt = getCurrentJwt();
        Long uid = claimAsLong(jwt, SecurityClaims.UID);
        if (uid == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或会话已过期");
        }
        return uid;
    }

    /**
     * 获取当前用户名（来自 JWT claims，必需）
     * 若未认证或缺失 UNAME 声明，则抛出 UNAUTHORIZED（HTTP 401）。
     * @return 用户名
     */
    public static String getCurrentUsername() {
        Jwt jwt = getCurrentJwt();
        String uname = jwt.getClaim(SecurityClaims.UNAME);
        if (uname == null || uname.isBlank()) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或会话已过期");
        }
        return uname;
    }

    /**
     * 获取当前用户角色ID集合（来自 JWT claims）
     * @return 角色ID集合，可能为空集合
     */
    public static Set<Long> getCurrentRoleIds() {
        Jwt jwt = getCurrentJwt();
        return claimAsLongSet(jwt, SecurityClaims.ROLES);
    }

    /**
     * 获取当前用户权限码集合（来自 JWT claims）
     * @return 权限码集合，可能为空集合
     */
    public static Set<String> getCurrentPermissions() {
        Jwt jwt = getCurrentJwt();
        return claimAsStringSet(jwt, SecurityClaims.PERMS);
    }

    /**
     * 获取当前用户轻量主体（不查库，必需）
     * 若未认证或缺失 UID 声明，则抛出 UNAUTHORIZED（HTTP 401）。
     * @return 轻量主体对象
     */
    public static UserPrincipal getCurrentPrincipal() {
        Jwt jwt = getCurrentJwt();
        UserPrincipal p = buildPrincipal(jwt);
        if (p.getId() == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "未登录或会话已过期");
        }
        return p;
    }

    /**
     * 从 JWT 构造轻量主体
     * @param jwt JWT
     * @return 轻量主体
     */
    public static UserPrincipal buildPrincipal(Jwt jwt) {
        Long uid = claimAsLong(jwt, SecurityClaims.UID);
        String uname = jwt.getClaim(SecurityClaims.UNAME);
        Set<Long> roleIds = claimAsLongSet(jwt, SecurityClaims.ROLES);
        Set<String> perms = claimAsStringSet(jwt, SecurityClaims.PERMS);
        Long ver = claimAsLong(jwt, SecurityClaims.VER);
        Long isAdminLong = claimAsLong(jwt, SecurityClaims.IS_ADMIN);
        boolean isAdmin = isAdminLong != null && isAdminLong == 1L;
        return new UserPrincipal(uid, uname, roleIds, perms, ver, isAdmin);
    }

    /**
     * 读取 Long 类型声明
     * @param jwt JWT
     * @param key 声明键
     * @return Long 值或 null
     */
    private static Long claimAsLong(Jwt jwt, String key) {
        Object v = jwt.getClaim(key);
        if (v instanceof Number n) return n.longValue();
        if (v instanceof String s) try { return Long.parseLong(s); } catch (Exception ignore) {}
        return null;
    }

    /**
     * 读取 Long 集合声明
     * @param jwt JWT
     * @param key 声明键
     * @return 有序去重集合
     */
    private static Set<Long> claimAsLongSet(Jwt jwt, String key) {
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

    /**
     * 读取字符串集合声明
     * @param jwt JWT
     * @param key 声明键
     * @return 有序去重集合
     */
    private static Set<String> claimAsStringSet(Jwt jwt, String key) {
        Object v = jwt.getClaim(key);
        Set<String> set = new LinkedHashSet<>();
        if (v instanceof Collection<?> c) {
            for (Object o : c) if (o != null) set.add(String.valueOf(o));
        }
        return set;
    }
}
