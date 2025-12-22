package dev.tagtag.framework.aop;

import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.kernel.annotation.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * AOP 限流切面：支持基于 @RateLimit 的方法级限流，使用 Redis 计数与过期实现滑窗限流。
 */
@Aspect
@Component
@Order(Integer.MIN_VALUE + 20)
public class RateLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> rateLimitScript;
    private static final String KEY_PREFIX = "rl:";

    /**
     * 构造函数：注入 Redis 模板
     * @param stringRedisTemplate Redis 模板
     */
    public RateLimitAspect(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.rateLimitScript = new DefaultRedisScript<>();
        this.rateLimitScript.setScriptText(
                "local current = redis.call('INCR', KEYS[1]);" +
                "if current == 1 then redis.call('EXPIRE', KEYS[1], tonumber(ARGV[1])); end;" +
                "if current > tonumber(ARGV[2]) then return 0 else return 1 end"
        );
        this.rateLimitScript.setResultType(Long.class);
    }

    /**
     * 环绕通知：当方法标注 @RateLimit 并启用时，执行滑窗限流校验，超过阈值抛出 429 业务异常。
     * @param pjp 连接点
     * @param limit 注解配置
     * @return 方法返回值
     * @throws Throwable 原方法抛出的异常或限流异常
     */
    @Around("@annotation(limit)")
    public Object around(ProceedingJoinPoint pjp, RateLimit limit) throws Throwable {
        if (limit == null || !limit.enabled()) return pjp.proceed();
        String key = buildKey(pjp, limit);
        int period = Math.max(limit.periodSeconds(), 1);
        int permits = Math.max(limit.permits(), 1);
        try {
            List<String> keys = Collections.singletonList(key);
            Long allowed = stringRedisTemplate.execute(
                    rateLimitScript,
                    keys,
                    String.valueOf(period),
                    String.valueOf(permits)
            );
            if (allowed == 0L) {
                throw new BusinessException(ErrorCode.TOO_MANY_REQUESTS, limit.message());
            }
        } catch (BusinessException be) {
            throw be;
        } catch (RuntimeException ignored) {
        }
        return pjp.proceed();
    }

    /**
     * 生成限流键：优先使用注解 key；否则按 方法签名 + 客户端IP 组合。
     * @param pjp 连接点
     * @param limit 注解配置
     * @return 键名
     */
    private String buildKey(ProceedingJoinPoint pjp, RateLimit limit) {
        String key = limit.key();
        if (StringUtils.hasText(key)) {
            return KEY_PREFIX + key.trim();
        }
        String ip = resolveClientIp();
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        String sig = ms.getDeclaringTypeName() + "#" + ms.getName();
        return KEY_PREFIX + sig + ":" + (ip == null ? "unknown" : ip);
    }

    /**
     * 解析客户端 IP（优先使用反向代理头部）
     * @return 客户端 IP
     */
    private String resolveClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return null;
            HttpServletRequest req = attrs.getRequest();
            String xff = req.getHeader("X-Forwarded-For");
            if (StringUtils.hasText(xff)) {
                int idx = xff.indexOf(',');
                return idx > 0 ? xff.substring(0, idx).trim() : xff.trim();
            }
            String rip = req.getHeader("X-Real-IP");
            if (StringUtils.hasText(rip)) return rip.trim();
            return req.getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }
}