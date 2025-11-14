package dev.tagtag.kernel.util;

import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;

import java.util.Collection;
import java.util.Set;

public class PermissionChecker {

    /** 判断是否具有某角色 */
    public static boolean hasRole(String role) {
        Set<String> roles = UserContextHolder.getRoles();
        return roles.contains(role);
    }

    /** 判断是否具有任一角色 */
    public static boolean hasAnyRole(Collection<String> required) {
        Set<String> roles = UserContextHolder.getRoles();
        for (String r : required) if (roles.contains(r)) return true;
        return false;
    }

    /** 判断是否具有某权限 */
    public static boolean hasPermission(String permission) {
        Set<String> perms = UserContextHolder.getPermissions();
        return perms.contains(permission);
    }

    /** 判断是否具有任一权限 */
    public static boolean hasAnyPermission(Collection<String> required) {
        Set<String> perms = UserContextHolder.getPermissions();
        for (String p : required) if (perms.contains(p)) return true;
        return false;
    }

    /** 断言必须具备某权限 */
    public static void assertPermission(String permission) {
        if (!hasPermission(permission)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "缺少权限: " + permission);
        }
    }

    /** 断言必须具备某角色 */
    public static void assertRole(String role) {
        if (!hasRole(role)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "缺少角色: " + role);
        }
    }
}
