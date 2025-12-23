package dev.tagtag.module.iam.controller;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.kernel.constant.AppMessages;
import dev.tagtag.common.constant.GlobalConstants;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;
import dev.tagtag.module.iam.service.RoleService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import java.util.List;
import dev.tagtag.framework.security.annotation.RequirePerm;
import dev.tagtag.kernel.constant.Permissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(GlobalConstants.API_PREFIX + "/iam/roles")
@Tag(name = "IAM - 角色管理", description = "角色相关 API 接口")
public class RoleController {

    private final RoleService roleService;

    /** 角色分页查询 */
    @PostMapping("/page")
    @RequirePerm(Permissions.ROLE_READ)
    @Operation(summary = "角色分页查询", description = "根据条件分页查询角色列表")
    public Result<PageResult<RoleDTO>> page(@Valid @RequestBody RolePageRequest req) {
        PageResult<RoleDTO> pr = roleService.page(req.getQuery(), req.getPage());
        return Result.ok(pr);
    }

    /** 获取角色详情 */
    @GetMapping("/{id}")
    @RequirePerm(Permissions.ROLE_READ)
    @Operation(summary = "获取角色详情", description = "根据角色ID获取角色详细信息")
    public Result<RoleDTO> get(@PathVariable("id") Long id) {
        return Result.ok(roleService.getById(id));
    }

    /**
     * 根据角色编码查询角色详情
     * @param code 角色编码
     * @return 角色详情
     */
    @GetMapping("/code/{code}")
    @RequirePerm(Permissions.ROLE_READ)
    @Operation(summary = "根据编码获取角色", description = "根据角色编码查询角色详情")
    public Result<RoleDTO> getByCode(@PathVariable("code") String code) {
        return Result.ok(roleService.getByCode(code));
    }

    /**
     * 根据角色名称查询角色详情
     * @param name 角色名称
     * @return 角色详情
     */
    @GetMapping("/name/{name}")
    @RequirePerm(Permissions.ROLE_READ)
    @Operation(summary = "根据名称获取角色", description = "根据角色名称查询角色详情")
    public Result<RoleDTO> getByName(@PathVariable("name") String name) {
        return Result.ok(roleService.getByName(name));
    }

    /** 创建角色 */
    @PostMapping
    @RequirePerm(Permissions.ROLE_CREATE)
    @Operation(summary = "创建角色", description = "创建新角色")
    public Result<Void> create(@Valid @RequestBody RoleDTO role) {
        roleService.create(role);
        return Result.okMsg(AppMessages.CREATE_SUCCESS);
    }

    /** 更新角色（忽略源对象中的空值） */
    @PutMapping
    @RequirePerm(Permissions.ROLE_UPDATE)
    @Operation(summary = "更新角色", description = "更新角色信息")
    public Result<Void> update(@Valid @RequestBody RoleDTO role) {
        roleService.update(role);
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /** 删除角色 */
    @DeleteMapping("/{id}")
    @RequirePerm(Permissions.ROLE_DELETE)
    @Operation(summary = "删除角色", description = "根据角色ID删除角色")
    public Result<Void> delete(@PathVariable("id") Long id) {
        roleService.delete(id);
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /**
     * 更新角色状态
     * @param id 角色ID
     * @param req 包含 status（0=禁用，1=启用）
     * @return 操作结果
     */
    @PutMapping("/{id}/status")
    @RequirePerm(Permissions.ROLE_UPDATE)
    @Operation(summary = "更新角色状态", description = "启用或禁用角色")
    public Result<Void> updateStatus(@PathVariable("id") Long id, @RequestBody RoleStatusUpdateRequest req) {
        roleService.updateStatus(id, req.getStatus());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 批量更新角色状态
     * @param req 包含角色ID列表与禁用标记
     * @return 操作结果
     */
    @PutMapping("/status/batch")
    @RequirePerm(Permissions.ROLE_UPDATE)
    @Operation(summary = "批量更新角色状态", description = "批量启用或禁用角色")
    public Result<Void> batchUpdateStatus(@Valid @RequestBody RoleStatusBatchRequest req) {
        roleService.batchUpdateStatus(req.getIds(), req.getStatus());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /** 查询所有角色（简单列表） */
    @GetMapping
    @RequirePerm(Permissions.ROLE_READ)
    @Operation(summary = "查询所有角色", description = "查询所有角色列表")
    public Result<List<RoleDTO>> listAll() {
        return Result.ok(roleService.listAll());
    }


    /** 为角色分配菜单（覆盖式） */
    @PostMapping("/{id}/menus")
    @RequirePerm(Permissions.ROLE_ASSIGN_MENU)
    @Operation(summary = "分配菜单", description = "为角色分配菜单")
    public Result<Void> assignMenus(@PathVariable("id") Long roleId, @RequestBody List<Long> menuIds) {
        roleService.assignMenus(roleId, menuIds);
        return Result.okMsg(AppMessages.ASSIGN_SUCCESS);
    }

    /**
     * 查询角色已分配的菜单ID列表（包含目录/菜单/按钮）
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/{id}/menu-ids")
    @RequirePerm(Permissions.ROLE_READ)
    @Operation(summary = "查询角色菜单", description = "查询角色已分配的菜单ID列表")
    public Result<List<Long>> listMenuIds(@PathVariable("id") Long roleId) {
        return Result.ok(roleService.listMenuIdsByRoleId(roleId));
    }


    @Data
    public static class RolePageRequest {
        private RoleQueryDTO query;
        @Valid
        private PageQuery page;
    }

    /**
     * 角色状态更新请求
     */
    @Data
    public static class RoleStatusUpdateRequest {
        private int status;
    }

    /**
     * 角色批量状态更新请求
     */
    @Data
    public static class RoleStatusBatchRequest {
        private List<Long> ids;
        private int status;
    }
}

