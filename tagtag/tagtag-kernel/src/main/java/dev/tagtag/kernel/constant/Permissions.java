package dev.tagtag.kernel.constant;

/** 权限编码常量集中管理（仅存业务码；前缀统一在一处维护） */
public final class Permissions {

    private Permissions() {}

    public static final String PREFIX = "PERM_";

    /** 用户：创建 */
    public static final String USER_CREATE = "user:create";
    /** 用户：更新 */
    public static final String USER_UPDATE = "user:update";
    /** 用户：删除 */
    public static final String USER_DELETE = "user:delete";
    /** 用户：分配角色 */
    public static final String USER_ASSIGN_ROLE = "user:assignRole";
    /** 用户：读取 */
    public static final String USER_READ = "user:read";

    /** 角色：创建 */
    public static final String ROLE_CREATE = "role:create";
    /** 角色：更新 */
    public static final String ROLE_UPDATE = "role:update";
    /** 角色：删除 */
    public static final String ROLE_DELETE = "role:delete";
    /** 角色：分配菜单 */
    public static final String ROLE_ASSIGN_MENU = "role:assignMenu";
    /** 角色：读取 */
    public static final String ROLE_READ = "role:read";

    /** 菜单：创建 */
    public static final String MENU_CREATE = "menu:create";
    /** 菜单：更新 */
    public static final String MENU_UPDATE = "menu:update";
    /** 菜单：删除 */
    public static final String MENU_DELETE = "menu:delete";
    /** 菜单：读取 */
    public static final String MENU_READ = "menu:read";

    /** 部门：创建 */
    public static final String DEPT_CREATE = "dept:create";
    /** 部门：更新 */
    public static final String DEPT_UPDATE = "dept:update";
    /** 部门：删除 */
    public static final String DEPT_DELETE = "dept:delete";
    /** 部门：读取 */
    public static final String DEPT_READ = "dept:read";

    /** 文件：读取 */
    public static final String FILE_READ = "file:read";
    /** 文件：下载 */
    public static final String FILE_DOWNLOAD = "file:download";

    /** 字典类型：创建 */
    public static final String DICT_TYPE_CREATE = "dictType:create";
    /** 字典类型：更新 */
    public static final String DICT_TYPE_UPDATE = "dictType:update";
    /** 字典类型：删除 */
    public static final String DICT_TYPE_DELETE = "dictType:delete";
    /** 字典类型：读取 */
    public static final String DICT_TYPE_READ = "dictType:read";

    /** 字典数据：创建 */
    public static final String DICT_DATA_CREATE = "dictData:create";
    /** 字典数据：更新 */
    public static final String DICT_DATA_UPDATE = "dictData:update";
    /** 字典数据：删除 */
    public static final String DICT_DATA_DELETE = "dictData:delete";
    /** 字典数据：读取 */
    public static final String DICT_DATA_READ = "dictData:read";

    /** 消息：读取 */
    public static final String MESSAGE_READ = "message:read";
    /** 消息：更新（标记已读/未读） */
    public static final String MESSAGE_UPDATE = "message:update";
    /** 消息：删除 */
    public static final String MESSAGE_DELETE = "message:delete";

    /** 统计：读取 */
    public static final String STATS_READ = "stats:read";

    /**
     * 将业务权限码拼接为完整的 GrantedAuthority 字符串
     * @param code 业务权限码（如 "user:create"）
     * @return 完整权限（如 "PERM_user:create"）
     */
    public static String authority(String code) {
        return PREFIX + code;
    }
}
