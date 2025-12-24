package dev.tagtag.framework.security.model;

import lombok.Getter;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
public class UserPrincipal {

    private final Long id;
    private final String username;
    private final Set<Long> roleIds;
    private final Set<String> permissions;
    private final Long version;
    private final boolean isAdmin;

    public UserPrincipal(Long id, String username, Set<Long> roleIds, Set<String> permissions, Long version, boolean isAdmin) {
        this.id = id;
        this.username = username;
        this.roleIds = roleIds == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(roleIds));
        this.permissions = permissions == null ? Collections.emptySet() : Collections.unmodifiableSet(new LinkedHashSet<>(permissions));
        this.version = version;
        this.isAdmin = isAdmin;
    }

    public boolean hasPermission(String perm) {
        return perm != null && permissions.contains(perm);
    }

    public boolean hasRole(Long roleId) {
        return roleId != null && roleIds.contains(roleId);
    }
}