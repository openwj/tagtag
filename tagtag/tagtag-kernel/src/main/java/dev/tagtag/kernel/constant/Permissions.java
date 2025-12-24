package dev.tagtag.kernel.constant;

/**
 * 权限常量类
 * 权限格式：resource:action
 */
public final class Permissions {

    private Permissions() {}

    /**
     * 权限前缀
     */
    public static final String PREFIX = "PERM_";

    // ================= 用户权限 =================
    public static final String USER_CREATE = "user:create";
    public static final String USER_UPDATE = "user:update";
    public static final String USER_DELETE = "user:delete";
    public static final String USER_ASSIGN_ROLE = "user:assignRole";
    public static final String USER_READ = "user:read";

    // ================= 角色权限 =================
    public static final String ROLE_CREATE = "role:create";
    public static final String ROLE_UPDATE = "role:update";
    public static final String ROLE_DELETE = "role:delete";
    public static final String ROLE_ASSIGN_MENU = "role:assignMenu";
    public static final String ROLE_READ = "role:read";

    // ================= 菜单权限 =================
    public static final String MENU_CREATE = "menu:create";
    public static final String MENU_UPDATE = "menu:update";
    public static final String MENU_DELETE = "menu:delete";
    public static final String MENU_READ = "menu:read";

    // ================= 部门权限 =================
    public static final String DEPT_CREATE = "dept:create";
    public static final String DEPT_UPDATE = "dept:update";
    public static final String DEPT_DELETE = "dept:delete";
    public static final String DEPT_READ = "dept:read";

    // ================= 文件权限 =================
    public static final String FILE_READ = "file:read";
    public static final String FILE_DOWNLOAD = "file:download";

    // ================= 字典类型权限 =================
    public static final String DICT_TYPE_CREATE = "dictType:create";
    public static final String DICT_TYPE_UPDATE = "dictType:update";
    public static final String DICT_TYPE_DELETE = "dictType:delete";
    public static final String DICT_TYPE_READ = "dictType:read";

    // ================= 字典数据权限 =================
    public static final String DICT_DATA_CREATE = "dictData:create";
    public static final String DICT_DATA_UPDATE = "dictData:update";
    public static final String DICT_DATA_DELETE = "dictData:delete";
    public static final String DICT_DATA_READ = "dictData:read";

    // ================= 消息权限 =================
    public static final String MESSAGE_READ = "message:read";
    public static final String MESSAGE_UPDATE = "message:update";
    public static final String MESSAGE_DELETE = "message:delete";

    // ================= 统计权限 =================
    public static final String STATS_READ = "stats:read";

    /**
     * 获取带前缀的权限标识
     * @param code 权限编码
     * @return 带前缀的权限标识
     */
    public static String authority(String code) {
        return PREFIX + code;
    }
}
