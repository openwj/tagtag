package dev.tagtag.kernel.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UserContextHolder {

    private static final InheritableThreadLocal<UserContext> CTX = new InheritableThreadLocal<>();

    /** 设置当前用户上下文 */
    public static void set(UserContext ctx) { CTX.set(ctx); }

    /** 获取当前用户上下文 */
    public static UserContext get() { return CTX.get(); }

    /** 清理当前用户上下文 */
    public static void clear() { CTX.remove(); }

    /** 是否已认证（存在用户上下文且有用户ID） */
    public static boolean isAuthenticated() { return getUserId() != null; }

    /** 获取当前用户ID */
    public static Long getUserId() { return get() != null ? get().userId : null; }

    /** 获取当前用户名 */
    public static String getUsername() { return get() != null ? get().username : null; }

    /** 获取当前角色集合 */
    public static Set<String> getRoles() { return get() != null ? get().roles : Collections.emptySet(); }

    /** 获取当前权限集合 */
    public static Set<String> getPermissions() { return get() != null ? get().permissions : Collections.emptySet(); }

    /** 判断是否具备某角色 */
    public static boolean hasRole(String role) { return getRoles().contains(role); }

    /** 判断是否具备某权限 */
    public static boolean hasPermission(String permission) { return getPermissions().contains(permission); }

    /**
     * 在指定用户上下文下执行任务（执行完毕自动还原）
     * @param ctx 用户上下文
     * @param task 任务
     */
    public static void runWithContext(UserContext ctx, Runnable task) {
        UserContext previous = get();
        try {
            set(ctx);
            task.run();
        } finally {
            if (previous == null) clear(); else set(previous);
        }
    }

    /** 用户上下文模型（简化） */
    public static class UserContext {
        public Long userId;
        public String username;
        public Set<String> roles = new HashSet<>();
        public Set<String> permissions = new HashSet<>();

        /** 创建上下文 */
        public static UserContext of(Long userId, String username, Set<String> roles, Set<String> permissions) {
            UserContext uc = new UserContext();
            uc.userId = userId;
            uc.username = username;
            if (roles != null) uc.roles.addAll(roles);
            if (permissions != null) uc.permissions.addAll(permissions);
            return uc;
        }
    }
}
