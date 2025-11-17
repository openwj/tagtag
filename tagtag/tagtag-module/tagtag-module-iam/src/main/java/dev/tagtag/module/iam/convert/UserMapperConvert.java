package dev.tagtag.module.iam.convert;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.module.iam.entity.User;
import java.util.List;

/**
 * 使用 MapStruct 的用户实体与DTO转换器
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface UserMapperConvert {

    /** 将实体转换为DTO */
    @Mapping(target = "roleIds", ignore = true)
    UserDTO toDTO(User entity);

    /** 将DTO转换为实体 */
    User toEntity(UserDTO dto);

    /** 将实体列表转换为DTO列表 */
    List<UserDTO> toDTOList(List<User> list);

    /** 用DTO更新实体（忽略源对象中的空值） */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(UserDTO dto, @MappingTarget User entity);
}