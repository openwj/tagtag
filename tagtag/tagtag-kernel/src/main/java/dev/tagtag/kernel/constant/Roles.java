package dev.tagtag.kernel.constant;

/** 角色常量集中管理（仅存业务码；前缀统一在一处维护） */
public final class Roles {

    private Roles() {}

    public static final String PREFIX = "ROLE_";

    /** 系统管理员 */
    public static final String ADMIN = "admin";
    /** 普通用户 */
    public static final String USER = "user";

    /**
     * 将业务角色码拼接为完整的 GrantedAuthority 字符串
     * @param role 业务角色码（如 "admin"）
     * @return 完整角色权限（如 "ROLE_admin"）
     */
    public static String authority(String role) {
        return PREFIX + role;
    }
}
