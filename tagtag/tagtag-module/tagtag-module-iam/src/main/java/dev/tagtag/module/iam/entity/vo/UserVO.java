package dev.tagtag.module.iam.entity.vo;

import dev.tagtag.module.iam.entity.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页/视图对象，扩展部门名称等非持久化字段
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserVO extends User {
    private String deptName;
}

