package dev.tagtag.module.iam.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.iam.dto.DeptDTO;
import dev.tagtag.contract.iam.dto.DeptQueryDTO;
import java.util.List;

public interface DeptService {

    /** 部门分页查询 */
    PageResult<DeptDTO> page(DeptQueryDTO query, PageQuery pageQuery);

    /** 获取部门详情 */
    DeptDTO getById(Long id);

    /** 创建部门 */
    void create(DeptDTO dept);

    /** 更新部门（忽略源对象中的空值） */
    void update(DeptDTO dept);

    /** 删除部门 */
    void delete(Long id);

    /** 部门树列表 */
    List<DeptDTO> listTree();

    /**
     * 部门树列表（支持查询条件）
     * @param query 查询条件：名称模糊、状态精确、父部门精确
     * @return 部门树（按 sort、id 升序）
     */
    List<DeptDTO> listTree(DeptQueryDTO query);

    /**
     * 判断指定部门是否存在子部门
     * @param deptId 部门ID
     * @return 是否存在子部门
     */
    boolean hasChildren(Long deptId);

    /**
     * 判断指定部门是否存在用户
     * @param deptId 部门ID
     * @return 是否存在用户
     */
    boolean hasUsers(Long deptId);

    /**
     * 检查部门编码是否占用
     * @param code 部门编码
     * @param excludeId 可选排除的部门ID
     * @return 是否已占用
     */
    boolean existsByCode(String code, Long excludeId);

    /**
     * 更新部门状态
     * @param id 部门ID
     * @param status 状态（0=禁用，1=启用）
     */
    void updateStatus(Long id, int status);

    /**
     * 批量更新部门状态
     * @param ids 部门ID列表
     * @param status 状态（0=禁用，1=启用）
     */
    void batchUpdateStatus(List<Long> ids, int status);
}
