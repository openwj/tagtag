package dev.tagtag.framework.aop;

import dev.tagtag.common.exception.BusinessException;
import dev.tagtag.common.exception.ErrorCode;
import dev.tagtag.kernel.annotation.RateLimit;
import dev.tagtag.kernel.constant.CacheConstants;
import dev.tagtag.kernel.constant.SecurityConstants;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Collections;
import java.util.List;

@Aspect
@Component
@Order(Integer.MIN_VALUE + 20)
public class RateLimitAspect {

    private static final int MIN_PERIOD_SECONDS = 1;
    private static final int MIN_PERMITS = 1;
    private static final String UNKNOWN_IP = "unknown";

    private final StringRedisTemplate stringRedisTemplate;
    private final DefaultRedisScript<Long> rateLimitScript;

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

    @Around("@annotation(limit)")
    public Object around(ProceedingJoinPoint pjp, RateLimit limit) throws Throwable {
        if (limit == null || !limit.enabled()) {
            return pjp.proceed();
        }

        String key = buildKey(pjp, limit);
        int period = Math.max(limit.periodSeconds(), MIN_PERIOD_SECONDS);
        int permits = Math.max(limit.permits(), MIN_PERMITS);

        try {
            List<String> keys = Collections.singletonList(key);
            Long allowed = stringRedisTemplate.execute(
                    rateLimitScript,
                    keys,
                    String.valueOf(period),
                    String.valueOf(permits)
            );
            if (allowed == 0L) {
                throw BusinessException.of(ErrorCode.TOO_MANY_REQUESTS, limit.message());
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

        String ip = resolveClientIp();
        MethodSignature ms = (MethodSignature) pjp.getSignature();
        String sig = ms.getDeclaringTypeName() + "#" + ms.getName();
        return CacheConstants.RATE_LIMIT_KEY_PREFIX + sig + ":" + (ip == null ? UNKNOWN_IP : ip);
    }

    private String resolveClientIp() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) {
                return null;
            }
            HttpServletRequest req = attrs.getRequest();
            String xff = req.getHeader(SecurityConstants.X_FORWARDED_FOR);
            if (StringUtils.hasText(xff)) {
                int idx = xff.indexOf(',');
                return idx > 0 ? xff.substring(0, idx).trim() : xff.trim();
            }
            String rip = req.getHeader(SecurityConstants.X_REAL_IP);
            if (StringUtils.hasText(rip)) {
                return rip.trim();
            }
            return req.getRemoteAddr();
        } catch (Exception e) {
            return null;
        }
    }
}