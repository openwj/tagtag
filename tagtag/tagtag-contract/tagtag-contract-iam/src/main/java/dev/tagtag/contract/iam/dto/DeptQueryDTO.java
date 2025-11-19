package dev.tagtag.contract.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import dev.tagtag.common.enums.StatusEnum;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptQueryDTO {
    private String name;
    private StatusEnum status;
    private Long parentId;
    private String code;
}
