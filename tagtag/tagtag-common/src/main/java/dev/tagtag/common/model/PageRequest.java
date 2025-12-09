package dev.tagtag.common.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * 通用分页请求体：统一承载查询条件与分页参数
 * @param <Q> 查询条件类型
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PageRequest<Q>(
        Q query,
        PageQuery page
) {}

