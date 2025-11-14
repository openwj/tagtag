package dev.tagtag.module.iam.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;

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
}
