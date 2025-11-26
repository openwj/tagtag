package dev.tagtag.module.iam.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.contract.iam.dto.UserQueryDTO;
import java.util.List;

public interface UserService {

    /** 用户分页查询（支持排序白名单） */
    PageResult<UserDTO> page(UserQueryDTO query, PageQuery pageQuery);

    /** 获取用户详情 */
    UserDTO getById(Long id);

    /** 创建用户（返回创建后的主键或空） */
    void create(UserDTO user);

    /** 更新用户（忽略源对象中的空值） */
    void update(UserDTO user);

    /** 删除用户 */
    void delete(Long id);

    /** 为用户分配角色 */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 根据用户名查询用户详情（包含密码与角色ID）
     * @param username 用户名
     * @return 用户数据
     */
    UserDTO getByUsername(String username);

    /** 更新单个用户状态 */
    void updateStatus(Long id, Integer status);

    /** 批量更新用户状态 */
    void batchUpdateStatus(List<Long> ids, Integer status);

    /** 批量删除用户 */
    void batchDelete(List<Long> ids);

    /** 重置用户密码 */
    void resetPassword(Long id, String password);

    /** 查询用户已分配角色列表（DTO） */
    List<dev.tagtag.contract.iam.dto.RoleDTO> listRolesByUserId(Long userId);

    /** 批量为用户分配角色（覆盖式） */
    void assignRolesBatch(java.util.List<Long> userIds, java.util.List<Long> roleIds);
}
