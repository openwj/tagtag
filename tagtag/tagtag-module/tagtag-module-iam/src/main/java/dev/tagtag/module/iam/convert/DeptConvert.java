package dev.tagtag.module.iam.convert;

import dev.tagtag.common.util.BeanUtils;
import dev.tagtag.contract.iam.dto.DeptDTO;
import dev.tagtag.module.iam.entity.Dept;

import java.util.List;

public class DeptConvert {

    /** 将实体转换为DTO（使用 common BeanUtils 同名属性拷贝） */
    public static DeptDTO toDTO(Dept entity) {
        if (entity == null) return null;
        DeptDTO dto = new DeptDTO();
        BeanUtils.copyTo(entity, dto);
        return dto;
    }

    /** 将DTO转换为实体（使用 common BeanUtils 同名属性拷贝） */
    public static Dept toEntity(DeptDTO dto) {
        if (dto == null) return null;
        Dept entity = new Dept();
        BeanUtils.copyTo(dto, entity);
        return entity;
    }

    /** 将实体列表转换为DTO列表（使用 Supplier 批量拷贝） */
    public static List<DeptDTO> toDTOList(List<Dept> list) {
        return BeanUtils.copyList(list, DeptDTO::new);
    }

    /** 用DTO更新实体（忽略源对象中的空值） */
    public static void mergeNonNull(DeptDTO dto, Dept entity) {
        if (dto == null || entity == null) return;
        BeanUtils.copyNonNullTo(dto, entity);
    }
}
