package dev.tagtag.module.iam.controller;

import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.module.iam.service.UserService;
import dev.tagtag.common.constant.GlobalConstants;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import dev.tagtag.contract.iam.dto.UserOperationRequest;
import dev.tagtag.contract.iam.dto.UserPageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import dev.tagtag.kernel.constant.AppMessages;
import dev.tagtag.kernel.constant.Permissions;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(GlobalConstants.API_PREFIX + "/iam/users")
public class UserController {


    private final UserService userService;

    /**
     * 用户分页查询接口（请求体包含查询条件与分页参数）
     *
     * @param req 包含 UserQueryDTO 与 PageQuery 的请求对象
     * @return 分页结果
     */
    @PostMapping("/page")
    @PreAuthorize("hasAuthority('" + Permissions.USER_READ + "')")
    public Result<PageResult<UserDTO>> page(@Valid @RequestBody UserPageRequest req) {
        PageResult<UserDTO> pr = userService.page(req.getQuery(), req.getPage());
        return Result.ok(pr);
    }

    /** 获取用户详情 */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.USER_READ + "')")
    public Result<UserDTO> get(@PathVariable("id") Long id) {
        return Result.ok(userService.getById(id));
    }

    /** 创建用户 */
    @PostMapping
    @PreAuthorize("hasAuthority('" + Permissions.USER_CREATE + "')")
    public Result<Void> create(@Valid @RequestBody UserDTO user) {
        userService.create(user);
        return Result.okMsg(AppMessages.CREATE_SUCCESS);
    }

    /** 更新用户（忽略源对象中的空值） */
    @PutMapping
    @PreAuthorize("hasAuthority('" + Permissions.USER_UPDATE + "')")
    public Result<Void> update(@Valid @RequestBody UserDTO user) {
        userService.update(user);
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /** 删除用户 */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('" + Permissions.USER_DELETE + "')")
    public Result<Void> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /** 为用户分配角色（覆盖式分配） */
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('" + Permissions.USER_ASSIGN_ROLE + "')")
    public Result<Void> assignRoles(@PathVariable("id") Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.okMsg(AppMessages.ASSIGN_SUCCESS);
    }

    /**
     * 更新单个用户状态
     * @param id 用户ID
     * @param req 请求体，包含 status（0=禁用,1=启用）
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAuthority('" + Permissions.USER_UPDATE + "')")
    public Result<Void> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody UserOperationRequest req) {
        userService.updateStatus(id, req.getStatus());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 批量更新用户状态
     * @param req 请求体，包含 ids 与 status
     */
    @PutMapping("/status/batch")
    @PreAuthorize("hasAuthority('" + Permissions.USER_UPDATE + "')")
    public Result<Void> batchUpdateStatus(@Valid @RequestBody UserOperationRequest req) {
        userService.batchUpdateStatus(req.getIds(), req.getStatus());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 批量删除用户
     * @param req 请求体，包含 ids
     */
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('" + Permissions.USER_DELETE + "')")
    public Result<Void> batchDelete(@Valid @RequestBody UserOperationRequest req) {
        userService.batchDelete(req.getIds());
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /**
     * 重置用户密码
     * @param id 用户ID
     * @param req 请求体，包含 password
     */
    @PutMapping("/{id}/password")
    @PreAuthorize("hasAuthority('" + Permissions.USER_UPDATE + "')")
    public Result<Void> resetPassword(@PathVariable("id") Long id, @Valid @RequestBody UserOperationRequest req) {
        userService.resetPassword(id, req.getPassword());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 查询用户已分配的角色列表
     * @param id 用户ID
     * @return 角色列表
     */
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('" + Permissions.USER_READ + "')")
    public Result<List<RoleDTO>> listUserRoles(@PathVariable("id") Long id) {
        return Result.ok(userService.listRolesByUserId(id));
    }

    /**
     * 批量为用户分配角色（覆盖式）
     * @param req 请求体，包含 userIds 与 roleIds
     */
    @PostMapping("/roles/batch")
    @PreAuthorize("hasAuthority('" + Permissions.USER_ASSIGN_ROLE + "')")
    public Result<Void> batchAssignRoles(@Valid @RequestBody UserOperationRequest req) {
        userService.assignRolesBatch(req.getUserIds(), req.getRoleIds());
        return Result.okMsg(AppMessages.ASSIGN_SUCCESS);
    }

    
}
