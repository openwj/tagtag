package dev.tagtag.contract.storage.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 批量ID请求对象
 */
@Data
@Accessors(chain = true)
public class BatchIdsDTO {
    private List<Long> ids;
}

