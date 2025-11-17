package dev.tagtag.module.iam.convert;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.module.iam.entity.Role;
import java.util.List;

/**
 * 使用 MapStruct 的角色实体与DTO转换器
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface RoleMapperConvert {

    /** 将实体转换为DTO */
    RoleDTO toDTO(Role entity);

    /** 将DTO转换为实体 */
    Role toEntity(RoleDTO dto);

    /** 将实体列表转换为DTO列表 */
    List<RoleDTO> toDTOList(List<Role> list);

    /** 用DTO更新实体（忽略源对象中的空值） */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(RoleDTO dto, @MappingTarget Role entity);
}