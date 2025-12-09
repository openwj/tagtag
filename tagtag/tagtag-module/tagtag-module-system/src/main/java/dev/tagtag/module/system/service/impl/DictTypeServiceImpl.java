package dev.tagtag.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import dev.tagtag.common.model.PageQuery;
import dev.tagtag.common.model.PageResult;
import dev.tagtag.contract.system.dto.DictTypeDTO;
import dev.tagtag.contract.system.dto.DictTypeQueryDTO;
import dev.tagtag.framework.util.PageResults;
import dev.tagtag.framework.util.Pages;
import dev.tagtag.module.system.entity.DictType;
import dev.tagtag.module.system.entity.DictData;
import dev.tagtag.module.system.mapper.DictDataMapper;
import dev.tagtag.module.system.mapper.DictTypeMapper;
import dev.tagtag.module.system.service.DictTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {

    private final DictDataMapper dictDataMapper;

    @Override
    public PageResult<DictTypeDTO> page(DictTypeQueryDTO query, PageQuery pageQuery) {
        LambdaQueryWrapper<DictType> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getName())) {
            wrapper.like(DictType::getName, query.getName());
        }
        if (StringUtils.hasText(query.getCode())) {
            wrapper.like(DictType::getType, query.getCode());
        }
        if (query.getStatus() != null) {
            wrapper.eq(DictType::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(DictType::getCreateTime);

        var page = this.page(Pages.toPage(pageQuery), wrapper);
        return PageResults.of(page.convert(this::toDTO));
    }

    @Override
    public List<DictTypeDTO> listAll() {
        List<DictType> list = this.list(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getStatus, 1)
                .orderByDesc(DictType::getCreateTime));
        return list.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public DictTypeDTO getById(Long id) {
        DictType entity = super.getById(id);
        return toDTO(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void save(DictTypeDTO dto) {
        DictType entity = new DictType();
        BeanUtils.copyProperties(dto, entity);
        entity.setType(dto.getCode());
        this.save(entity);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DictTypeDTO dto) {
        DictType entity = super.getById(dto.getId());
        if (entity != null) {
            BeanUtils.copyProperties(dto, entity);
            entity.setType(dto.getCode());
            this.updateById(entity);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DictType dictType = super.getById(id);
        if (dictType != null) {
            dictDataMapper.delete(new LambdaQueryWrapper<DictData>()
                    .eq(DictData::getDictType, dictType.getType()));
            this.removeById(id);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    /**
     * 批量删除字典类型（级联清理字典数据）
     * @param ids 字典类型 ID 列表
     */
    public void deleteBatch(List<Long> ids) {
        List<DictType> list = this.listByIds(ids);
        if (!list.isEmpty()) {
            List<String> types = list.stream().map(DictType::getType).collect(Collectors.toList());
            dictDataMapper.delete(new LambdaQueryWrapper<DictData>()
                    .in(DictData::getDictType, types));
            this.removeByIds(ids);
        }
    }

    /**
     * 刷新字典缓存（当前无缓存实现，为显式 no-op）
     */
    @Override
    public void refreshCache() {
        // no-op
    }

    private DictTypeDTO toDTO(DictType entity) {
        if (entity == null) {
            return null;
        }
        DictTypeDTO dto = new DictTypeDTO();
        BeanUtils.copyProperties(entity, dto);
        dto.setCode(entity.getType());
        return dto;
    }
}
