package dev.tagtag.contract.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String username;
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
    private LocalDate birthday;
    private LocalDate entryDate;
    private LocalDateTime lastLoginTime;
    private String lastLoginIp;
    private LocalDateTime passwordUpdatedAt;
    private String deptName;
}
