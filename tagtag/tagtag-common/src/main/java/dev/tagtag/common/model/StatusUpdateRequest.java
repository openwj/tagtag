package dev.tagtag.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 通用状态更新请求对象
 */
@Data
@Accessors(chain = true)
public class StatusUpdateRequest {
    /**
     * 状态值（0=禁用，1=启用）
     */
    private int status;
}
