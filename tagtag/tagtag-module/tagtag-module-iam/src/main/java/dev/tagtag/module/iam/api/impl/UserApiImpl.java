package dev.tagtag.module.iam.api.impl;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.api.UserApi;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.dto.UserQueryDTO;
import dev.tagtag.module.iam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserApiImpl implements UserApi {

    private final UserService userService;

    /** 根据用户ID查询用户信息 */
    @Override
    public Result<UserDTO> getUserById(Long userId) {
        return Result.ok(userService.getById(userId));
    }

    /** 分页查询用户列表（支持过滤） */
    @Override
    public Result<PageResult<UserDTO>> listUsers(PageQuery pageQuery, UserQueryDTO filter) {
        return Result.ok(userService.page(filter, pageQuery));
    }

    /** 创建用户 */
    @Override
    public Result<Void> createUser(UserDTO user) {
        userService.create(user);
        return Result.ok();
    }

    /** 更新用户 */
    @Override
    public Result<Void> updateUser(UserDTO user) {
        userService.update(user);
        return Result.ok();
    }

    /** 删除用户 */
    @Override
    public Result<Void> deleteUser(Long userId) {
        userService.delete(userId);
        return Result.ok();
    }

    /** 为用户分配角色 */
    @Override
    public Result<Void> assignRoles(Long userId, List<Long> roleIds) {
        userService.assignRoles(userId, roleIds);
        return Result.ok();
    }

    /** 根据用户名查询用户信息（包含密码） */
    @Override
    public Result<UserDTO> getUserByUsername(String username) {
        return Result.ok(userService.getByUsername(username));
    }
}
