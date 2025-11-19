package dev.tagtag.module.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.common.model.SortField;
import dev.tagtag.contract.iam.dto.UserQueryDTO;
import dev.tagtag.module.iam.entity.User;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    /**
     * XML 分页查询（由 MyBatis XML 构建 WHERE/ORDER BY）
     * @param page 分页对象（MyBatis Plus 拦截器识别）
     * @param q 查询条件 DTO
     * @param orderList 排序字段列表
     * @return 分页结果
     */
    IPage<User> selectPage(IPage<User> page, @Param("q") UserQueryDTO q, @Param("orderBySql") String orderBySql);

    /** 根据用户ID查询角色ID列表 */
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /** 删除用户的所有角色关联 */
    int deleteUserRoles(@Param("userId") Long userId);

    /** 批量插入用户角色关联 */
    int insertUserRoles(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    

}
