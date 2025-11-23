package dev.tagtag.contract.iam.api;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;

import java.util.List;
import java.util.Set;

/**
 * 角色契约接口
 */
public interface RoleApi {

    /**
     * 分页查询角色列表（支持过滤）
     * @param pageQuery 分页参数
     * @param filter 过滤条件
     * @return 角色分页结果
     */
    Result<PageResult<RoleDTO>> listRoles(PageQuery pageQuery, RoleQueryDTO filter);

    /**
     * 查询角色详情
     * @param roleId 角色ID
     * @return 角色详情
     */
    Result<RoleDTO> getRoleById(Long roleId);

    /**
     * 创建角色
     * @param role 角色数据
     * @return 操作结果
     */
    Result<Void> createRole(RoleDTO role);

    /**
     * 更新角色
     * @param role 角色数据
     * @return 操作结果
     */
    Result<Void> updateRole(RoleDTO role);

    /**
     * 删除角色
     * @param roleId 角色ID
     * @return 操作结果
     */
    Result<Void> deleteRole(Long roleId);

    /**
     * 查询指定角色的菜单（含按钮）
     * @param roleId 角色ID
     * @return 菜单列表
     */
    Result<List<MenuDTO>> listMenusByRole(Long roleId);

    /**
     * 按角色ID集合批量查询权限编码（按钮型菜单的 menu_code）
     * @param roleIds 角色ID集合
     * @return 权限编码集合
     */
    Result<Set<String>> listMenuCodesByRoleIds(List<Long> roleIds);

    /**
     * 按角色ID集合查询已分配的菜单ID列表（包含目录/菜单/按钮），返回去重后的并集
     * @param roleIds 角色ID集合
     * @return 菜单ID列表（并集，去重）
     */
    Result<List<Long>> listMenuIdsByRoleIds(List<Long> roleIds);
}
