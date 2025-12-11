package dev.tagtag.module.iam.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;
import java.util.List;
import java.util.Set;

public interface MenuService {

    /** 权限分页查询 */
    PageResult<MenuDTO> page(MenuQueryDTO query, PageQuery pageQuery);

    /** 获取权限详情 */
    MenuDTO getById(Long id);

    /** 创建权限（返回新建ID） */
    Long create(MenuDTO menu);

    /** 更新权限（忽略源对象中的空值） */
    void update(MenuDTO menu);

    /** 删除权限 */
    void delete(Long id);


    /**
     * 菜单树查询（不分页）
     * @param query 查询条件
     * @return 树形菜单
     */
    List<MenuDTO> listTree(MenuQueryDTO query);

    /**
     * 查询所有有效的权限编码（按钮类型）
     * @return 权限编码集合
     */
    Set<String> listAllPermissionCodes();

    /**
     * 更新菜单状态
     * @param id 菜单ID
     * @param status 状态（0=禁用，1=启用）
     */
    void updateStatus(Long id, int status);

    /**
     * 批量更新菜单状态
     * @param ids 菜单ID列表
     * @param status 状态（0=禁用，1=启用）
     */
    void batchUpdateStatus(List<Long> ids, int status);

    /**
     * 批量删除菜单（含子菜单保护）
     * @param ids 菜单ID列表
     */
    void batchDelete(List<Long> ids);
}
