package dev.tagtag.kernel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataPermission {

    /** 数据权限是否启用 */
    boolean enabled() default true;

    /** 部门字段名（用于拼接数据范围） */
    String deptField() default "dept_id";

    /** 用户字段名（用于拼接个人数据） */
    String userField() default "user_id";

    /** 是否包含本人数据 */
    boolean includeSelf() default true;
}
