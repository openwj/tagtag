package dev.tagtag.module.iam.api.impl;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.api.RoleApi;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;
import dev.tagtag.module.iam.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class RoleApiImpl implements RoleApi {

    private final RoleService roleService;

    /** 分页查询角色列表（支持过滤） */
    @Override
    public Result<PageResult<RoleDTO>> listRoles(PageQuery pageQuery, RoleQueryDTO filter) {
        return Result.ok(roleService.page(filter, pageQuery));
    }

    /** 查询角色详情 */
    @Override
    public Result<RoleDTO> getRoleById(Long roleId) {
        return Result.ok(roleService.getById(roleId));
    }

    /** 创建角色 */
    @Override
    public Result<Void> createRole(RoleDTO role) {
        roleService.create(role);
        return Result.ok();
    }

    /** 更新角色 */
    @Override
    public Result<Void> updateRole(RoleDTO role) {
        roleService.update(role);
        return Result.ok();
    }

    /** 删除角色 */
    @Override
    public Result<Void> deleteRole(Long roleId) {
        roleService.delete(roleId);
        return Result.ok();
    }


    /** 批量查询角色的权限编码集合 */
    @Override
    public Result<Set<String>> listMenuCodesByRoleIds(List<Long> roleIds) {
        return Result.ok(roleService.listMenuCodesByRoleIds(roleIds));
    }

    /**
     * 按角色ID集合查询已分配的菜单ID列表（并集、去重）
     * @param roleIds 角色ID集合
     * @return 菜单ID列表
     */
    @Override
    public Result<List<Long>> listMenuIdsByRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) return Result.ok(List.of());
        Set<Long> set = new LinkedHashSet<>();
        for (Long roleId : roleIds) {
            if (roleId == null) continue;
            List<Long> ids = roleService.listMenuIdsByRoleId(roleId);
            if (ids != null && !ids.isEmpty()) set.addAll(ids);
        }
        return Result.ok(new ArrayList<>(set));
    }
}
