package dev.tagtag.framework.util;

import java.util.Set;

/**
 * 排序字段白名单集中管理
 */
public final class SortWhitelists {

    private SortWhitelists() {}

    /** 部门可排序字段白名单 */
    public static Set<String> dept() {
        return java.util.Set.of("id", "name", "sort", "createTime", "create_time");
    }

    /** 用户可排序字段白名单 */
    public static Set<String> user() {
        return java.util.Set.of("id", "username", "nickname", "createTime", "create_time");
    }

    /** 角色可排序字段白名单 */
    public static Set<String> role() {
        return java.util.Set.of("id", "code", "name", "createTime", "create_time");
    }

    /** 菜单可排序字段白名单 */
    public static Set<String> permission() {
        return java.util.Set.of("id", "menuCode", "menuName", "createTime", "create_time", "sort");
    }
}
