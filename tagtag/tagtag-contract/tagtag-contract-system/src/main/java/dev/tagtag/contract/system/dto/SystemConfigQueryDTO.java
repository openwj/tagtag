package dev.tagtag.contract.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import dev.tagtag.kernel.enums.StatusEnum;
import dev.tagtag.common.model.TimeRangeDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemConfigQueryDTO {
    private String group;
    private String key;
    private String name;
    private StatusEnum status;
    private Boolean editable;
    private TimeRangeDTO createTimeRange;
}
