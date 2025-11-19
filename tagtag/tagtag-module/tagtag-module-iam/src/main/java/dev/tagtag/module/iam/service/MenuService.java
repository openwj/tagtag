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
}
