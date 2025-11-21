package dev.tagtag.module.iam.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;
import java.util.List;

public interface MenuService {

    /** 权限分页查询（支持排序白名单） */
    PageResult<MenuDTO> page(MenuQueryDTO query, PageQuery pageQuery);

    /** 获取权限详情 */
    MenuDTO getById(Long id);

    /** 创建权限 */
    void create(MenuDTO menu);

    /** 更新权限（忽略源对象中的空值） */
    void update(MenuDTO menu);

    /** 删除权限 */
    void delete(Long id);

    /**
     * 根据父ID查询子菜单列表
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    List<MenuDTO> listByParentId(Long parentId);

    /**
     * 根据菜单编码查询单条
     * @param menuCode 菜单编码
     * @return 菜单详情
     */
    MenuDTO getByMenuCode(String menuCode);

    /**
     * 菜单树查询（不分页）
     * @param query 查询条件
     * @return 树形菜单
     */
    List<MenuDTO> listTree(MenuQueryDTO query);

    /**
     * 判断菜单编码是否存在
     * @param menuCode 菜单编码
     * @return 是否存在
     */
    boolean existsByCode(String menuCode);

    /**
     * 更新菜单状态
     * @param id 菜单ID
     * @param disabled 是否禁用（true=禁用，false=启用）
     */
    void updateStatus(Long id, boolean disabled);

    /**
     * 批量更新菜单状态
     * @param ids 菜单ID列表
     * @param disabled 是否禁用（true=禁用，false=启用）
     */
    void batchUpdateStatus(java.util.List<Long> ids, boolean disabled);

    /**
     * 批量删除菜单（含子菜单保护）
     * @param ids 菜单ID列表
     */
    void batchDelete(java.util.List<Long> ids);
}
