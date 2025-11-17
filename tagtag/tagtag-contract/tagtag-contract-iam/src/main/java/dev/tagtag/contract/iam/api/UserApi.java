package dev.tagtag.contract.iam.api;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.common.model.Result;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.dto.UserQueryDTO;

import java.util.List;

/**
 * 用户契约接口
 */
public interface UserApi {

    /**
     * 根据用户ID查询用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    Result<UserDTO> getUserById(Long userId);

    /**
     * 分页查询用户列表（支持过滤）
     * @param pageQuery 分页参数
     * @param filter 过滤条件
     * @return 用户分页结果
     */
    Result<PageResult<UserDTO>> listUsers(PageQuery pageQuery, UserQueryDTO filter);

    /**
     * 创建用户
     * @param user 用户数据
     * @return 操作结果
     */
    Result<Void> createUser(UserDTO user);

    /**
     * 更新用户
     * @param user 用户数据
     * @return 操作结果
     */
    Result<Void> updateUser(UserDTO user);

    /**
     * 删除用户
     * @param userId 用户ID
     * @return 操作结果
     */
    Result<Void> deleteUser(Long userId);

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID集合
     * @return 操作结果
     */
    Result<Void> assignRoles(Long userId, List<Long> roleIds);

    /**
     * 根据用户名查询用户信息（包含密码）
     * @param username 用户名
     * @return 用户信息
     */
    Result<UserDTO> getUserByUsername(String username);
}
