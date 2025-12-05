package dev.tagtag.contract.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import dev.tagtag.common.model.TimeRangeDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryDTO {
    private String username;
    private String nickname;
    private Integer status;
    private Integer gender;
    private Long roleId;
    private String email;
    private String phone;
    private TimeRangeDTO createTimeRange;
    private Long deptId;
    private String employeeNo;
    private TimeRangeDTO entryDateRange;
}

