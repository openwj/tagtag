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
public class SystemConfigDTO {

    private Long id;
    private String key;
    private String value;
    private String name;
    private String description;
    private String group;
    private Integer type;
    private Integer status;
    private Boolean editable;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
