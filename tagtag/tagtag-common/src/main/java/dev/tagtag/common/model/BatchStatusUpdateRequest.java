package dev.tagtag.common.model;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * 通用批量状态更新请求对象
 */
@Data
@Accessors(chain = true)
public class BatchStatusUpdateRequest {
    /**
     * 要操作的ID列表
     */
    private List<Long> ids;
    
    /**
     * 状态值（0=禁用，1=启用）
     */
    private int status;
}
