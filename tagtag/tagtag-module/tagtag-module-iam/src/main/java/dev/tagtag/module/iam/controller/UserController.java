package dev.tagtag.module.iam.controller;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.dto.UserQueryDTO;
import dev.tagtag.module.iam.service.UserService;
import dev.tagtag.common.constant.GlobalConstants;
import lombok.AllArgsConstructor;
import lombok.Data;
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
import org.springframework.security.access.prepost.PreAuthorize;
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
    public Result<Void> assignRoles(@PathVariable("id") Long id, @RequestBody java.util.List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.okMsg(AppMessages.ASSIGN_SUCCESS);
    }

    @Data
    public static class UserPageRequest {
        private UserQueryDTO query;
        @Valid
        private PageQuery page;
    }
}
