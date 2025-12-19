package dev.tagtag.module.system.convert;

import dev.tagtag.contract.system.dto.DictItemDTO;
import dev.tagtag.module.system.entity.DictData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictDataConvert {
    DictDataConvert INSTANCE = Mappers.getMapper(DictDataConvert.class);

    @Mapping(source = "dictType", target = "typeCode")
    @Mapping(source = "dictLabel", target = "itemName")
    @Mapping(source = "dictValue", target = "itemCode")
    @Mapping(source = "dictSort", target = "sort")
    DictItemDTO toDTO(DictData entity);

    @Mapping(source = "typeCode", target = "dictType")
    @Mapping(source = "itemName", target = "dictLabel")
    @Mapping(source = "itemCode", target = "dictValue")
    @Mapping(source = "sort", target = "dictSort")
    DictData toEntity(DictItemDTO dto);

    List<DictItemDTO> toDTOList(List<DictData> list);
}
