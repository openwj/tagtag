package dev.tagtag.module.iam.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.iam.dto.DeptDTO;
import dev.tagtag.contract.iam.dto.DeptQueryDTO;

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
    java.util.List<DeptDTO> listTree();
}
