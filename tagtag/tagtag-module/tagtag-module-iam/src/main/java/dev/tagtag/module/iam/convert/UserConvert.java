package dev.tagtag.module.iam.convert;

import dev.tagtag.common.util.BeanUtils;
import dev.tagtag.contract.iam.dto.UserDTO;
import dev.tagtag.module.iam.entity.User;

import java.util.List;

public class UserConvert {

    /** 将实体转换为DTO（使用 common BeanUtils 同名属性拷贝） */
    public static UserDTO toDTO(User entity) {
        if (entity == null) return null;
        UserDTO dto = new UserDTO();
        BeanUtils.copyTo(entity, dto);
        return dto;
    }

    /** 将DTO转换为实体（使用 common BeanUtils 同名属性拷贝） */
    public static User toEntity(UserDTO dto) {
        if (dto == null) return null;
        User entity = new User();
        BeanUtils.copyTo(dto, entity);
        return entity;
    }

    /** 将实体列表转换为DTO列表（使用 Supplier 批量拷贝） */
    public static List<UserDTO> toDTOList(List<User> list) {
        return BeanUtils.copyList(list, UserDTO::new);
    }

    /** 用DTO更新实体（忽略源对象中的空值） */
    public static void mergeNonNull(UserDTO dto, User entity) {
        if (dto == null || entity == null) return;
        BeanUtils.copyNonNullTo(dto, entity);
    }
}
