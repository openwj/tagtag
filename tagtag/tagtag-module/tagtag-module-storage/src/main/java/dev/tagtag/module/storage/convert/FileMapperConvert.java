package dev.tagtag.module.storage.convert;

import org.mapstruct.Mapper;
import dev.tagtag.framework.mapstruct.MapStructConfig;
import dev.tagtag.module.storage.entity.FileResource;
import dev.tagtag.contract.storage.dto.FileDTO;

/**
 * 文件实体与DTO转换器（MapStruct）
 */
@Mapper(config = MapStructConfig.class)
public interface FileMapperConvert {
    /**
     * 将文件实体转换为DTO
     * @param entity 文件实体
     * @return 文件DTO
     */
    FileDTO toDTO(FileResource entity);
}

