package dev.tagtag.module.iam.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;
import dev.tagtag.contract.iam.dto.MenuDTO;

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

    /** 查询指定角色的菜单列表（含按钮） */
    List<MenuDTO> listMenusByRole(Long roleId);

    /** 按角色ID集合批量查询权限编码（按钮型菜单的 menu_code） */
    Set<String> listMenuCodesByRoleIds(List<Long> roleIds);

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

    /**
     * 判断角色编码是否存在
     * @param code 角色编码
     * @return 是否存在
     */
    boolean existsByCode(String code);

    /**
     * 判断角色名称是否存在
     * @param name 角色名称
     * @return 是否存在
     */
    boolean existsByName(String name);
}
