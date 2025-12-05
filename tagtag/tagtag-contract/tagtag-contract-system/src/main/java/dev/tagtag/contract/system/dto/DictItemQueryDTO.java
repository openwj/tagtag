package dev.tagtag.contract.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictItemQueryDTO {
    private String typeCode;
    private String itemCode;
    private String itemName;
    private Integer status;
}
