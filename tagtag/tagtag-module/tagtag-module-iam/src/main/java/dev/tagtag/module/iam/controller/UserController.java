package dev.tagtag.module.iam.controller;

import dev.tagtag.common.model.PageRequest;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.kernel.context.AuthContext;
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
import dev.tagtag.contract.iam.dto.ChangePasswordRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import dev.tagtag.contract.iam.dto.UserQueryDTO;
import dev.tagtag.kernel.annotation.RequirePerm;

import java.util.List;

import dev.tagtag.kernel.constant.AppMessages;
import dev.tagtag.kernel.constant.Permissions;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Validated
@AllArgsConstructor
@RequestMapping(GlobalConstants.API_PREFIX + "/iam/users")
@Tag(name = "IAM - 用户管理", description = "用户相关 API 接口")
public class UserController {


    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户分页查询接口（通用分页请求体）
     *
     * @param req 通用分页请求体，包含查询条件与分页参数
     * @return 分页结果
     */
    @PostMapping("/page")
    @RequirePerm(Permissions.USER_READ)
    @Operation(summary = "用户分页查询", description = "根据条件分页查询用户列表")
    public Result<PageResult<UserDTO>> page(@Valid @RequestBody PageRequest<UserQueryDTO> req) {
        PageResult<UserDTO> pr = userService.page(req.query(), req.page());
        return Result.ok(pr);
    }

    /**
     * 获取用户详情
     * @param id 用户ID
     * @return 用户详情DTO
     */
    @GetMapping("/{id}")
    @RequirePerm(Permissions.USER_READ)
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    public Result<UserDTO> get(@PathVariable Long id) {
        return Result.ok(userService.getById(id));
    }

    /**
     * 创建用户
     * @param user 用户DTO
     * @return 空
     */
    @PostMapping
    @RequirePerm(Permissions.USER_CREATE)
    @Operation(summary = "创建用户", description = "创建新用户")
    public Result<Void> create(@Valid @RequestBody UserDTO user) {
        userService.create(user);
        return Result.okMsg(AppMessages.CREATE_SUCCESS);
    }

    /**
     * 更新用户（忽略源对象中的空值）
     * @param user 用户DTO
     * @return 空
     */
    @PutMapping
    @RequirePerm(Permissions.USER_UPDATE)
    @Operation(summary = "更新用户", description = "更新用户信息")
    public Result<Void> update(@Valid @RequestBody UserDTO user) {
        userService.update(user);
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 本人更新基础信息（头像/昵称/邮箱/电话/性别/生日/备注）
     * 从会话中获取当前用户ID，仅更新非空字段；返回最新用户信息避免前端再拉取。
     * @param user 用户DTO
     * @return 更新后的用户DTO
     */
    @PutMapping("/me")
    @Operation(summary = "更新个人信息", description = "当前用户更新自己的基础信息")
    public Result<UserDTO> updateMe(@Valid @RequestBody UserDTO user) {
        Long uid = AuthContext.getCurrentUserId();
        user.setId(uid);
        userService.update(user);
        UserDTO fresh = userService.getById(uid);
        if (fresh != null) fresh.setPassword(null);
        return Result.ok(fresh);
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 空
     */
    @DeleteMapping("/{id}")
    @RequirePerm(Permissions.USER_DELETE)
    @Operation(summary = "删除用户", description = "根据用户ID删除用户")
    public Result<Void> delete(@PathVariable("id") Long id) {
        userService.delete(id);
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /**
     * 为用户分配角色（覆盖式分配）
     * @param id 用户ID
     * @param roleIds 角色ID列表
     * @return 空
     */
    @PostMapping("/{id}/roles")
    @RequirePerm(Permissions.USER_ASSIGN_ROLE)
    @Operation(summary = "分配角色", description = "为用户分配角色")
    public Result<Void> assignRoles(@PathVariable("id") Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.okMsg(AppMessages.ASSIGN_SUCCESS);
    }

    /**
     * 更新单个用户状态
     *
     * @param id  用户ID
     * @param req 请求体，包含 status（0=禁用,1=启用）
     */
    @PutMapping("/{id}/status")
    @RequirePerm(Permissions.USER_UPDATE)
    @Operation(summary = "更新用户状态", description = "启用或禁用用户")
    public Result<Void> updateStatus(@PathVariable("id") Long id, @Valid @RequestBody UserOperationRequest req) {
        userService.updateStatus(id, req.getStatus());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 批量更新用户状态
     *
     * @param req 请求体，包含 ids 与 status
     */
    @PutMapping("/status/batch")
    @RequirePerm(Permissions.USER_UPDATE)
    @Operation(summary = "批量更新用户状态", description = "批量启用或禁用用户")
    public Result<Void> batchUpdateStatus(@Valid @RequestBody UserOperationRequest req) {
        userService.batchUpdateStatus(req.getIds(), req.getStatus());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 批量删除用户
     *
     * @param req 请求体，包含 ids
     */
    @DeleteMapping("/batch")
    @RequirePerm(Permissions.USER_DELETE)
    @Operation(summary = "批量删除用户", description = "批量删除用户")
    public Result<Void> batchDelete(@Valid @RequestBody UserOperationRequest req) {
        userService.batchDelete(req.getIds());
        return Result.okMsg(AppMessages.DELETE_SUCCESS);
    }

    /**
     * 重置用户密码
     *
     * @param id  piID
     * @param req 请求体，包含 password
     */
    @PutMapping("/{id}/password")
    @RequirePerm(Permissions.USER_UPDATE)
    @Operation(summary = "重置密码", description = "重置用户密码")
    public Result<Void> resetPassword(@PathVariable Long id, @Valid @RequestBody UserOperationRequest req) {
        userService.resetPassword(id, req.getPassword());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 本人修改密码（校验旧密码）
     *
     * @param req 请求体，包含 oldPassword 与 newPassword
     */
    @PutMapping("/me/password")
    @Operation(summary = "修改密码", description = "当前用户修改自己的密码")
    public Result<Void> changeMyPassword(@Valid @RequestBody ChangePasswordRequest req) {
        Long uid = AuthContext.getCurrentUserId();
        UserDTO me = userService.getById(uid);
        boolean matched = passwordEncoder.matches(req.getOldPassword(), me.getPassword());
        if (!matched) {
            return Result.unauthorized("旧密码不正确");
        }
        userService.resetPassword(uid, req.getNewPassword());
        return Result.okMsg(AppMessages.UPDATE_SUCCESS);
    }

    /**
     * 查询用户已分配的角色列表
     *
     * @param id piID
     * @return 角色列表
     */
    @GetMapping("/{id}/roles")
    @RequirePerm(Permissions.USER_READ)
    @Operation(summary = "查询用户角色", description = "查询用户已分配的角色列表")
    public Result<List<RoleDTO>> listUserRoles(@PathVariable Long id) {
        return Result.ok(userService.listRolesByUserId(id));
    }

    /**
     * 批量为用户分配角色（覆盖式）
     *
     * @param req 请求体，包含 userIds 与 roleIds
     */
    @PostMapping("/roles/batch")
    @RequirePerm(Permissions.USER_ASSIGN_ROLE)
    @Operation(summary = "批量分配角色", description = "批量为用户分配角色")
    public Result<Void> batchAssignRoles(@Valid @RequestBody UserOperationRequest req) {
        userService.assignRolesBatch(req.getUserIds(), req.getRoleIds());
        return Result.okMsg(AppMessages.ASSIGN_SUCCESS);
    }


}
