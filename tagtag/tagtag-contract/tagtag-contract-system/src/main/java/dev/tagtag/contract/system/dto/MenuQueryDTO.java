package dev.tagtag.contract.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import dev.tagtag.common.enums.StatusEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuQueryDTO {
    private String name;
    private StatusEnum status;
    private Long parentId;
}
