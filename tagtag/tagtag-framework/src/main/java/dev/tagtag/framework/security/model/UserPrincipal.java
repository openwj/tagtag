package dev.tagtag.framework.security.model;

import lombok.Getter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * 轻量用户主体模型，来源于 JWT claims，用于在控制器快速注入当前用户。
 */
@Getter
public class UserPrincipal {

    private final Long id;
    private final String username;
    private final Set<Long> roleIds;
    private final Set<String> permissions;
    private final Long version;

    /**
     * 构造函数：基于基本字段初始化主体
     * @param id 用户ID
     * @param username 用户名
     * @param roleIds 角色ID集合
     * @param permissions 权限集合（业务码）
     * @param version 令牌版本
     */
    public UserPrincipal(Long id, String username, Set<Long> roleIds, Set<String> permissions, Long version) {
        this.id = id;
        this.username = username;
        this.roleIds = roleIds == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(roleIds));
        this.permissions = permissions == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(permissions));
        this.version = version;
    }



    /**
     * 判断是否具备某个权限码
     * @param perm 权限码
     * @return 是否具备
     */
    public boolean hasPermission(String perm) {
        return perm != null && permissions.contains(perm);
    }

    /**
     * 判断是否具备某个角色ID
     * @param roleId 角色ID
     * @return 是否具备
     */
    public boolean hasRole(Long roleId) {
        return roleId != null && roleIds.contains(roleId);
    }
}
