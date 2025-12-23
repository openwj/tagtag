package dev.tagtag.framework.security.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

/** 声明方法访问所需的业务权限码（不含前缀） */
@Documented
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequirePerm {
    /** 指定业务权限码常量（如 Permissions.USER_READ） */
    String value();
}
