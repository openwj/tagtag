package dev.tagtag.kernel.annotation;

import dev.tagtag.kernel.enums.LogTypeEnum;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {

    /** 日志标题 */
    String value() default "";

    /** 日志类型 */
    LogTypeEnum type() default LogTypeEnum.OTHER;

    /** 是否记录请求体 */
    boolean saveRequestBody() default true;

    /** 是否记录响应体 */
    boolean saveResponseBody() default true;

    /** 标签（便于分类检索） */
    String[] tags() default {};

    /** 详细描述 */
    String detail() default "";
}
