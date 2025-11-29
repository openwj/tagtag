package dev.tagtag.module.iam.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;

import java.util.List;
import java.util.Set;

public interface RoleService {

    /** 角色分页查询（支持排序白名单） */
    PageResult<RoleDTO> page(RoleQueryDTO query, PageQuery pageQuery);

    /** 获取角色详情 */
    RoleDTO getById(Long id);

    /** 创建角色 */
    void create(RoleDTO role);

    /** 更新角色（忽略源对象中的空值） */
    void update(RoleDTO role);

    /** 删除角色 */
    void delete(Long id);

    /** 为角色分配菜单（按钮权限） */
    void assignMenus(Long roleId, List<Long> menuIds);

    /** 按角色ID集合批量查询权限编码（按钮型菜单的 menu_code） */
    Set<String> listMenuCodesByRoleIds(List<Long> roleIds);

    /**
     * 查询指定角色已分配的菜单ID列表（包含目录/菜单/按钮）
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> listMenuIdsByRoleId(Long roleId);

    /**
     * 根据角色编码查询角色详情
     * @param code 角色编码
     * @return 角色详情
     */
    RoleDTO getByCode(String code);

    /**
     * 根据角色名称查询角色详情
     * @param name 角色名称
     * @return 角色详情
     */
    RoleDTO getByName(String name);


    /** 查询所有角色（简单列表） */
    List<RoleDTO> listAll();

    /**
     * 更新角色状态
     * @param id 角色ID
     * @param disabled 是否禁用（true=禁用，false=启用）
     */
    void updateStatus(Long id, boolean disabled);

    /**
     * 批量更新角色状态
     * @param ids 角色ID列表
     * @param disabled 是否禁用（true=禁用，false=启用）
     */
    void batchUpdateStatus(List<Long> ids, boolean disabled);
}
