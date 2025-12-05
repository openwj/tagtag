package dev.tagtag.module.system.service;

import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.system.dto.DictItemDTO;
import dev.tagtag.contract.system.dto.DictItemQueryDTO;

import java.util.List;

public interface DictDataService {

    /**
     * 分页查询字典数据
     * @param query 查询参数
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    PageResult<DictItemDTO> page(DictItemQueryDTO query, PageQuery pageQuery);

    /**
     * 根据字典类型查询字典数据
     * @param dictType 字典类型
     * @return 列表
     */
    List<DictItemDTO> listByDictType(String dictType);

    /**
     * 根据ID获取字典数据
     * @param id ID
     * @return 详情
     */
    DictItemDTO getById(Long id);

    /**
     * 新增字典数据
     * @param dto 参数
     */
    void save(DictItemDTO dto);

    /**
     * 修改字典数据
     * @param dto 参数
     */
    void update(DictItemDTO dto);

    /**
     * 删除字典数据
     * @param id ID
     */
    void delete(Long id);

    /**
     * 批量删除字典数据
     * @param ids ID列表
     */
    void deleteBatch(List<Long> ids);
}
