package dev.tagtag.framework.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

/**
 * 操作日志切面
 */
@Aspect
@Component
public class OperationLogAspect {

    /**
     * 切入点：Controller 层公共方法
     */
    @Pointcut("execution(public * dev.tagtag..controller..*(..))")
    public void controllerMethods() {}

    /**
     * 记录方法返回后的基本信息（简化版示例）
     * @param joinPoint 连接点
     * @param ret 返回值
     */
    @AfterReturning(pointcut = "controllerMethods()", returning = "ret")
    public void afterReturn(JoinPoint joinPoint, Object ret) {
        // 这里可以接入实际日志系统，目前仅示例，不输出具体日志
    }
}
