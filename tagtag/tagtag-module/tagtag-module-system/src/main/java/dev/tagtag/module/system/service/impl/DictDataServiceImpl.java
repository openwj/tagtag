package dev.tagtag.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.system.dto.DictItemDTO;
import dev.tagtag.contract.system.dto.DictItemQueryDTO;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.module.system.entity.DictData;
import dev.tagtag.module.system.mapper.DictDataMapper;
import dev.tagtag.module.system.service.DictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {


    @Override
    /**
     * 字典数据分页查询
     * 根据类型、标签、状态进行筛选，状态枚举转换为整型编码
     * @param query 查询条件（允许为 null）
     * @param pageQuery 分页参数
     * @return 分页结果
     */
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
        return PageResults.of(page.convert(this::toDTO));
    }

    @Override
    public List<DictItemDTO> listByDictType(String dictType) {
        LambdaQueryWrapper<DictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DictData::getDictType, dictType)
                .eq(DictData::getStatus, 1)
                .orderByAsc(DictData::getDictSort);
        
        List<DictData> list = this.list(wrapper);
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public DictItemDTO getById(Long id) {
        DictData entity = super.getById(id);
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DictItemDTO dto) {
        DictData entity = new DictData();
        BeanUtils.copyProperties(dto, entity);
        entity.setDictType(dto.getTypeCode());
        entity.setDictLabel(dto.getItemName());
        entity.setDictValue(dto.getItemCode());
        entity.setDictSort(dto.getSort());
        this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DictItemDTO dto) {
        DictData entity = super.getById(dto.getId());
        if (entity != null) {
            BeanUtils.copyProperties(dto, entity);
            entity.setDictType(dto.getTypeCode());
            entity.setDictLabel(dto.getItemName());
            entity.setDictValue(dto.getItemCode());
            entity.setDictSort(dto.getSort());
            this.updateById(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        this.removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    /**
     * 批量删除字典数据
     * @param ids 字典数据 ID 列表
     */
    public void deleteBatch(List<Long> ids) {
        this.removeByIds(ids);
    }

    private DictItemDTO toDTO(DictData entity) {
        if (entity == null) {
            return null;
        }
        DictItemDTO dto = new DictItemDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setTypeCode(entity.getDictType());
        dto.setItemName(entity.getDictLabel());
        dto.setItemCode(entity.getDictValue());
        dto.setSort(entity.getDictSort());
        return dto;
    }
}
