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
}
