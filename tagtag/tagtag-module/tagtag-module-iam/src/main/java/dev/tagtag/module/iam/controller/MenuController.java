package dev.tagtag.module.iam.controller;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.kernel.constant.AppMessages;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.contract.iam.dto.MenuQueryDTO;
import dev.tagtag.module.iam.service.MenuService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import dev.tagtag.kernel.constant.Permissions;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(GlobalConstants.API_PREFIX + "/iam/menus")
public class MenuController {

    private final MenuService menuService;

    /** 菜单分页查询 */
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('" + Permissions.MENU_READ + "')")
    public Result<PageResult<MenuDTO>> page(@Valid @RequestBody MenuPageRequest req) {
        PageResult<MenuDTO> pr = menuService.page(req.getQuery(), req.getPage());
        return Result.ok(pr);
    }

    /**
     * 菜单树查询（不分页）
     * @param query 菜单过滤条件
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('" + Permissions.MENU_READ + "')")
    public Result<List<MenuDTO>> tree(MenuQueryDTO query) {
        return Result.ok(menuService.listTree(query));
    }

    /** 获取菜单详情 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.MENU_READ + "')")
    public Result<MenuDTO> get(@PathVariable("id") Long id) {
        return Result.ok(menuService.getById(id));
    }

    /**
     * 根据父ID查询子菜单列表
     * @param parentId 父菜单ID
     * @return 子菜单列表
     */
    @GetMapping("/parent/{parentId}")
    @PreAuthorize("hasAuthority('" + Permissions.MENU_READ + "')")
    public Result<List<MenuDTO>> listByParent(@PathVariable("parentId") Long parentId) {
        return Result.ok(menuService.listByParentId(parentId));
    }

    /**
     * 根据菜单编码查询单条
     * @param menuCode 菜单编码
     * @return 菜单详情
     */
    @GetMapping("/code/{menuCode}")
    @PreAuthorize("hasAuthority('" + Permissions.MENU_READ + "')")
    public Result<MenuDTO> getByCode(@PathVariable("menuCode") String menuCode) {
        return Result.ok(menuService.getByMenuCode(menuCode));
    }

    /** 创建菜单 */
    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.MENU_CREATE + "')")
    public Result<Void> create(@Valid @RequestBody MenuDTO menu) {
        menuService.create(menu);
        return Result.okMsg(AppMessages.CREATE_SUCCESS);
    }

    /** 更新菜单（忽略源对象中的空值） */
    @PutMapping
    @PreAuthorize("hasAuthority('" + Permissions.MENU_UPDATE + "')")
    public Result<Void> update(@Valid @RequestBody MenuDTO menu) {
        menuService.update(menu);
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /** 删除菜单 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.MENU_DELETE + "')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        menuService.delete(id);
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /**
     * 更新菜单状态
     * @param id 菜单ID
     * @param disabled 是否禁用（true=禁用，false=启用）
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('" + Permissions.MENU_UPDATE + "')")
    public Result<Void> updateStatus(@PathVariable("id") Long id, @RequestParam("disabled") boolean disabled) {
        menuService.updateStatus(id, disabled);
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 批量更新菜单状态
     * @param req 包含 id 列表与禁用标记
     */
    @PutMapping("/status/batch")
    @PreAuthorize("hasAuthority('" + Permissions.MENU_UPDATE + "')")
    public Result<Void> batchUpdateStatus(@RequestBody MenuStatusBatchRequest req) {
        menuService.batchUpdateStatus(req.getIds(), req.isDisabled());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 批量删除菜单
     * @param req 包含 id 列表
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('" + Permissions.MENU_DELETE + "')")
    public Result<Void> batchDelete(@RequestBody MenuBatchDeleteRequest req) {
        menuService.batchDelete(req.getIds());
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /**
     * 判断菜单编码是否存在（唯一性校验）
     * @param menuCode 菜单编码
     * @return 是否存在
     */
    @GetMapping("/exist/code/{menuCode}")
    @PreAuthorize("hasAuthority('" + Permissions.MENU_READ + "')")
    public Result<Boolean> existsByCode(@PathVariable("menuCode") String menuCode) {
        return Result.ok(menuService.existsByCode(menuCode));
    }

    @Data
    public static class MenuPageRequest {
        private MenuQueryDTO query;
        @Valid
        private PageQuery page;
    }

    @Data
    public static class MenuStatusBatchRequest {
        private List<Long> ids;
        private boolean disabled;
    }

    @Data
    public static class MenuBatchDeleteRequest {
        private List<Long> ids;
    }
}
