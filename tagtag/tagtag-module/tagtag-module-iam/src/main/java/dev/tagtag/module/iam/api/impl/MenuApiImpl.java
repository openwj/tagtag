package dev.tagtag.module.iam.api.impl;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.api.MenuApi;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;
import dev.tagtag.module.iam.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuApiImpl implements MenuApi {

    private final MenuService menuService;

    /** 菜单分页查询（支持过滤） */
    public Result<PageResult<MenuDTO>> listMenus(PageQuery pageQuery, MenuQueryDTO filter) {
        return Result.ok(menuService.page(filter, pageQuery));
    }

    /** 查询菜单详情 */
    public Result<MenuDTO> getMenuById(Long menuId) {
        return Result.ok(menuService.getById(menuId));
    }

    /** 创建菜单 */
    public Result<Void> createMenu(MenuDTO menu) {
        menuService.create(menu);
        return Result.ok();

    }

    /** 更新菜单 */
    public Result<Void> updateMenu(MenuDTO menu) {
        menuService.update(menu);
        return Result.ok();
    }

    /** 删除菜单 */
    public Result<Void> deleteMenu(Long menuId) {
        menuService.delete(menuId);
        return Result.ok();
    }

    /**
     * 菜单树查询（不分页）
     * @param filter 过滤条件
     * @return 树形菜单列表
     */
    @Override
    public Result<List<MenuDTO>> listMenuTree(MenuQueryDTO filter) {
        return Result.ok(menuService.listTree(filter));
    }
}