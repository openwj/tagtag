package dev.tagtag.common.model;

import lombok.Data;
import lombok.experimental.Accessors;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@Data
@Accessors(chain = true)
public class BatchStatusUpdateRequest {
    @NotEmpty(message = "ID列表不能为空")
    private List<Long> ids;
    
    @NotNull(message = "状态值不能为空")
    private int status;
}
