package dev.tagtag.contract.iam.dto;

import dev.tagtag.common.validation.CreateGroup;
import dev.tagtag.common.validation.UpdateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    @NotBlank(message = "用户名不能为空", groups = {CreateGroup.class})
    @Size(min = 3, max = 50, message = "用户名长度必须在3-50之间", groups = {CreateGroup.class})
    private String username;
    @Size(min = 6, max = 100, message = "密码长度必须在6-100之间", groups = {CreateGroup.class})
    private String password;
    private String nickname;
    private String email;
    private String phone;
    private Integer gender;
    private Long deptId;
    private Integer status;
    private Integer isAdmin;
    private List<Long> roleIds;
    private LocalDateTime createTime;
    private String avatar;
    private String remark;
    private String employeeNo;
    private String position;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate entryDate;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private LocalDateTime passwordUpdatedAt;
    private String deptName;
}
