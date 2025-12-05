package dev.tagtag.contract.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptQueryDTO {
    private String name;
    private Integer status;
    private Long parentId;
    private String code;
}
