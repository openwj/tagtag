package dev.tagtag.contract.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptDTO {
    private Long id;
    @NotBlank(message = "部门名称不能为空")
    @Size(max = 64, message = "部门名称长度不能超过64")
    private String name;
    private Long parentId;
    @PositiveOrZero(message = "排序值不能为负数")
    private Integer sort;
    private Integer status;
    private LocalDateTime createTime;
    private java.util.List<DeptDTO> children;
}
