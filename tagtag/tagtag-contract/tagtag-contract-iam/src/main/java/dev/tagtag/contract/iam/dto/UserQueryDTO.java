package dev.tagtag.contract.iam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import dev.tagtag.common.enums.GenderEnum;
import dev.tagtag.common.enums.StatusEnum;
import dev.tagtag.common.model.TimeRangeDTO;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserQueryDTO {
    private String username;
    private String nickname;
    private StatusEnum status;
    private GenderEnum gender;
    private Long roleId;
    private String email;
    private String mobile;
    private TimeRangeDTO createTimeRange;
    private Long deptId;
    private String employeeNo;
    private TimeRangeDTO entryDateRange;
}

