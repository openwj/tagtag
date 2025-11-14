package dev.tagtag.module.iam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import dev.tagtag.common.model.SortField;
import dev.tagtag.contract.iam.dto.RoleQueryDTO;
import dev.tagtag.module.iam.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    /**
     * 角色分页查询（由 MyBatis XML 构建 WHERE/ORDER BY）
     * @param page 分页对象
     * @param q 查询条件
     * @param orderList 排序字段列表
     * @return 分页结果
     */
    IPage<Role> selectPage(IPage<Role> page, @Param("q") RoleQueryDTO q, @Param("orderList") List<SortField> orderList);

    /** 根据角色ID查询权限ID列表 */
    List<Long> selectPermissionIdsByRoleId(@Param("roleId") Long roleId);

    /** 删除角色的所有权限关联 */
    int deleteRolePermissions(@Param("roleId") Long roleId);

    /** 批量插入角色权限关联 */
    int insertRolePermissions(@Param("roleId") Long roleId, @Param("permissionIds") List<Long> permissionIds);
}
