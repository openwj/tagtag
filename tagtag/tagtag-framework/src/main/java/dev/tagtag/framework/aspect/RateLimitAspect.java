package dev.tagtag.framework.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 接口限流切面（占位，实际限流依赖 Redis 或令牌桶实现）
 */
@Aspect
@Component
public class RateLimitAspect {

    /**
     * 切入点：Controller 层公共方法
     */
    @Pointcut("execution(public * dev.tagtag..controller..*(..))")
    public void rateLimitPointcut() {}

    /**
     * 环绕控制（示例中直接放行）
     * @param pjp 连接点
     * @return 方法返回
     * @throws Throwable 异常
     */
    @Around("rateLimitPointcut()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        return pjp.proceed();
    }
}
