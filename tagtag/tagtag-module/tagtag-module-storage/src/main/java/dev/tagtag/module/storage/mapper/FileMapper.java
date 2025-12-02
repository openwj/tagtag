package dev.tagtag.module.storage.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import dev.tagtag.module.storage.entity.FileResource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件资源 Mapper
 */
@Mapper
public interface FileMapper extends BaseMapper<FileResource> {
}

