package dev.tagtag.module.iam.convert;

import dev.tagtag.common.util.BeanUtils;
import dev.tagtag.contract.iam.dto.RoleDTO;
import dev.tagtag.module.iam.entity.Role;

import java.util.List;

public class RoleConvert {

    /** 将实体转换为DTO */
    public static RoleDTO toDTO(Role entity) {
        if (entity == null) return null;
        RoleDTO dto = new RoleDTO();
        BeanUtils.copyTo(entity, dto);
        return dto;
    }

    /** 将DTO转换为实体 */
    public static Role toEntity(RoleDTO dto) {
        if (dto == null) return null;
        Role entity = new Role();
        BeanUtils.copyTo(dto, entity);
        return entity;
    }

    /** 将实体列表转换为DTO列表 */
    public static List<RoleDTO> toDTOList(List<Role> list) {
        return BeanUtils.copyList(list, RoleDTO::new);
    }

    /** 用DTO更新实体（忽略源对象中的空值） */
    public static void mergeNonNull(RoleDTO dto, Role entity) {
        if (dto == null || entity == null) return;
        BeanUtils.copyNonNullTo(dto, entity);
    }
}
