package dev.tagtag.module.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.contract.iam.dto.UserQueryDTO;
import dev.tagtag.module.iam.entity.vo.UserVO;
import dev.tagtag.module.iam.entity.User;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * XML 分页查询（由 MyBatis XML 构建 WHERE/ORDER BY）
     *
     * @param page 分页对象（MyBatis Plus 拦截器识别）
     * @param q    查询条件 DTO
     *             固定排序由 XML 定义（create_time DESC, id DESC），不接受前端传入
     * @return 分页结果
     */
    IPage<UserVO> selectPage(IPage<UserVO> page, @Param("q") UserQueryDTO q);

    /**
     * 根据用户ID查询角色ID列表
     */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * 删除用户的所有角色关联
     */
    int deleteUserRoles(@Param("userId") Long userId);

    /**
     * 批量删除多个用户的角色关联
     */
    int deleteUserRolesBatch(@Param("userIds") List<Long> userIds);

    /**
     * 批量插入用户角色关联
     */
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * 批量插入多个用户的角色关联
     */
    int insertUserRolesBatch(@Param("userIds") List<Long> userIds, @Param("roleIds") List<Long> roleIds);

}
