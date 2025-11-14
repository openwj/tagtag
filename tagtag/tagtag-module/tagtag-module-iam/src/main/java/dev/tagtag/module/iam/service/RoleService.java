package dev.tagtag.module.iam.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;
import dev.tagtag.contract.iam.dto.MenuDTO;

import java.util.List;

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
}
