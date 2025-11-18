package dev.tagtag.framework.mapstruct;

import org.mapstruct.Builder;
import org.mapstruct.MapperConfig;
import org.mapstruct.ReportingPolicy;

/**
 * MapStruct 全局配置：统一 Spring 组件模型、忽略未映射字段、禁用构建器方式
 */
@MapperConfig(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        builder = @Builder(disableBuilder = true)
)
public interface MapStructConfig {
}