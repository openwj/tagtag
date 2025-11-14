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
public class RoleQueryDTO {
    private String code;
    private String name;
    private StatusEnum status;
}
