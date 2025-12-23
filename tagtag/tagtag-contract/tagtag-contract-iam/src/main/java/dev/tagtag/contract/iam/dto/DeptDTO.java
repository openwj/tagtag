package dev.tagtag.contract.iam.dto;

import dev.tagtag.common.validation.CreateGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.PositiveOrZero;


import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeptDTO {
    private Long id;
    @NotBlank(message = "部门名称不能为空", groups = CreateGroup.class)
    @Size(max = 64, message = "部门名称长度不能超过64")
    private String name;
    @NotBlank(message = "部门编码不能为空", groups = CreateGroup.class)
    @Size(max = 64, message = "部门编码长度不能超过64")
    private String code;
    private Long parentId;
    @PositiveOrZero(message = "排序值不能为负数")
    private Integer sort;
    private Integer status;
    @Size(max = 64, message = "负责人长度不能超过64")
    private String leader;
    @Size(max = 32, message = "联系电话长度不能超过32")
    private String phone;
    @Size(max = 128, message = "邮箱长度不能超过128")
    private String email;
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;
    private LocalDateTime updateTime;
    private LocalDateTime createTime;
    private List<DeptDTO> children;
}

