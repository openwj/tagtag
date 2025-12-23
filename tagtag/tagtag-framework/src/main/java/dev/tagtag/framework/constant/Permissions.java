package dev.tagtag.framework.constant;

public final class Permissions {

    private Permissions() {}

    public static final String PREFIX = "PERM_";

    public static final String USER_CREATE = "user:create";
    public static final String USER_UPDATE = "user:update";
    public static final String USER_DELETE = "user:delete";
    public static final String USER_ASSIGN_ROLE = "user:assignRole";
    public static final String USER_READ = "user:read";

    public static final String ROLE_CREATE = "role:create";
    public static final String ROLE_UPDATE = "role:update";
    public static final String ROLE_DELETE = "role:delete";
    public static final String ROLE_ASSIGN_MENU = "role:assignMenu";
    public static final String ROLE_READ = "role:read";

    public static final String MENU_CREATE = "menu:create";
    public static final String MENU_UPDATE = "menu:update";
    public static final String MENU_DELETE = "menu:delete";
    public static final String MENU_READ = "menu:read";

    public static final String DEPT_CREATE = "dept:create";
    public static final String DEPT_UPDATE = "dept:update";
    public static final String DEPT_DELETE = "dept:delete";
    public static final String DEPT_READ = "dept:read";

    public static final String FILE_READ = "file:read";
    public static final String FILE_DOWNLOAD = "file:download";

    public static final String DICT_TYPE_CREATE = "dictType:create";
    public static final String DICT_TYPE_UPDATE = "dictType:update";
    public static final String DICT_TYPE_DELETE = "dictType:delete";
    public static final String DICT_TYPE_READ = "dictType:read";

    public static final String DICT_DATA_CREATE = "dictData:create";
    public static final String DICT_DATA_UPDATE = "dictData:update";
    public static final String DICT_DATA_DELETE = "dictData:delete";
    public static final String DICT_DATA_READ = "dictData:read";

    public static final String MESSAGE_READ = "message:read";
    public static final String MESSAGE_UPDATE = "message:update";
    public static final String MESSAGE_DELETE = "message:delete";

    public static final String STATS_READ = "stats:read";

    public static String authority(String code) {
        return PREFIX + code;
    }
}
