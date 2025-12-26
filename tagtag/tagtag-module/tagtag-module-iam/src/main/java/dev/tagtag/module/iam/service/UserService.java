package dev.tagtag.module.iam.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.dto.UserQueryDTO;
import java.util.List;

public interface UserService {

    /**
     * 用户分页查询
     * @param query 查询条件DTO
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<UserDTO> page(UserQueryDTO query, PageQuery pageQuery);

    /**
     * 获取用户详情
     * @param id 用户ID
     * @return 用户DTO
     */
    UserDTO getById(Long id);

    /**
     * 创建用户（返回创建后的主键或空）
     * @param user 用户DTO
     */
    void create(UserDTO user);

    /**
     * 更新用户（忽略源对象中的空值）
     * @param user 用户DTO
     */
    void update(UserDTO user);

    /**
     * 删除用户
     * @param id 用户ID
     */
    void delete(Long id);

    /**
     * 为用户分配角色
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 根据用户名查询用户详情（包含密码与角色ID）
     * @param username 用户名
     * @return 用户数据
     */
    UserDTO getByUsername(String username);

    /**
     * 更新单个用户状态
     * @param id 用户ID
     * @param status 状态值
     */
    void updateStatus(Long id, Integer status);

    /**
     * 批量更新用户状态
     * @param ids 用户ID列表
     * @param status 状态值
     */
    void batchUpdateStatus(List<Long> ids, Integer status);

    /**
     * 批量删除用户
     * @param ids 用户ID列表
     */
    void batchDelete(List<Long> ids);

    /**
     * 重置用户密码
     * @param id 用户ID
     * @param password 新密码
     */
    void resetPassword(Long id, String password);

    /**
     * 查询用户已分配角色列表（DTO）
     * @param userId 用户ID
     * @return 角色DTO列表
     */
    List<dev.tagtag.contract.iam.dto.RoleDTO> listRolesByUserId(Long userId);

    /**
     * 批量为用户分配角色（覆盖式）
     * @param userIds 用户ID列表
     * @param roleIds 角色ID列表
     */
    void assignRolesBatch(List<Long> userIds, List<Long> roleIds);

    /**
     * 修改用户密码（校验旧密码）
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
