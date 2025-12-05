package dev.tagtag.contract.system.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DictItemDTO {

    private Long id;
    private String typeCode;
    private String itemCode;
    private String itemName;
    private Integer sort;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
