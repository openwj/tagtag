package dev.tagtag.module.iam.controller;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.module.iam.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping("/iam/roles")
public class RoleController {

    private final RoleService roleService;

    /** 角色分页查询 */
    @PostMapping("/page")
    public Result<PageResult<RoleDTO>> page(@Valid @RequestBody RolePageRequest req) {
        PageResult<RoleDTO> pr = roleService.page(req.getQuery(), req.getPage());
        return Result.ok(pr);
    }

    /** 获取角色详情 */
    @GetMapping("/{id}")
    public Result<RoleDTO> get(@PathVariable("id") Long id) {
        return Result.ok(roleService.getById(id));
    }

    /** 创建角色 */
    @PostMapping
    public Result<Void> create(@Valid @RequestBody RoleDTO role) {
        roleService.create(role);
        return Result.okMsg("创建成功");
    }

    /** 更新角色（忽略源对象中的空值） */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody RoleDTO role) {
        roleService.update(role);
        return Result.okMsg("更新成功");
    }

    /** 删除角色 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") Long id) {
        roleService.delete(id);
        return Result.okMsg("删除成功");
    }

    /** 查询角色的菜单列表（含按钮） */
    @GetMapping("/{id}/menus")
    public Result<java.util.List<MenuDTO>> listMenus(@PathVariable("id") Long roleId) {
        return Result.ok(roleService.listMenusByRole(roleId));
    }

    /** 为角色分配菜单（覆盖式） */
    @PostMapping("/{id}/menus")
    public Result<Void> assignMenus(@PathVariable("id") Long roleId, @RequestBody java.util.List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.okMsg("分配成功");
    }

    @Data
    public static class RolePageRequest {
        private RoleQueryDTO query;
        @Valid
        private PageQuery page;
    }
}
