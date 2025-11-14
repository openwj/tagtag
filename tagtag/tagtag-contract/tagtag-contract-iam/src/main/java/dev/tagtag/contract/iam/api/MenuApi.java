package dev.tagtag.contract.iam.api;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;

/**
 * 菜单契约接口（目录/菜单/按钮）。按钮型菜单的 menuCode 用作后端权限码
 */
public interface MenuApi {

    /**
     * 菜单分页查询（支持过滤）
     * @param pageQuery 分页参数
     * @param filter 过滤条件
     * @return 菜单分页结果
     */
    Result<PageResult<MenuDTO>> listMenus(PageQuery pageQuery, MenuQueryDTO filter);

    /**
     * 查询菜单详情
     * @param menuId 菜单ID
     * @return 菜单详情
     */
    Result<MenuDTO> getMenuById(Long menuId);

    /**
     * 创建菜单
     * @param menu 菜单数据
     * @return 操作结果
     */
    Result<Void> createMenu(MenuDTO menu);

    /**
     * 更新菜单
     * @param menu 菜单数据
     * @return 操作结果
     */
    Result<Void> updateMenu(MenuDTO menu);

    /**
     * 删除菜单
     * @param menuId 菜单ID
     * @return 操作结果
     */
    Result<Void> deleteMenu(Long menuId);
}
