package dev.tagtag.module.iam.controller;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.kernel.constant.AppMessages;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.module.iam.service.RoleService;
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
@RequestMapping(GlobalConstants.API_PREFIX + "/iam/roles")
public class RoleController {

    private final RoleService roleService;

    /** 角色分页查询 */
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('" + Permissions.ROLE_READ + "')")
    public Result<PageResult<RoleDTO>> page(@Valid @RequestBody RolePageRequest req) {
        PageResult<RoleDTO> pr = roleService.page(req.getQuery(), req.getPage());
        return Result.ok(pr);
    }

    /** 获取角色详情 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.ROLE_READ + "')")
    public Result<RoleDTO> get(@PathVariable("id") Long id) {
        return Result.ok(roleService.getById(id));
    }

    /**
     * 根据角色编码查询角色详情
     * @param code 角色编码
     * @return 角色详情
     */
    @GetMapping("/code/{code}")
    @PreAuthorize("hasAuthority('" + Permissions.ROLE_READ + "')")
    public Result<RoleDTO> getByCode(@PathVariable("code") String code) {
        return Result.ok(roleService.getByCode(code));
    }

    /**
     * 根据角色名称查询角色详情
     * @param name 角色名称
     * @return 角色详情
     */
    @GetMapping("/name/{name}")
    @PreAuthorize("hasAuthority('" + Permissions.ROLE_READ + "')")
    public Result<RoleDTO> getByName(@PathVariable("name") String name) {
        return Result.ok(roleService.getByName(name));
    }

    /** 创建角色 */
    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.ROLE_CREATE + "')")
    public Result<Void> create(@Valid @RequestBody RoleDTO role) {
        roleService.create(role);
        return Result.okMsg(AppMessages.CREATE_SUCCESS);
    }

    /** 更新角色（忽略源对象中的空值） */
    @PutMapping
    @PreAuthorize("hasAuthority('" + Permissions.ROLE_UPDATE + "')")
    public Result<Void> update(@Valid @RequestBody RoleDTO role) {
        roleService.update(role);
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /** 删除角色 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.ROLE_DELETE + "')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        roleService.delete(id);
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /** 查询角色的菜单列表（含按钮） */
    @GetMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('" + Permissions.ROLE_READ + "')")
    public Result<List<MenuDTO>> listMenus(@PathVariable("id") Long roleId) {
        return Result.ok(roleService.listMenusByRole(roleId));
    }

    /** 为角色分配菜单（覆盖式） */
    @PostMapping("/{id}/menus")
    @PreAuthorize("hasAuthority('" + Permissions.ROLE_ASSIGN_MENU + "')")
    public Result<Void> assignMenus(@PathVariable("id") Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.okMsg(AppMessages.ASSIGN_SUCCESS);
    }

    /**
     * 判断角色编码是否存在
     * @param code 角色编码
     * @return 是否存在
     */
    @GetMapping("/exist/code/{code}")
    public Result<Boolean> existsByCode(@PathVariable("code") String code) {
        return Result.ok(roleService.existsByCode(code));
    }

    /**
     * 判断角色名称是否存在
     * @param name 角色名称
     * @return 是否存在
     */
    @GetMapping("/exist/name/{name}")
    public Result<Boolean> existsByName(@PathVariable("name") String name) {
        return Result.ok(roleService.existsByName(name));
    }

    @Data
    public static class RolePageRequest {
        private RoleQueryDTO query;
        @Valid
        private PageQuery page;
    }
}
