package dev.tagtag.module.iam.controller;

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
import dev.tagtag.framework.security.RequirePerm;
import dev.tagtag.kernel.constant.Permissions;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(GlobalConstants.API_PREFIX + "/iam/menus")
public class MenuController {

    private final MenuService menuService;


    /**
     * 菜单树查询（不分页）
     * @param query 菜单过滤条件
     */
    @GetMapping("/tree")
    @RequirePerm(Permissions.MENU_READ)
    public Result<List<MenuDTO>> tree(MenuQueryDTO query) {
        return Result.ok(menuService.listTree(query));
    }

    /** 获取菜单详情 */
    @GetMapping("/{id}")
    @RequirePerm(Permissions.MENU_READ)
    public Result<MenuDTO> get(@PathVariable("id") Long id) {
        return Result.ok(menuService.getById(id));
    }


    /** 创建菜单 */
    @PostMapping
    @RequirePerm(Permissions.MENU_CREATE)
    public Result<Long> create(@Valid @RequestBody MenuDTO menu) {
        Long id = menuService.create(menu);
        return Result.ok(id);
    }

    /** 更新菜单（忽略源对象中的空值） */
    @PutMapping
    @RequirePerm(Permissions.MENU_UPDATE)
    public Result<Void> update(@Valid @RequestBody MenuDTO menu) {
        menuService.update(menu);
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /** 删除菜单 */
    @DeleteMapping("/{id}")
    @RequirePerm(Permissions.MENU_DELETE)
    public Result<Void> delete(@PathVariable("id") Long id) {
        menuService.delete(id);
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /**
     * 更新菜单状态
     * @param id 菜单ID
     * @param req 包含 status（0=禁用，1=启用）
     */
    @PutMapping("/{id}/status")
    @RequirePerm(Permissions.MENU_UPDATE)
    public Result<Void> updateStatus(@PathVariable("id") Long id, @RequestBody MenuStatusUpdateRequest req) {
        menuService.updateStatus(id, req.getStatus());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 批量更新菜单状态
     * @param req 包含 id 列表与禁用标记
     */
    @PutMapping("/status/batch")
    @RequirePerm(Permissions.MENU_UPDATE)
    public Result<Void> batchUpdateStatus(@RequestBody MenuStatusBatchRequest req) {
        menuService.batchUpdateStatus(req.getIds(), req.getStatus());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 批量删除菜单
     * @param req 包含 id 列表
     */
    @DeleteMapping("/batch")
    @RequirePerm(Permissions.MENU_DELETE)
    public Result<Void> batchDelete(@RequestBody MenuBatchDeleteRequest req) {
        menuService.batchDelete(req.getIds());
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }



    @Data
    public static class MenuStatusUpdateRequest {
        private int status;
    }

    @Data
    public static class MenuStatusBatchRequest {
        private List<Long> ids;
        private int status;
    }

    @Data
    public static class MenuBatchDeleteRequest {
        private List<Long> ids;
    }
}
