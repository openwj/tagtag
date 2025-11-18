package dev.tagtag.kernel.constant;

/**
 * 权限编码常量集中管理
 */
public final class Permissions {

    private Permissions() {}

    /** 用户：创建 */
    public static final String USER_CREATE = "PERM_user:create";
    /** 用户：更新 */
    public static final String USER_UPDATE = "PERM_user:update";
    /** 用户：删除 */
    public static final String USER_DELETE = "PERM_user:delete";
    /** 用户：分配角色 */
    public static final String USER_ASSIGN_ROLE = "PERM_user:assignRole";
    /** 用户：读取 */
    public static final String USER_READ = "PERM_user:read";

    /** 角色：创建 */
    public static final String ROLE_CREATE = "PERM_role:create";
    /** 角色：更新 */
    public static final String ROLE_UPDATE = "PERM_role:update";
    /** 角色：删除 */
    public static final String ROLE_DELETE = "PERM_role:delete";
    /** 角色：分配菜单 */
    public static final String ROLE_ASSIGN_MENU = "PERM_role:assignMenu";
    /** 角色：读取 */
    public static final String ROLE_READ = "PERM_role:read";

    /** 菜单：创建 */
    public static final String MENU_CREATE = "PERM_menu:create";
    /** 菜单：更新 */
    public static final String MENU_UPDATE = "PERM_menu:update";
    /** 菜单：删除 */
    public static final String MENU_DELETE = "PERM_menu:delete";
    /** 菜单：读取 */
    public static final String MENU_READ = "PERM_menu:read";

    /** 部门：创建 */
    public static final String DEPT_CREATE = "PERM_dept:create";
    /** 部门：更新 */
    public static final String DEPT_UPDATE = "PERM_dept:update";
    /** 部门：删除 */
    public static final String DEPT_DELETE = "PERM_dept:delete";
    /** 部门：读取 */
    public static final String DEPT_READ = "PERM_dept:read";
}