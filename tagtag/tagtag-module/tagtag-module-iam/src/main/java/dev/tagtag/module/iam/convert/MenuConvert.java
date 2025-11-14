package dev.tagtag.module.iam.convert;

import dev.tagtag.common.util.BeanUtils;
import dev.tagtag.contract.iam.dto.MenuDTO;
import dev.tagtag.module.iam.entity.Menu;

import java.util.List;

public class MenuConvert {

    /** 将实体转换为DTO */
    public static MenuDTO toDTO(Menu entity) {
        if (entity == null) return null;
        MenuDTO dto = new MenuDTO();
        BeanUtils.copyTo(entity, dto);
        return dto;
    }

    /** 将DTO转换为实体 */
    public static Menu toEntity(MenuDTO dto) {
        if (dto == null) return null;
        Menu entity = new Menu();
        BeanUtils.copyTo(dto, entity);
        return entity;
    }

    /** 将实体列表转换为DTO列表 */
    public static List<MenuDTO> toDTOList(List<Menu> list) {
        return BeanUtils.copyList(list, MenuDTO::new);
    }

    /** 用DTO更新实体（忽略源对象中的空值） */
    public static void mergeNonNull(MenuDTO dto, Menu entity) {
        if (dto == null || entity == null) return;
        BeanUtils.copyNonNullTo(dto, entity);
    }
}
