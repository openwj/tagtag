package dev.tagtag.module.system.convert;

import dev.tagtag.contract.system.dto.DictTypeDTO;
import dev.tagtag.module.system.entity.DictType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictTypeConvert {
    DictTypeConvert INSTANCE = Mappers.getMapper(DictTypeConvert.class);

    @Mapping(source = "type", target = "code")
    DictTypeDTO toDTO(DictType entity);

    @Mapping(source = "code", target = "type")
    DictType toEntity(DictTypeDTO dto);

    List<DictTypeDTO> toDTOList(List<DictType> list);
}
