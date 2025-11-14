package dev.tagtag.kernel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimit {

    /** 是否启用限流 */
    boolean enabled() default true;

    /** 唯一限流键（默认按方法签名与IP组合） */
    String key() default "";

    /** 周期秒数 */
    int periodSeconds() default 60;

    /** 周期内允许的请求数 */
    int permits() default 10;

    /** 限流后的提示信息 */
    String message() default "请求过于频繁，请稍后再试";
}
