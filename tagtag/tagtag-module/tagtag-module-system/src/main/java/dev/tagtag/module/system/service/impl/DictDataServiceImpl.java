package dev.tagtag.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.constant.CacheConstants;
import dev.tagtag.common.enums.StatusEnum;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.system.dto.DictItemDTO;
import dev.tagtag.contract.system.dto.DictItemQueryDTO;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.module.system.convert.DictDataConvert;
import dev.tagtag.module.system.entity.DictData;
import dev.tagtag.module.system.mapper.DictDataMapper;
import dev.tagtag.module.system.service.DictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {


    /**
     * 字典数据分页查询
     * 根据类型、标签、状态进行筛选，状态枚举转换为整型编码
     * @param query 查询条件（允许为 null）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
    @Override
    public PageResult<DictItemDTO> page(DictItemQueryDTO query, PageQuery pageQuery) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        if (query != null) {
            if (StringUtils.hasText(query.getTypeCode())) {
                wrapper.eq(DictData::getDictType, query.getTypeCode());
            }
            if (StringUtils.hasText(query.getItemName())) {
                wrapper.like(DictData::getDictLabel, query.getItemName());
            }
            if (query.getStatus() != null) {
                wrapper.eq(DictData::getStatus, query.getStatus());
            }
        }
        wrapper.orderByAsc(DictData::getDictSort);

        IPage<DictData> page = this.page(Pages.toPage(pageQuery), wrapper);
        return PageResults.of(page.convert(DictDataConvert.INSTANCE::toDTO));
    }

    /**
     * 根据字典类型查询字典数据列表
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    @Override
    @Cacheable(value = CacheConstants.DICT, key = "#dictType")
    public List<DictItemDTO> listByDictType(String dictType) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType)
                .eq(DictData::getStatus, StatusEnum.ENABLED.getCode())
                .orderByAsc(DictData::getDictSort);
        
        List<DictData> list = this.list(wrapper);
        return DictDataConvert.INSTANCE.toDTOList(list);
    }

    /**
     * 根据ID获取字典数据
     * @param id 字典数据ID
     * @return 字典数据DTO
     */
    @Override
    public DictItemDTO getById(Long id) {
        DictData entity = super.getById(id);
        return DictDataConvert.INSTANCE.toDTO(entity);
    }

    /**
     * 保存字典数据
     * @param dto 字典数据DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.DICT, key = "#dto.typeCode")
    public void save(DictItemDTO dto) {
        DictData entity = DictDataConvert.INSTANCE.toEntity(dto);
        this.save(entity);
    }

    /**
     * 更新字典数据
     * @param dto 字典数据DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.DICT, key = "#dto.typeCode")
    public void update(DictItemDTO dto) {
        DictData entity = DictDataConvert.INSTANCE.toEntity(dto);
        this.updateById(entity);
    }

    /**
     * 删除字典数据
     * @param id 字典数据ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.DICT, allEntries = true)
    public void delete(Long id) {
        this.removeById(id);
    }

    /**
     * 批量删除字典数据
     * @param ids 字典数据 ID 列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = CacheConstants.DICT, allEntries = true)
    public void deleteBatch(List<Long> ids) {
        this.removeByIds(ids);
    }
}
