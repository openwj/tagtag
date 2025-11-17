package dev.tagtag.module.iam.convert;

import org.mapstruct.BeanMapping;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.module.iam.entity.Menu;
import java.util.List;

/**
 * 使用 MapStruct 的菜单实体与DTO转换器
 */
@Mapper(
        componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface MenuMapperConvert {

    /** 将实体转换为DTO */
    MenuDTO toDTO(Menu entity);

    /** 将DTO转换为实体 */
    Menu toEntity(MenuDTO dto);

    /** 将实体列表转换为DTO列表 */
    List<MenuDTO> toDTOList(List<Menu> list);

    /** 用DTO更新实体（忽略源对象中的空值） */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDTO(MenuDTO dto, @MappingTarget Menu entity);
}