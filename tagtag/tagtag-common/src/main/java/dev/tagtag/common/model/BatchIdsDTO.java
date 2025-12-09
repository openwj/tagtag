package dev.tagtag.common.model;

import lombok.Data;
import lombok.experimental.Accessors;
import java.util.List;

/**
 * 通用批量ID请求对象
 */
@Data
@Accessors(chain = true)
public class BatchIdsDTO {
    /**
     * 要操作的ID列表
     */
    private List<Long> ids;
}

