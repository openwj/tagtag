package dev.tagtag.module.auth.service;

import dev.tagtag.contract.iam.api.RoleApi;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 权限解析服务：根据角色ID列表解析权限编码集合
 */
@Service
@RequiredArgsConstructor
public class PermissionResolver {

    private final RoleApi roleApi;

    /**
     * 根据角色ID列表收集权限编码集合
     * @param roleIds 角色ID列表
     * @return 权限编码集合
     */
    public Set<String> resolvePermissions(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return Collections.emptySet();
        Set<String> codes = roleApi.listMenuCodesByRoleIds(roleIds).getData();
        return codes == null ? Collections.emptySet() : codes;
    }
}