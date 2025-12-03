package dev.tagtag.module.system.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.system.dto.DictTypeDTO;
import dev.tagtag.contract.system.dto.DictTypeQueryDTO;

import java.util.List;

public interface DictTypeService {

    /**
     * 分页查询字典类型
     * @param query 查询参数
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<DictTypeDTO> page(DictTypeQueryDTO query, PageQuery pageQuery);

    /**
     * 查询所有字典类型
     * @return 列表
     */
    List<DictTypeDTO> listAll();

    /**
     * 根据ID获取字典类型
     * @param id ID
     * @return 详情
     */
    DictTypeDTO getById(Long id);

    /**
     * 新增字典类型
     * @param dto 参数
     */
    void save(DictTypeDTO dto);

    /**
     * 修改字典类型
     * @param dto 参数
     */
    void update(DictTypeDTO dto);

    /**
     * 删除字典类型
     * @param id ID
     */
    void delete(Long id);
    
    /**
     * 刷新缓存
     */
    void refreshCache();
}
