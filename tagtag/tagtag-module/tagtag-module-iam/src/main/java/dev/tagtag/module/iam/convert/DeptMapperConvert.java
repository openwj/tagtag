package dev.tagtag.module.iam.convert;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import dev.tagtag.framework.mapstruct.MapStructConfig;
import dev.tagtag.contract.iam.dto.DeptDTO;
import dev.tagtag.module.iam.entity.Dept;
import java.util.List;

/**
 * 使用 MapStruct 的部门实体与DTO转换器
 */
@Mapper(config = MapStructConfig.class)
public interface DeptMapperConvert {

    /** 将实体转换为DTO */
    @Mapping(target = "children", ignore = true)
    DeptDTO toDTO(Dept entity);

    /** 将DTO转换为实体 */
    Dept toEntity(DeptDTO dto);

    /** 将实体列表转换为DTO列表 */
    List<DeptDTO> toDTOList(List<Dept> list);

    /** 用DTO更新实体（忽略源对象中的空值） */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(DeptDTO dto, @MappingTarget Dept entity);
}