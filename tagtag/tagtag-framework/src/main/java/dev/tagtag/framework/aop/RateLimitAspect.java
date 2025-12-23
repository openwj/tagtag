package dev.tagtag.framework.aop;

import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.common.constant.CacheConstants;
import dev.tagtag.framework.constant.RateLimitConstants;
import dev.tagtag.framework.util.IpUtils;
import dev.tagtag.kernel.annotation.RateLimit;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;

@Aspect
@Component
@Order(Integer.MIN_VALUE + 20)
public class RateLimitAspect {

    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> rateLimitScript;

    public RateLimitAspect(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.rateLimitScript = new DefaultRedisScript<>();
        this.rateLimitScript.setScriptText(RateLimitConstants.RATE_LIMIT_SCRIPT);
        this.rateLimitScript.setResultType(Long.class);
    }

    @Around("@annotation(limit)")
    public Object around(ProceedingJoinPoint pjp, RateLimit limit) throws Throwable {
        if (limit == null || !limit.enabled()) {
            return pjp.proceed();
        }

        String key = buildKey(pjp, limit);
        int period = Math.max(limit.periodSeconds(), RateLimitConstants.MIN_PERIOD_SECONDS);
        int permits = Math.max(limit.permits(), RateLimitConstants.MIN_PERMITS);

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

    private String buildKey(ProceedingJoinPoint pjp, RateLimit limit) {
        String key = limit.key();
        if (StringUtils.hasText(key)) {
            return CacheConstants.RATE_LIMIT_KEY_PREFIX + key.trim();
        }

        String ip = IpUtils.resolveClientIp();
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        String sig = ms.getDeclaringTypeName() + "#" + ms.getName();
        return CacheConstants.RATE_LIMIT_KEY_PREFIX + sig + ":" + (ip == null ? RateLimitConstants.UNKNOWN_IP : ip);
    }
}
