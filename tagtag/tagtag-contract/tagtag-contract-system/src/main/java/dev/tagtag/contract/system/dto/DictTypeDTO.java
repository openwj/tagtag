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
public class DictTypeDTO {

    private Long id;
    private String code;
    private String name;
    private Integer status;
    private String remark;
    private LocalDateTime createTime;
}
